package com.xcopilot.investor.service;

import com.xcopilot.investor.domain.Portfolio;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.xcopilot.investor.domain.Portfolio}.
 */
public interface PortfolioService {
    /**
     * Save a portfolio.
     *
     * @param portfolio the entity to save.
     * @return the persisted entity.
     */
    Portfolio save(Portfolio portfolio);

    /**
     * Updates a portfolio.
     *
     * @param portfolio the entity to update.
     * @return the persisted entity.
     */
    Portfolio update(Portfolio portfolio);

    /**
     * Partially updates a portfolio.
     *
     * @param portfolio the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Portfolio> partialUpdate(Portfolio portfolio);

    /**
     * Get all the portfolios.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Portfolio> findAll(Pageable pageable);

    /**
     * Get all the portfolios with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Portfolio> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" portfolio.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Portfolio> findOne(Long id);

    /**
     * Delete the "id" portfolio.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
