package com.hdmon.uaa.web.rest;

import com.hdmon.uaa.UaaApp;

import com.hdmon.uaa.config.SecurityBeanOverrideConfiguration;

import com.hdmon.uaa.domain.RegisterStatistics;
import com.hdmon.uaa.repository.RegisterStatisticsRepository;
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
import java.util.List;


import static com.hdmon.uaa.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the RegisterStatisticsResource REST controller.
 *
 * @see RegisterStatisticsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UaaApp.class)
public class RegisterStatisticsResourceIntTest {

    private static final Integer DEFAULT_CURRENT_DAY = 1;
    private static final Integer UPDATED_CURRENT_DAY = 2;

    private static final Integer DEFAULT_CURRENT_MONTH = 1;
    private static final Integer UPDATED_CURRENT_MONTH = 2;

    private static final Integer DEFAULT_CURRENT_YEAR = 1;
    private static final Integer UPDATED_CURRENT_YEAR = 2;

    private static final Integer DEFAULT_COUNT = 1;
    private static final Integer UPDATED_COUNT = 2;

    private static final Integer DEFAULT_COUNT_ANDROID = 1;
    private static final Integer UPDATED_COUNT_ANDROID = 2;

    private static final Integer DEFAULT_COUNT_IOS = 1;
    private static final Integer UPDATED_COUNT_IOS = 2;

    private static final Integer DEFAULT_COUNT_WEB = 1;
    private static final Integer UPDATED_COUNT_WEB = 2;

    @Autowired
    private RegisterStatisticsRepository registerStatisticsRepository;


    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restRegisterStatisticsMockMvc;

    private RegisterStatistics registerStatistics;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RegisterStatisticsResource registerStatisticsResource = new RegisterStatisticsResource(registerStatisticsRepository);
        this.restRegisterStatisticsMockMvc = MockMvcBuilders.standaloneSetup(registerStatisticsResource)
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
    public static RegisterStatistics createEntity(EntityManager em) {
        RegisterStatistics registerStatistics = new RegisterStatistics()
            .currentDay(DEFAULT_CURRENT_DAY)
            .currentMonth(DEFAULT_CURRENT_MONTH)
            .currentYear(DEFAULT_CURRENT_YEAR)
            .count(DEFAULT_COUNT)
            .countAndroid(DEFAULT_COUNT_ANDROID)
            .countIos(DEFAULT_COUNT_IOS)
            .countWeb(DEFAULT_COUNT_WEB);
        return registerStatistics;
    }

    @Before
    public void initTest() {
        registerStatistics = createEntity(em);
    }

    @Test
    @Transactional
    public void createRegisterStatistics() throws Exception {
        int databaseSizeBeforeCreate = registerStatisticsRepository.findAll().size();

        // Create the RegisterStatistics
        restRegisterStatisticsMockMvc.perform(post("/api/register-statistics")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(registerStatistics)))
            .andExpect(status().isCreated());

