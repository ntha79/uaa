package com.hdmon.uaa.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.hdmon.uaa.domain.RegisterStatistics;
import com.hdmon.uaa.repository.RegisterStatisticsRepository;
import com.hdmon.uaa.web.rest.errors.BadRequestAlertException;
import com.hdmon.uaa.web.rest.util.HeaderUtil;
import com.hdmon.uaa.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing RegisterStatistics.
 */
@RestController
@RequestMapping("/api")
public class RegisterStatisticsResource {

    private final Logger log = LoggerFactory.getLogger(RegisterStatisticsResource.class);

    private static final String ENTITY_NAME = "registerStatistics";

    private final RegisterStatisticsRepository registerStatisticsRepository;

    public RegisterStatisticsResource(RegisterStatisticsRepository registerStatisticsRepository) {
        this.registerStatisticsRepository = registerStatisticsRepository;
    }

    /**
     * POST  /registerstatistics : Create a new registerStatistics.
     *
     * @param registerStatistics the registerStatistics to create
     * @return the ResponseEntity with status 201 (Created) and with body the new registerStatistics, or with status 400 (Bad Request) if the registerStatistics has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/registerstatistics")
    @Timed
    public ResponseEntity<RegisterStatistics> createRegisterStatistics(@RequestBody RegisterStatistics registerStatistics) throws URISyntaxException {
        log.debug("REST request to save RegisterStatistics : {}", registerStatistics);
        if (registerStatistics.getId() != null) {
            throw new BadRequestAlertException("A new registerStatistics cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RegisterStatistics result = registerStatisticsRepository.save(registerStatistics);
        return ResponseEntity.created(new URI("/api/register-statistics/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /registerstatistics : Updates an existing registerStatistics.
     *
     * @param registerStatistics the registerStatistics to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated registerStatistics,
     * or with status 400 (Bad Request) if the registerStatistics is not valid,
     * or with status 500 (Internal Server Error) if the registerStatistics couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/registerstatistics")
    @Timed
    public ResponseEntity<RegisterStatistics> updateRegisterStatistics(@RequestBody RegisterStatistics registerStatistics) throws URISyntaxException {
        log.debug("REST request to update RegisterStatistics : {}", registerStatistics);
        if (registerStatistics.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        RegisterStatistics result = registerStatisticsRepository.save(registerStatistics);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, registerStatistics.getId().toString()))
            .body(result);
    }

    /**
     * GET  /registerstatistics : get all the registerStatistics.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of registerStatistics in body
     */
    @GetMapping("/registerstatistics")
    @Timed
    public ResponseEntity<List<RegisterStatistics>> getAllRegisterStatistics(Pageable pageable) {
        log.debug("REST request to get a page of RegisterStatistics");
        Page<RegisterStatistics> page = registerStatisticsRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/register-statistics");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /registerstatistics/:id : get the "id" registerStatistics.
     *
     * @param id the id of the registerStatistics to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the registerStatistics, or with status 404 (Not Found)
     */
    @GetMapping("/registerstatistics/{id}")
    @Timed
    public ResponseEntity<RegisterStatistics> getRegisterStatistics(@PathVariable Long id) {
        log.debug("REST request to get RegisterStatistics : {}", id);
        RegisterStatistics registerStatistics = registerStatisticsRepository.findOne(id);
        return ResponseEntity.ok(registerStatistics);
    }

    /**
     * DELETE  /registerstatistics/:id : delete the "id" registerStatistics.
     *
     * @param id the id of the registerStatistics to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/registerstatistics/{id}")
    @Timed
    public ResponseEntity<Void> deleteRegisterStatistics(@PathVariable Long id) {
        log.debug("REST request to delete RegisterStatistics : {}", id);

        registerStatisticsRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
