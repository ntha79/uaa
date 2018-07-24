package com.hdmon.uaa.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdmon.uaa.domain.IsoResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by UserName on 7/23/2018.
 */
public class MicroserviceHelper {
    private final static Logger log = LoggerFactory.getLogger(MicroserviceHelper.class);

    /**
     * Gọi sang otp service để tạo mã otp.
     * Last update date: 23-07-2018
     * @param mobile mobile của thành viên đăng ký.
     * @param deviceId id của thiết bị hiện thời.
     * @param typeOtp phân loại otp.
     */
    public static String createOtpQueues(String otpApiUrl,  String mobile, String typeOtp, String deviceId)
    {
        String resOtpCode = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();
            String authenCode = UUID.randomUUID().toString();

            HttpHeaders reqHeaders = new HttpHeaders();
            reqHeaders.add("X-XSRF-TOKEN", authenCode);
            reqHeaders.add("Cookie", "XSRF-TOKEN=" + authenCode);
            reqHeaders.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> params = new HashMap<>();
            params.put("mobile", mobile);
            params.put("typeOtp", typeOtp);
            params.put("deviceId", deviceId);
            String jsonBody = mapper.writeValueAsString(params);

            HttpEntity<String> httpEntity = new HttpEntity<>(jsonBody, reqHeaders);
            String requestUrl = otpApiUrl + "/api/hd/spmotpqueues/create";
            ResponseEntity<IsoResponseEntity> responseEntity = restTemplate.postForEntity(requestUrl, httpEntity, IsoResponseEntity.class);

            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                log.debug("failed to create otp on Otp Service, status: {}", responseEntity.getStatusCodeValue());
            } else {
                if (responseEntity.getBody().getError() == 1) {
                    resOtpCode = responseEntity.getBody().getData().toString();
                }
            }
        }
        catch (Exception ex)
        {
            log.info("Loi ghi trong ham MicroserviceHelper.createOtpQueues(token, {},{},{})", mobile, deviceId, typeOtp);
            log.error("{}", ex);
        }
        return resOtpCode;
    }

    /**
     * Gọi sang otp service để kiểm tra lại mã otp lần cuối.
     * Last update date: 24-07-2018
     * @param mobile mobile của thành viên đăng ký.
     * @param typeOtp phân loại otp.
     * @param otpCode mã otp nhận đã nhận được.
     * @param action hành động tương ứng: 1 check lần đầu, 2 check lần cuối.
     */
    public static boolean checkOtpCodeExists(String otpApiUrl, String mobile, String typeOtp, String otpCode, Integer action)
    {
        boolean resResult = false;
        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();
            String authenCode = UUID.randomUUID().toString();

            HttpHeaders reqHeaders = new HttpHeaders();
            reqHeaders.add("X-XSRF-TOKEN", authenCode);
            reqHeaders.add("Cookie", "XSRF-TOKEN=" + authenCode);
            reqHeaders.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> params = new HashMap<>();
            params.put("mobile", mobile);
            params.put("otp", otpCode);
            params.put("typeOtp", typeOtp);
            params.put("action", action.toString());
            String jsonBody = mapper.writeValueAsString(params);

            HttpEntity<String> httpEntity = new HttpEntity<>(jsonBody, reqHeaders);
            String requestUrl = otpApiUrl + "/api/hd/spmotpqueues/checkexists";
            ResponseEntity<IsoResponseEntity> responseEntity = restTemplate.postForEntity(requestUrl, httpEntity, IsoResponseEntity.class);

            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                log.debug("failed to create otp on Otp Service, status: {}", responseEntity.getStatusCodeValue());
            } else {
                if (responseEntity.getBody().getError() == 1) {
                    resResult = (boolean)responseEntity.getBody().getData();
                }
            }
        }
        catch (Exception ex)
        {
            log.info("Loi ghi trong ham MicroserviceHelper.checkOtpCodeExists(token, {},{},{},{})", mobile, typeOtp, otpCode, action);
            log.error("{}", ex);
        }
        return resResult;
    }

    /**
     * Gọi sang otp service để kiểm tra và mã otp gần nhất để tránh bị spam.
     * Last update date: 24-07-2018
     * @param mobile mobile của thành viên đăng ký.
     * @param typeOtp phân loại otp.
     */
    public static String getNewestOtpCode(String otpApiUrl, String mobile, String typeOtp)
    {
        String resResult = "";
        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();
            String authenCode = UUID.randomUUID().toString();

            HttpHeaders reqHeaders = new HttpHeaders();
            reqHeaders.add("X-XSRF-TOKEN", authenCode);
            reqHeaders.add("Cookie", "XSRF-TOKEN=" + authenCode);
            reqHeaders.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> params = new HashMap<>();
            params.put("mobile", mobile);
            params.put("typeOtp", typeOtp);
            String jsonBody = mapper.writeValueAsString(params);

            HttpEntity<String> httpEntity = new HttpEntity<>(jsonBody, reqHeaders);
            String requestUrl = otpApiUrl + "/api/hd/spmotpqueues/getnewotp";
            ResponseEntity<IsoResponseEntity> responseEntity = restTemplate.postForEntity(requestUrl, httpEntity, IsoResponseEntity.class);

            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                log.debug("failed to create otp on Otp Service, status: {}", responseEntity.getStatusCodeValue());
            } else {
                if (responseEntity.getBody().getError() == 1) {
                    resResult = responseEntity.getBody().getData().toString();
                }
            }
        }
        catch (Exception ex)
        {
            log.info("Loi ghi trong ham MicroserviceHelper.getNewestOtpCode(token, {},{})", mobile, typeOtp);
            log.error("{}", ex);
        }
        return resResult;
    }
}
