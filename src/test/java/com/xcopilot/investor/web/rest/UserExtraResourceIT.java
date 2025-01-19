package com.xcopilot.investor.web.rest;

import static com.xcopilot.investor.domain.UserExtraAsserts.*;
import static com.xcopilot.investor.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xcopilot.investor.IntegrationTest;
import com.xcopilot.investor.domain.UserExtra;
import com.xcopilot.investor.repository.UserExtraRepository;
import com.xcopilot.investor.repository.UserRepository;
import com.xcopilot.investor.service.UserExtraService;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link UserExtraResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UserExtraResourceIT {

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/user-extras";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserExtraRepository userExtraRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private UserExtraRepository userExtraRepositoryMock;

    @Mock
    private UserExtraService userExtraServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserExtraMockMvc;

    private UserExtra userExtra;

    private UserExtra insertedUserExtra;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserExtra createEntity() {
        return new UserExtra().fullName(DEFAULT_FULL_NAME).phoneNumber(DEFAULT_PHONE_NUMBER);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserExtra createUpdatedEntity() {
        return new UserExtra().fullName(UPDATED_FULL_NAME).phoneNumber(UPDATED_PHONE_NUMBER);
    }

    @BeforeEach
    public void initTest() {
        userExtra = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedUserExtra != null) {
            userExtraRepository.delete(insertedUserExtra);
            insertedUserExtra = null;
        }
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void createUserExtra() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UserExtra
        var returnedUserExtra = om.readValue(
            restUserExtraMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userExtra)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserExtra.class
        );

        // Validate the UserExtra in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertUserExtraUpdatableFieldsEquals(returnedUserExtra, getPersistedUserExtra(returnedUserExtra));

        insertedUserExtra = returnedUserExtra;
    }

    @Test
    @Transactional
    void createUserExtraWithExistingId() throws Exception {
        // Create the UserExtra with an existing ID
        userExtra.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserExtraMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userExtra)))
            .andExpect(status().isBadRequest());

        // Validate the UserExtra in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFullNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userExtra.setFullName(null);

        // Create the UserExtra, which fails.

        restUserExtraMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userExtra)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserExtras() throws Exception {
        // Initialize the database
        insertedUserExtra = userExtraRepository.saveAndFlush(userExtra);

        // Get all the userExtraList
        restUserExtraMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userExtra.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserExtrasWithEagerRelationshipsIsEnabled() throws Exception {
        when(userExtraServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserExtraMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(userExtraServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserExtrasWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(userExtraServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserExtraMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(userExtraRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getUserExtra() throws Exception {
        // Initialize the database
        insertedUserExtra = userExtraRepository.saveAndFlush(userExtra);

        // Get the userExtra
        restUserExtraMockMvc
            .perform(get(ENTITY_API_URL_ID, userExtra.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userExtra.getId().intValue()))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER));
    }

    @Test
    @Transactional
    void getNonExistingUserExtra() throws Exception {
        // Get the userExtra
        restUserExtraMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserExtra() throws Exception {
        // Initialize the database
        insertedUserExtra = userExtraRepository.saveAndFlush(userExtra);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userExtra
        UserExtra updatedUserExtra = userExtraRepository.findById(userExtra.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserExtra are not directly saved in db
        em.detach(updatedUserExtra);
        updatedUserExtra.fullName(UPDATED_FULL_NAME).phoneNumber(UPDATED_PHONE_NUMBER);

        restUserExtraMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserExtra.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedUserExtra))
            )
            .andExpect(status().isOk());

        // Validate the UserExtra in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserExtraToMatchAllProperties(updatedUserExtra);
    }

    @Test
    @Transactional
    void putNonExistingUserExtra() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userExtra.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserExtraMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userExtra.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userExtra))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserExtra in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserExtra() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userExtra.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserExtraMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userExtra))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserExtra in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserExtra() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userExtra.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserExtraMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userExtra)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserExtra in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserExtraWithPatch() throws Exception {
        // Initialize the database
        insertedUserExtra = userExtraRepository.saveAndFlush(userExtra);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userExtra using partial update
        UserExtra partialUpdatedUserExtra = new UserExtra();
        partialUpdatedUserExtra.setId(userExtra.getId());

        partialUpdatedUserExtra.fullName(UPDATED_FULL_NAME);

        restUserExtraMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserExtra.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserExtra))
            )
            .andExpect(status().isOk());

        // Validate the UserExtra in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserExtraUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserExtra, userExtra),
            getPersistedUserExtra(userExtra)
        );
    }

    @Test
    @Transactional
    void fullUpdateUserExtraWithPatch() throws Exception {
        // Initialize the database
        insertedUserExtra = userExtraRepository.saveAndFlush(userExtra);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userExtra using partial update
        UserExtra partialUpdatedUserExtra = new UserExtra();
        partialUpdatedUserExtra.setId(userExtra.getId());

        partialUpdatedUserExtra.fullName(UPDATED_FULL_NAME).phoneNumber(UPDATED_PHONE_NUMBER);

        restUserExtraMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserExtra.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserExtra))
            )
            .andExpect(status().isOk());

        // Validate the UserExtra in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserExtraUpdatableFieldsEquals(partialUpdatedUserExtra, getPersistedUserExtra(partialUpdatedUserExtra));
    }

    @Test
    @Transactional
    void patchNonExistingUserExtra() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userExtra.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserExtraMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userExtra.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userExtra))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserExtra in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserExtra() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userExtra.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserExtraMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userExtra))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserExtra in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserExtra() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userExtra.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserExtraMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userExtra))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserExtra in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserExtra() throws Exception {
        // Initialize the database
        insertedUserExtra = userExtraRepository.saveAndFlush(userExtra);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userExtra
        restUserExtraMockMvc
            .perform(delete(ENTITY_API_URL_ID, userExtra.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userExtraRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected UserExtra getPersistedUserExtra(UserExtra userExtra) {
        return userExtraRepository.findById(userExtra.getId()).orElseThrow();
    }

    protected void assertPersistedUserExtraToMatchAllProperties(UserExtra expectedUserExtra) {
        assertUserExtraAllPropertiesEquals(expectedUserExtra, getPersistedUserExtra(expectedUserExtra));
    }

    protected void assertPersistedUserExtraToMatchUpdatableProperties(UserExtra expectedUserExtra) {
        assertUserExtraAllUpdatablePropertiesEquals(expectedUserExtra, getPersistedUserExtra(expectedUserExtra));
    }
}
