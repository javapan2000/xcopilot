package com.xcopilot.investor.service;

import com.xcopilot.investor.domain.UserExtra;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.xcopilot.investor.domain.UserExtra}.
 */
public interface UserExtraService {
    /**
     * Save a userExtra.
     *
     * @param userExtra the entity to save.
     * @return the persisted entity.
     */
    UserExtra save(UserExtra userExtra);

    /**
     * Updates a userExtra.
     *
     * @param userExtra the entity to update.
     * @return the persisted entity.
     */
    UserExtra update(UserExtra userExtra);

    /**
     * Partially updates a userExtra.
     *
     * @param userExtra the entity to update partially.
     * @return the persisted entity.
     */
    Optional<UserExtra> partialUpdate(UserExtra userExtra);

    /**
     * Get all the userExtras.
     *
     * @return the list of entities.
     */
    List<UserExtra> findAll();

    /**
     * Get all the userExtras with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UserExtra> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" userExtra.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserExtra> findOne(Long id);

    /**
     * Delete the "id" userExtra.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
