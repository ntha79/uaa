package com.hdmon.uaa.web.rest;

import com.codahale.metrics.annotation.Timed;

import com.hdmon.uaa.domain.IsoResponseEntity;
import com.hdmon.uaa.domain.User;
import com.hdmon.uaa.repository.UserRepository;
import com.hdmon.uaa.security.SecurityUtils;
import com.hdmon.uaa.service.MailService;
import com.hdmon.uaa.service.UserService;
import com.hdmon.uaa.service.dto.UserDTO;
import com.hdmon.uaa.service.util.MicroserviceHelper;
import com.hdmon.uaa.web.rest.errors.*;
import com.hdmon.uaa.web.rest.util.HeaderUtil;
import com.hdmon.uaa.web.rest.vm.KeyAndPasswordVM;
import com.hdmon.uaa.web.rest.vm.ManagedUserVM;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    public AccountResource(UserRepository userRepository, UserService userService, MailService mailService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
    }

    /**
     * POST  /register : register the user.
     *
     * @param managedUserVM the managed user View Model
     * @throws InvalidPasswordException 400 (Bad Request) if the password is incorrect
     * @throws EmailAlreadyUsedException 400 (Bad Request) if the email is already used
     * @throws LoginAlreadyUsedException 400 (Bad Request) if the login is already used
     */
    @PostMapping("/register")
    @Timed
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        if (!checkPasswordLength(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase()).ifPresent(u -> {throw new LoginAlreadyUsedException();});
        userRepository.findOneByEmailIgnoreCase(managedUserVM.getEmail()).ifPresent(u -> {throw new EmailAlreadyUsedException();});
        User user = userService.registerUser(managedUserVM, managedUserVM.getPassword());
        mailService.sendActivationEmail(user);
    }

    /**
     * GET  /activate : activate the registered user.
     *
     * @param key the activation key
     * @throws RuntimeException 500 (Internal Server Error) if the user couldn't be activated
     */
    @GetMapping("/activate")
    @Timed
    public void activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new InternalServerErrorException("No user was found for this reset key");
        }
    }

    /**
     * GET  /authenticate : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request
     * @return the login if the user is authenticated
     */
    @GetMapping("/authenticate")
    @Timed
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * GET  /account : get the current user.
     *
     * @return the current user
     * @throws RuntimeException 500 (Internal Server Error) if the user couldn't be returned
     */
    @GetMapping("/account")
    @Timed
    public UserDTO getAccount() {
        return userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new InternalServerErrorException("User could not be found"));
    }

    /**
     * POST  /account : update the current user information.
     *
     * @param userDTO the current user information
     * @throws EmailAlreadyUsedException 400 (Bad Request) if the email is already used
     * @throws RuntimeException 500 (Internal Server Error) if the user login wasn't found
     */
    @PostMapping("/account")
    @Timed
    public void saveAccount(@Valid @RequestBody UserDTO userDTO) {
        final String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new InternalServerErrorException("Current user login not found"));
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException();
        }
        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (!user.isPresent()) {
            throw new InternalServerErrorException("User could not be found");
        }
        userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
            userDTO.getMobile(), userDTO.getLangKey(), userDTO.getImageUrl());
   }

    /**
     * POST  /account/change-password : changes the current user's password
     *
     * @param password the new password
     * @throws InvalidPasswordException 400 (Bad Request) if the new password is incorrect
     */
    @PostMapping(path = "/account/change-password")
    @Timed
    public void changePassword(@RequestBody String password) {
        if (!checkPasswordLength(password)) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(password);
   }

    /**
     * POST   /account/reset-password/init : Send an email to reset the password of the user
     *
     * @param mail the mail of the user
     * @throws EmailNotFoundException 400 (Bad Request) if the email address is not registered
     */
    @PostMapping(path = "/account/reset-password/init")
    @Timed
    public void requestPasswordReset(@RequestBody String mail) {
       mailService.sendPasswordResetMail(
           userService.requestPasswordReset(mail)
               .orElseThrow(EmailNotFoundException::new)
       );
    }

    /**
     * POST   /account/reset-password/finish : Finish to reset the password of the user
     *
     * @param keyAndPassword the generated key and the new password
     * @throws InvalidPasswordException 400 (Bad Request) if the password is incorrect
     * @throws RuntimeException 500 (Internal Server Error) if the password could not be reset
     */
    @PostMapping(path = "/account/reset-password/finish")
    @Timed
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<User> user =
            userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            throw new InternalServerErrorException("No user was found for this reset key");
        }
    }

    private static boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
            password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
            password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
    }

    //=========================================HDMON-START=========================================

    /**
     * POST  /hd/account/register : đăng ký thành viên.
     * Last update date: 23-07-2018
     * @return cấu trúc json, báo kết quả lấy được.
     */
    @RequestMapping(value = "/hd/account/register", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<IsoResponseEntity> createUser_hd(HttpServletRequest request, HttpServletResponse response, @RequestBody
        Map<String, String> params)
    {
        log.debug("REST request to check create new User.");

        IsoResponseEntity<User> responseEntity = new IsoResponseEntity<>();
        HttpHeaders httpHeaders;
        try {
            String prUsername = params.get("username");
            String prMobile = params.get("mobile");
            String prOtpCode = params.get("otpCode");
            String prPassword = params.get("password");
            String prCountryCode = params.get("countryCode");
            String prClientType = params.get("clientType");

            if(!prUsername.isEmpty() && !prMobile.isEmpty() && !prOtpCode.isEmpty() && !prPassword.isEmpty()) {
                prCountryCode = prCountryCode.isEmpty() ? "84": prCountryCode;
                prClientType = prClientType.isEmpty() ? "WEB" : prClientType;

                User newUser = userService.execCreateUser_hd(prUsername, prMobile, prOtpCode, prPassword, prCountryCode, prClientType, responseEntity);
                if (newUser != null) {
                    responseEntity.setError(ResponseErrorCode.SUCCESSFULL.getValue());
                    responseEntity.setData(newUser);
                    responseEntity.setMessage("successfull");

                    httpHeaders = HeaderUtil.createEntityCreationAlert(ENTITY_NAME, "successfull");
                } else if (responseEntity.getError() == ResponseErrorCode.UNKNOW_ERROR.getValue()) {
                    responseEntity.setError(ResponseErrorCode.CREATEFAIL.getValue());
                    responseEntity.setData(null);
                    responseEntity.setMessage("fail");
                    httpHeaders = HeaderUtil.createFailureAlert(ENTITY_NAME, "fail", "An error occurred while create new User!");
                } else {
                    httpHeaders = HeaderUtil.createFailureAlert(ENTITY_NAME, responseEntity.getMessage(), responseEntity.getException());
                }
            }
            else
            {
                responseEntity.setError(ResponseErrorCode.INVALIDDATA.getValue());
                responseEntity.setMessage("invalid");
                responseEntity.setException("The fields Username, Mobile, OtpCode, Password cannot not null!");
                httpHeaders = HeaderUtil.createFailureAlert(ENTITY_NAME, "invalid", "The fields Username, Mobile, OtpCode, Password cannot not null!");
            }
        }
        catch (Exception ex)
        {
            responseEntity.setError(ResponseErrorCode.SYSTEM_ERROR.getValue());
            responseEntity.setMessage("system_error");
            responseEntity.setException(String.format("%s", ex.getMessage()));

            httpHeaders = HeaderUtil.createFailureAlert(ENTITY_NAME, "system_error", ex.getMessage());
        }
        return new ResponseEntity<>(responseEntity, httpHeaders, HttpStatus.OK);
    }

    /**
     * POST /hd/account/checkoptexists : kiểm tra optcode đã tồn tại trong hệ thống chưa
     * không yêu cầu đăng nhập khi gọi.
     * Last update date: 24-07-2018
     * @return cấu trúc json, báo kết quả dữ liệu lấy được.
     */
    @RequestMapping(value = "/hd/account/checkoptexists", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<IsoResponseEntity> checkOtpCodeExists_hd(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> params) {
        log.debug("REST request to check exist Otp Code : {}", params);

        IsoResponseEntity responseEntity = new IsoResponseEntity();
        HttpHeaders httpHeaders;

        try {
            String prMobile = params.get("mobile");
            String prOtpCode = params.get("otpCode");

            if(!prMobile.isEmpty() && !prOtpCode.isEmpty()) {
                boolean resResult = userService.execCheckOtpExists_hd(prMobile, prOtpCode);

                responseEntity.setError(ResponseErrorCode.SUCCESSFULL.getValue());
                responseEntity.setData(resResult);
                responseEntity.setMessage("successfull");

                httpHeaders = HeaderUtil.createAlert(ENTITY_NAME, "successfull");
            }
            else
            {
                responseEntity.setError(ResponseErrorCode.INVALIDDATA.getValue());
                responseEntity.setMessage("invalid");
                responseEntity.setException("The fields Mobile, OtpCode cannot not null!");
                httpHeaders = HeaderUtil.createFailureAlert(ENTITY_NAME, "invalid", "The fields Mobile, OtpCode cannot not null!");
            }
        }
        catch (Exception ex)
        {
            responseEntity.setError(ResponseErrorCode.SYSTEM_ERROR.getValue());
            responseEntity.setMessage("system_error");
            responseEntity.setException(ex.getMessage());

            httpHeaders = HeaderUtil.createFailureAlert(ENTITY_NAME, "system_error", ex.getMessage());
        }

        return new ResponseEntity<>(responseEntity, httpHeaders, HttpStatus.OK);
    }

    /**
     * POST /hd/account/createoptcode : tạo mới mã otp
     * không yêu cầu đăng nhập khi gọi.
     * Last update date: 24-07-2018
     * @return cấu trúc json, báo kết quả dữ liệu lấy được.
     */
    @RequestMapping(value = "/hd/account/createoptcode", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<IsoResponseEntity> createOtpCode_hd(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> params) {
        log.debug("REST request to create new Otp Code : {}", params);

        IsoResponseEntity responseEntity = new IsoResponseEntity();
        HttpHeaders httpHeaders;

        try {
            String prMobile = params.get("mobile");

            if(!prMobile.isEmpty()) {
                String resOtpCode = userService.execCreateOtpCode_hd(prMobile);

                responseEntity.setError(ResponseErrorCode.SUCCESSFULL.getValue());
                responseEntity.setData(resOtpCode);
                responseEntity.setMessage("successfull");

                httpHeaders = HeaderUtil.createAlert(ENTITY_NAME, "successfull");
            }
            else
            {
                responseEntity.setError(ResponseErrorCode.INVALIDDATA.getValue());
                responseEntity.setMessage("invalid");
                responseEntity.setException("The field Mobile cannot not null!");
                httpHeaders = HeaderUtil.createFailureAlert(ENTITY_NAME, "invalid", "The field Mobile cannot not null!");
            }
        }
        catch (Exception ex)
        {
            responseEntity.setError(ResponseErrorCode.SYSTEM_ERROR.getValue());
            responseEntity.setMessage("system_error");
            responseEntity.setException(ex.getMessage());

            httpHeaders = HeaderUtil.createFailureAlert(ENTITY_NAME, "system_error", ex.getMessage());
        }

        return new ResponseEntity<>(responseEntity, httpHeaders, HttpStatus.OK);
    }

    //===========================================HDMON-END===========================================
}
