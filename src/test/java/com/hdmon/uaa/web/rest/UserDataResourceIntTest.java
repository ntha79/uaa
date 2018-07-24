package com.hdmon.uaa.web.rest;

import com.hdmon.uaa.UaaApp;

import com.hdmon.uaa.config.SecurityBeanOverrideConfiguration;

import com.hdmon.uaa.domain.UserData;
import com.hdmon.uaa.repository.UserDataRepository;
import com.hdmon.uaa.service.UserDataService;
import com.hdmon.uaa.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;


import static com.hdmon.uaa.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the UserDataResource REST controller.
 *
 * @see UserDataResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UaaApp.class)
public class UserDataResourceIntTest {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final Integer DEFAULT_GENDER = 1;
    private static final Integer UPDATED_GENDER = 2;

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final Long DEFAULT_BIRTHDAY = 1L;
    private static final Long UPDATED_BIRTHDAY = 2L;

    private static final String DEFAULT_ABOUT = "AAAAAAAAAA";
    private static final String UPDATED_ABOUT = "BBBBBBBBBB";

    private static final String DEFAULT_COVER_URL = "AAAAAAAAAA";
    private static final String UPDATED_COVER_URL = "BBBBBBBBBB";

    private static final String DEFAULT_SOURCE_PROVINCE = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE_PROVINCE = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENT_PROVINCE = "AAAAAAAAAA";
    private static final String UPDATED_CURRENT_PROVINCE = "BBBBBBBBBB";

    private static final Integer DEFAULT_MARRIAGE = 1;
    private static final Integer UPDATED_MARRIAGE = 2;

    private static final String DEFAULT_LIST_COMPANY = "AAAAAAAAAA";
    private static final String UPDATED_LIST_COMPANY = "BBBBBBBBBB";

    private static final String DEFAULT_LIST_SCHOOL = "AAAAAAAAAA";
    private static final String UPDATED_LIST_SCHOOL = "BBBBBBBBBB";

    private static final String DEFAULT_SLOGAN = "AAAAAAAAAA";
    private static final String UPDATED_SLOGAN = "BBBBBBBBBB";

    @Autowired
    private UserDataRepository userDataRepository;



    @Autowired
    private UserDataService userDataService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restUserDataMockMvc;

