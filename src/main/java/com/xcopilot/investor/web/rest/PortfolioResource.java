package com.xcopilot.investor.web.rest;

import com.xcopilot.investor.domain.Portfolio;
import com.xcopilot.investor.repository.PortfolioRepository;
import com.xcopilot.investor.service.PortfolioService;
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
 * REST controller for managing {@link com.xcopilot.investor.domain.Portfolio}.
 */
@RestController
@RequestMapping("/api/portfolios")
public class PortfolioResource {

    private static final Logger LOG = LoggerFactory.getLogger(PortfolioResource.class);

    private static final String ENTITY_NAME = "portfolio";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PortfolioService portfolioService;

    private final PortfolioRepository portfolioRepository;

    public PortfolioResource(PortfolioService portfolioService, PortfolioRepository portfolioRepository) {
        this.portfolioService = portfolioService;
        this.portfolioRepository = portfolioRepository;
    }

    /**
     * {@code POST  /portfolios} : Create a new portfolio.
     *
     * @param portfolio the portfolio to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new portfolio, or with status {@code 400 (Bad Request)} if the portfolio has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Portfolio> createPortfolio(@Valid @RequestBody Portfolio portfolio) throws URISyntaxException {
        LOG.debug("REST request to save Portfolio : {}", portfolio);
        if (portfolio.getId() != null) {
            throw new BadRequestAlertException("A new portfolio cannot already have an ID", ENTITY_NAME, "idexists");
        }
        portfolio = portfolioService.save(portfolio);
        return ResponseEntity.created(new URI("/api/portfolios/" + portfolio.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, portfolio.getId().toString()))
            .body(portfolio);
    }

    /**
     * {@code PUT  /portfolios/:id} : Updates an existing portfolio.
     *
     * @param id the id of the portfolio to save.
     * @param portfolio the portfolio to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated portfolio,
     * or with status {@code 400 (Bad Request)} if the portfolio is not valid,
     * or with status {@code 500 (Internal Server Error)} if the portfolio couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Portfolio> updatePortfolio(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Portfolio portfolio
    ) throws URISyntaxException {
        LOG.debug("REST request to update Portfolio : {}, {}", id, portfolio);
        if (portfolio.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, portfolio.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!portfolioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        portfolio = portfolioService.update(portfolio);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, portfolio.getId().toString()))
            .body(portfolio);
    }

    /**
     * {@code PATCH  /portfolios/:id} : Partial updates given fields of an existing portfolio, field will ignore if it is null
     *
     * @param id the id of the portfolio to save.
     * @param portfolio the portfolio to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated portfolio,
     * or with status {@code 400 (Bad Request)} if the portfolio is not valid,
     * or with status {@code 404 (Not Found)} if the portfolio is not found,
     * or with status {@code 500 (Internal Server Error)} if the portfolio couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Portfolio> partialUpdatePortfolio(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Portfolio portfolio
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Portfolio partially : {}, {}", id, portfolio);
        if (portfolio.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, portfolio.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!portfolioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Portfolio> result = portfolioService.partialUpdate(portfolio);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, portfolio.getId().toString())
        );
    }

    /**
     * {@code GET  /portfolios} : get all the portfolios.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of portfolios in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Portfolio>> getAllPortfolios(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Portfolios");
        Page<Portfolio> page;
        if (eagerload) {
            page = portfolioService.findAllWithEagerRelationships(pageable);
        } else {
            page = portfolioService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /portfolios/:id} : get the "id" portfolio.
     *
     * @param id the id of the portfolio to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the portfolio, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Portfolio> getPortfolio(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Portfolio : {}", id);
        Optional<Portfolio> portfolio = portfolioService.findOne(id);
        return ResponseUtil.wrapOrNotFound(portfolio);
    }

    /**
     * {@code DELETE  /portfolios/:id} : delete the "id" portfolio.
     *
     * @param id the id of the portfolio to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Portfolio : {}", id);
        portfolioService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
