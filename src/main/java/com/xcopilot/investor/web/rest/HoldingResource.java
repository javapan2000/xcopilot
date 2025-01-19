package com.xcopilot.investor.web.rest;

import com.xcopilot.investor.domain.Holding;
import com.xcopilot.investor.repository.HoldingRepository;
import com.xcopilot.investor.service.HoldingService;
import com.xcopilot.investor.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.xcopilot.investor.domain.Holding}.
 */
@RestController
@RequestMapping("/api/holdings")
public class HoldingResource {

    private static final Logger LOG = LoggerFactory.getLogger(HoldingResource.class);

    private static final String ENTITY_NAME = "holding";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HoldingService holdingService;

    private final HoldingRepository holdingRepository;

    public HoldingResource(HoldingService holdingService, HoldingRepository holdingRepository) {
        this.holdingService = holdingService;
        this.holdingRepository = holdingRepository;
    }

    /**
     * {@code POST  /holdings} : Create a new holding.
     *
     * @param holding the holding to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new holding, or with status {@code 400 (Bad Request)} if the holding has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Holding> createHolding(@Valid @RequestBody Holding holding) throws URISyntaxException {
        LOG.debug("REST request to save Holding : {}", holding);
        if (holding.getId() != null) {
            throw new BadRequestAlertException("A new holding cannot already have an ID", ENTITY_NAME, "idexists");
        }
        holding = holdingService.save(holding);
        return ResponseEntity.created(new URI("/api/holdings/" + holding.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, holding.getId().toString()))
            .body(holding);
    }

    /**
     * {@code PUT  /holdings/:id} : Updates an existing holding.
     *
     * @param id the id of the holding to save.
     * @param holding the holding to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated holding,
     * or with status {@code 400 (Bad Request)} if the holding is not valid,
     * or with status {@code 500 (Internal Server Error)} if the holding couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Holding> updateHolding(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Holding holding
    ) throws URISyntaxException {
        LOG.debug("REST request to update Holding : {}, {}", id, holding);
        if (holding.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, holding.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!holdingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        holding = holdingService.update(holding);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, holding.getId().toString()))
            .body(holding);
    }

    /**
     * {@code PATCH  /holdings/:id} : Partial updates given fields of an existing holding, field will ignore if it is null
     *
     * @param id the id of the holding to save.
     * @param holding the holding to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated holding,
     * or with status {@code 400 (Bad Request)} if the holding is not valid,
     * or with status {@code 404 (Not Found)} if the holding is not found,
     * or with status {@code 500 (Internal Server Error)} if the holding couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Holding> partialUpdateHolding(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Holding holding
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Holding partially : {}, {}", id, holding);
        if (holding.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, holding.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!holdingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Holding> result = holdingService.partialUpdate(holding);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, holding.getId().toString())
        );
    }

    /**
     * {@code GET  /holdings} : get all the holdings.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of holdings in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Holding>> getAllHoldings(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Holdings");
        Page<Holding> page;
        if (eagerload) {
            page = holdingService.findAllWithEagerRelationships(pageable);
        } else {
            page = holdingService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /holdings/:id} : get the "id" holding.
     *
     * @param id the id of the holding to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the holding, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Holding> getHolding(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Holding : {}", id);
        Optional<Holding> holding = holdingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(holding);
    }

    /**
     * {@code DELETE  /holdings/:id} : delete the "id" holding.
     *
     * @param id the id of the holding to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHolding(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Holding : {}", id);
        holdingService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