        // Validate the RegisterStatistics in the database
        List<RegisterStatistics> registerStatisticsList = registerStatisticsRepository.findAll();
        assertThat(registerStatisticsList).hasSize(databaseSizeBeforeCreate + 1);
        RegisterStatistics testRegisterStatistics = registerStatisticsList.get(registerStatisticsList.size() - 1);
        assertThat(testRegisterStatistics.getCurrentDay()).isEqualTo(DEFAULT_CURRENT_DAY);
        assertThat(testRegisterStatistics.getCurrentMonth()).isEqualTo(DEFAULT_CURRENT_MONTH);
        assertThat(testRegisterStatistics.getCurrentYear()).isEqualTo(DEFAULT_CURRENT_YEAR);
        assertThat(testRegisterStatistics.getCount()).isEqualTo(DEFAULT_COUNT);
        assertThat(testRegisterStatistics.getCountAndroid()).isEqualTo(DEFAULT_COUNT_ANDROID);
        assertThat(testRegisterStatistics.getCountIos()).isEqualTo(DEFAULT_COUNT_IOS);
        assertThat(testRegisterStatistics.getCountWeb()).isEqualTo(DEFAULT_COUNT_WEB);
    }

    @Test
    @Transactional
    public void createRegisterStatisticsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = registerStatisticsRepository.findAll().size();

        // Create the RegisterStatistics with an existing ID
        registerStatistics.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRegisterStatisticsMockMvc.perform(post("/api/register-statistics")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(registerStatistics)))
            .andExpect(status().isBadRequest());

        // Validate the RegisterStatistics in the database
        List<RegisterStatistics> registerStatisticsList = registerStatisticsRepository.findAll();
        assertThat(registerStatisticsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllRegisterStatistics() throws Exception {
        // Initialize the database
        registerStatisticsRepository.saveAndFlush(registerStatistics);

        // Get all the registerStatisticsList
        restRegisterStatisticsMockMvc.perform(get("/api/register-statistics?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(registerStatistics.getId().intValue())))
            .andExpect(jsonPath("$.[*].currentDay").value(hasItem(DEFAULT_CURRENT_DAY)))
            .andExpect(jsonPath("$.[*].currentMonth").value(hasItem(DEFAULT_CURRENT_MONTH)))
            .andExpect(jsonPath("$.[*].currentYear").value(hasItem(DEFAULT_CURRENT_YEAR)))
            .andExpect(jsonPath("$.[*].count").value(hasItem(DEFAULT_COUNT)))
            .andExpect(jsonPath("$.[*].countAndroid").value(hasItem(DEFAULT_COUNT_ANDROID)))
            .andExpect(jsonPath("$.[*].countIos").value(hasItem(DEFAULT_COUNT_IOS)))
            .andExpect(jsonPath("$.[*].countWeb").value(hasItem(DEFAULT_COUNT_WEB)));
    }


    @Test
    @Transactional
    public void getRegisterStatistics() throws Exception {
        // Initialize the database
        registerStatisticsRepository.saveAndFlush(registerStatistics);

        // Get the registerStatistics
        restRegisterStatisticsMockMvc.perform(get("/api/register-statistics/{id}", registerStatistics.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(registerStatistics.getId().intValue()))
            .andExpect(jsonPath("$.currentDay").value(DEFAULT_CURRENT_DAY))
            .andExpect(jsonPath("$.currentMonth").value(DEFAULT_CURRENT_MONTH))
            .andExpect(jsonPath("$.currentYear").value(DEFAULT_CURRENT_YEAR))
            .andExpect(jsonPath("$.count").value(DEFAULT_COUNT))
            .andExpect(jsonPath("$.countAndroid").value(DEFAULT_COUNT_ANDROID))
            .andExpect(jsonPath("$.countIos").value(DEFAULT_COUNT_IOS))
            .andExpect(jsonPath("$.countWeb").value(DEFAULT_COUNT_WEB));
    }
    @Test
    @Transactional
    public void getNonExistingRegisterStatistics() throws Exception {
        // Get the registerStatistics
        restRegisterStatisticsMockMvc.perform(get("/api/register-statistics/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRegisterStatistics() throws Exception {
        // Initialize the database
        registerStatisticsRepository.saveAndFlush(registerStatistics);

        int databaseSizeBeforeUpdate = registerStatisticsRepository.findAll().size();

        // Update the registerStatistics
        RegisterStatistics updatedRegisterStatistics = registerStatisticsRepository.findOne(registerStatistics.getId());
        // Disconnect from session so that the updates on updatedRegisterStatistics are not directly saved in db
        em.detach(updatedRegisterStatistics);
        updatedRegisterStatistics
            .currentDay(UPDATED_CURRENT_DAY)
            .currentMonth(UPDATED_CURRENT_MONTH)
            .currentYear(UPDATED_CURRENT_YEAR)
            .count(UPDATED_COUNT)
            .countAndroid(UPDATED_COUNT_ANDROID)
            .countIos(UPDATED_COUNT_IOS)
            .countWeb(UPDATED_COUNT_WEB);

        restRegisterStatisticsMockMvc.perform(put("/api/register-statistics")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedRegisterStatistics)))
            .andExpect(status().isOk());

        // Validate the RegisterStatistics in the database
        List<RegisterStatistics> registerStatisticsList = registerStatisticsRepository.findAll();
        assertThat(registerStatisticsList).hasSize(databaseSizeBeforeUpdate);
        RegisterStatistics testRegisterStatistics = registerStatisticsList.get(registerStatisticsList.size() - 1);
        assertThat(testRegisterStatistics.getCurrentDay()).isEqualTo(UPDATED_CURRENT_DAY);
        assertThat(testRegisterStatistics.getCurrentMonth()).isEqualTo(UPDATED_CURRENT_MONTH);
        assertThat(testRegisterStatistics.getCurrentYear()).isEqualTo(UPDATED_CURRENT_YEAR);
        assertThat(testRegisterStatistics.getCount()).isEqualTo(UPDATED_COUNT);
        assertThat(testRegisterStatistics.getCountAndroid()).isEqualTo(UPDATED_COUNT_ANDROID);
        assertThat(testRegisterStatistics.getCountIos()).isEqualTo(UPDATED_COUNT_IOS);
        assertThat(testRegisterStatistics.getCountWeb()).isEqualTo(UPDATED_COUNT_WEB);
    }

    @Test
    @Transactional
    public void updateNonExistingRegisterStatistics() throws Exception {
        int databaseSizeBeforeUpdate = registerStatisticsRepository.findAll().size();

        // Create the RegisterStatistics

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restRegisterStatisticsMockMvc.perform(put("/api/register-statistics")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(registerStatistics)))
            .andExpect(status().isBadRequest());

        // Validate the RegisterStatistics in the database
        List<RegisterStatistics> registerStatisticsList = registerStatisticsRepository.findAll();
        assertThat(registerStatisticsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteRegisterStatistics() throws Exception {
        // Initialize the database
        registerStatisticsRepository.saveAndFlush(registerStatistics);

        int databaseSizeBeforeDelete = registerStatisticsRepository.findAll().size();

        // Get the registerStatistics
        restRegisterStatisticsMockMvc.perform(delete("/api/register-statistics/{id}", registerStatistics.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<RegisterStatistics> registerStatisticsList = registerStatisticsRepository.findAll();
        assertThat(registerStatisticsList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RegisterStatistics.class);
        RegisterStatistics registerStatistics1 = new RegisterStatistics();
        registerStatistics1.setId(1L);
        RegisterStatistics registerStatistics2 = new RegisterStatistics();
        registerStatistics2.setId(registerStatistics1.getId());
        assertThat(registerStatistics1).isEqualTo(registerStatistics2);
        registerStatistics2.setId(2L);
        assertThat(registerStatistics1).isNotEqualTo(registerStatistics2);
        registerStatistics1.setId(null);
        assertThat(registerStatistics1).isNotEqualTo(registerStatistics2);
    }
}
