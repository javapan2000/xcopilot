package com.xcopilot.investor.web.rest;

import com.xcopilot.investor.domain.UserExtra;
import com.xcopilot.investor.repository.UserExtraRepository;
import com.xcopilot.investor.service.UserExtraService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.xcopilot.investor.domain.UserExtra}.
 */
@RestController
@RequestMapping("/api/user-extras")
public class UserExtraResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserExtraResource.class);

    private static final String ENTITY_NAME = "userExtra";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserExtraService userExtraService;

    private final UserExtraRepository userExtraRepository;

    public UserExtraResource(UserExtraService userExtraService, UserExtraRepository userExtraRepository) {
        this.userExtraService = userExtraService;
        this.userExtraRepository = userExtraRepository;
    }

    /**
     * {@code POST  /user-extras} : Create a new userExtra.
     *
     * @param userExtra the userExtra to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userExtra, or with status {@code 400 (Bad Request)} if the userExtra has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UserExtra> createUserExtra(@Valid @RequestBody UserExtra userExtra) throws URISyntaxException {
        LOG.debug("REST request to save UserExtra : {}", userExtra);
        if (userExtra.getId() != null) {
            throw new BadRequestAlertException("A new userExtra cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userExtra = userExtraService.save(userExtra);
        return ResponseEntity.created(new URI("/api/user-extras/" + userExtra.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, userExtra.getId().toString()))
            .body(userExtra);
    }

    /**
     * {@code PUT  /user-extras/:id} : Updates an existing userExtra.
     *
     * @param id the id of the userExtra to save.
     * @param userExtra the userExtra to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userExtra,
     * or with status {@code 400 (Bad Request)} if the userExtra is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userExtra couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserExtra> updateUserExtra(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserExtra userExtra
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserExtra : {}, {}", id, userExtra);
        if (userExtra.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userExtra.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userExtraRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        userExtra = userExtraService.update(userExtra);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userExtra.getId().toString()))
            .body(userExtra);
    }

    /**
     * {@code PATCH  /user-extras/:id} : Partial updates given fields of an existing userExtra, field will ignore if it is null
     *
     * @param id the id of the userExtra to save.
     * @param userExtra the userExtra to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userExtra,
     * or with status {@code 400 (Bad Request)} if the userExtra is not valid,
     * or with status {@code 404 (Not Found)} if the userExtra is not found,
     * or with status {@code 500 (Internal Server Error)} if the userExtra couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserExtra> partialUpdateUserExtra(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserExtra userExtra
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserExtra partially : {}, {}", id, userExtra);
        if (userExtra.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userExtra.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userExtraRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserExtra> result = userExtraService.partialUpdate(userExtra);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userExtra.getId().toString())
        );
    }

    /**
     * {@code GET  /user-extras} : get all the userExtras.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userExtras in body.
     */
    @GetMapping("")
    public List<UserExtra> getAllUserExtras(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        LOG.debug("REST request to get all UserExtras");
        return userExtraService.findAll();
    }

    /**
     * {@code GET  /user-extras/:id} : get the "id" userExtra.
     *
     * @param id the id of the userExtra to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userExtra, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserExtra> getUserExtra(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UserExtra : {}", id);
        Optional<UserExtra> userExtra = userExtraService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userExtra);
    }

    /**
     * {@code DELETE  /user-extras/:id} : delete the "id" userExtra.
     *
     * @param id the id of the userExtra to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserExtra(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UserExtra : {}", id);
        userExtraService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