    private UserData userData;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final UserDataResource userDataResource = new UserDataResource(userDataService);
        this.restUserDataMockMvc = MockMvcBuilders.standaloneSetup(userDataResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserData createEntity(EntityManager em) {
        UserData userData = new UserData()
            .userId(DEFAULT_USER_ID)
            .gender(DEFAULT_GENDER)
            .address(DEFAULT_ADDRESS)
            .birthday(DEFAULT_BIRTHDAY)
            .about(DEFAULT_ABOUT)
            .coverUrl(DEFAULT_COVER_URL)
            .sourceProvince(DEFAULT_SOURCE_PROVINCE)
            .currentProvince(DEFAULT_CURRENT_PROVINCE)
            .marriage(DEFAULT_MARRIAGE)
            .listCompany(DEFAULT_LIST_COMPANY)
            .listSchool(DEFAULT_LIST_SCHOOL)
            .slogan(DEFAULT_SLOGAN);
        return userData;
    }

    @Before
    public void initTest() {
        userData = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserData() throws Exception {
        int databaseSizeBeforeCreate = userDataRepository.findAll().size();

        // Create the UserData
        restUserDataMockMvc.perform(post("/api/userdata")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userData)))
            .andExpect(status().isCreated());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeCreate + 1);
        UserData testUserData = userDataList.get(userDataList.size() - 1);
        assertThat(testUserData.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testUserData.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testUserData.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testUserData.getBirthday()).isEqualTo(DEFAULT_BIRTHDAY);
        assertThat(testUserData.getAbout()).isEqualTo(DEFAULT_ABOUT);
        assertThat(testUserData.getCoverUrl()).isEqualTo(DEFAULT_COVER_URL);
        assertThat(testUserData.getSourceProvince()).isEqualTo(DEFAULT_SOURCE_PROVINCE);
        assertThat(testUserData.getCurrentProvince()).isEqualTo(DEFAULT_CURRENT_PROVINCE);
        assertThat(testUserData.getMarriage()).isEqualTo(DEFAULT_MARRIAGE);
        assertThat(testUserData.getListCompany()).isEqualTo(DEFAULT_LIST_COMPANY);
        assertThat(testUserData.getListSchool()).isEqualTo(DEFAULT_LIST_SCHOOL);
        assertThat(testUserData.getSlogan()).isEqualTo(DEFAULT_SLOGAN);
    }

    @Test
    @Transactional
    public void createUserDataWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userDataRepository.findAll().size();

        // Create the UserData with an existing ID
        userData.setUserId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserDataMockMvc.perform(post("/api/userdata")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userData)))
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkUserIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = userDataRepository.findAll().size();
        // set the field null
        userData.setUserId(null);

        // Create the UserData, which fails.

        restUserDataMockMvc.perform(post("/api/userdata")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userData)))
            .andExpect(status().isBadRequest());

        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkGenderIsRequired() throws Exception {
        int databaseSizeBeforeTest = userDataRepository.findAll().size();
        // set the field null
        userData.setGender(null);

        // Create the UserData, which fails.

        restUserDataMockMvc.perform(post("/api/userdata")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userData)))
            .andExpect(status().isBadRequest());

        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllUserData() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList
        restUserDataMockMvc.perform(get("/api/userdata?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            //.andExpect(jsonPath("$.[*].id").value(hasItem(userData.getUserId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].birthday").value(hasItem(DEFAULT_BIRTHDAY.toString())))
            .andExpect(jsonPath("$.[*].about").value(hasItem(DEFAULT_ABOUT.toString())))
            .andExpect(jsonPath("$.[*].coverUrl").value(hasItem(DEFAULT_COVER_URL.toString())))
            .andExpect(jsonPath("$.[*].sourceProvince").value(hasItem(DEFAULT_SOURCE_PROVINCE.toString())))
            .andExpect(jsonPath("$.[*].currentProvince").value(hasItem(DEFAULT_CURRENT_PROVINCE.toString())))
            .andExpect(jsonPath("$.[*].marriage").value(hasItem(DEFAULT_MARRIAGE)))
            .andExpect(jsonPath("$.[*].listCompany").value(hasItem(DEFAULT_LIST_COMPANY.toString())))
            .andExpect(jsonPath("$.[*].listSchool").value(hasItem(DEFAULT_LIST_SCHOOL.toString())))
            .andExpect(jsonPath("$.[*].slogan").value(hasItem(DEFAULT_SLOGAN.toString())));
    }


    @Test
    @Transactional
    public void getUserData() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get the userData
        restUserDataMockMvc.perform(get("/api/userdata/{id}", userData.getUserId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            //.andExpect(jsonPath("$.id").value(userData.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS.toString()))
            .andExpect(jsonPath("$.birthday").value(DEFAULT_BIRTHDAY.toString()))
            .andExpect(jsonPath("$.about").value(DEFAULT_ABOUT.toString()))
            .andExpect(jsonPath("$.coverUrl").value(DEFAULT_COVER_URL.toString()))
            .andExpect(jsonPath("$.sourceProvince").value(DEFAULT_SOURCE_PROVINCE.toString()))
            .andExpect(jsonPath("$.currentProvince").value(DEFAULT_CURRENT_PROVINCE.toString()))
            .andExpect(jsonPath("$.marriage").value(DEFAULT_MARRIAGE))
            .andExpect(jsonPath("$.listCompany").value(DEFAULT_LIST_COMPANY.toString()))
            .andExpect(jsonPath("$.listSchool").value(DEFAULT_LIST_SCHOOL.toString()))
            .andExpect(jsonPath("$.slogan").value(DEFAULT_SLOGAN.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingUserData() throws Exception {
        // Get the userData
        restUserDataMockMvc.perform(get("/api/userdata/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserData() throws Exception {
        // Initialize the database
        userDataService.save(userData);

        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();

        // Update the userData
        UserData updatedUserData = userDataRepository.findOne(userData.getUserId());
        // Disconnect from session so that the updates on updatedUserData are not directly saved in db
        em.detach(updatedUserData);
        updatedUserData
            .userId(UPDATED_USER_ID)
            .gender(UPDATED_GENDER)
            .address(UPDATED_ADDRESS)
            .birthday(UPDATED_BIRTHDAY)
            .about(UPDATED_ABOUT)
            .coverUrl(UPDATED_COVER_URL)
            .sourceProvince(UPDATED_SOURCE_PROVINCE)
            .currentProvince(UPDATED_CURRENT_PROVINCE)
            .marriage(UPDATED_MARRIAGE)
            .listCompany(UPDATED_LIST_COMPANY)
            .listSchool(UPDATED_LIST_SCHOOL)
            .slogan(UPDATED_SLOGAN);

        restUserDataMockMvc.perform(put("/api/user-data")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedUserData)))
            .andExpect(status().isOk());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
        UserData testUserData = userDataList.get(userDataList.size() - 1);
        assertThat(testUserData.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testUserData.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testUserData.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testUserData.getBirthday()).isEqualTo(UPDATED_BIRTHDAY);
        assertThat(testUserData.getAbout()).isEqualTo(UPDATED_ABOUT);
        assertThat(testUserData.getCoverUrl()).isEqualTo(UPDATED_COVER_URL);
        assertThat(testUserData.getSourceProvince()).isEqualTo(UPDATED_SOURCE_PROVINCE);
        assertThat(testUserData.getCurrentProvince()).isEqualTo(UPDATED_CURRENT_PROVINCE);
        assertThat(testUserData.getMarriage()).isEqualTo(UPDATED_MARRIAGE);
        assertThat(testUserData.getListCompany()).isEqualTo(UPDATED_LIST_COMPANY);
        assertThat(testUserData.getListSchool()).isEqualTo(UPDATED_LIST_SCHOOL);
        assertThat(testUserData.getSlogan()).isEqualTo(UPDATED_SLOGAN);
    }

    @Test
    @Transactional
    public void updateNonExistingUserData() throws Exception {
        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();

        // Create the UserData

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restUserDataMockMvc.perform(put("/api/userdata")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userData)))
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteUserData() throws Exception {
        // Initialize the database
        userDataService.save(userData);

        int databaseSizeBeforeDelete = userDataRepository.findAll().size();

        // Get the userData
        restUserDataMockMvc.perform(delete("/api/userdata/{id}", userData.getUserId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserData.class);
        UserData userData1 = new UserData();
        userData1.setUserId(1L);
        UserData userData2 = new UserData();
        userData2.setUserId(userData1.getUserId());
        assertThat(userData1).isEqualTo(userData2);
        userData2.setUserId(2L);
        assertThat(userData1).isNotEqualTo(userData2);
        userData1.setUserId(null);
        assertThat(userData1).isNotEqualTo(userData2);
    }
}
