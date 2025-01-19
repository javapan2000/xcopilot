package com.xcopilot.investor.service;

import com.xcopilot.investor.domain.Holding;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.xcopilot.investor.domain.Holding}.
 */
public interface HoldingService {
    /**
     * Save a holding.
     *
     * @param holding the entity to save.
     * @return the persisted entity.
     */
    Holding save(Holding holding);

    /**
     * Updates a holding.
     *
     * @param holding the entity to update.
     * @return the persisted entity.
     */
    Holding update(Holding holding);

    /**
     * Partially updates a holding.
     *
     * @param holding the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Holding> partialUpdate(Holding holding);

    /**
     * Get all the holdings.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Holding> findAll(Pageable pageable);

    /**
     * Get all the holdings with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Holding> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" holding.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Holding> findOne(Long id);

    /**
     * Delete the "id" holding.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
