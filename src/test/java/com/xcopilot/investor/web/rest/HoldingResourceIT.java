package com.xcopilot.investor.web.rest;

import static com.xcopilot.investor.domain.HoldingAsserts.*;
import static com.xcopilot.investor.web.rest.TestUtil.createUpdateProxyForBean;
import static com.xcopilot.investor.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xcopilot.investor.IntegrationTest;
import com.xcopilot.investor.domain.Holding;
import com.xcopilot.investor.repository.HoldingRepository;
import com.xcopilot.investor.service.HoldingService;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link HoldingResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class HoldingResourceIT {

    private static final String DEFAULT_SYMBOL = "AAAAAAAAAA";
    private static final String UPDATED_SYMBOL = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_QUANTITY = new BigDecimal(1);
    private static final BigDecimal UPDATED_QUANTITY = new BigDecimal(2);

    private static final BigDecimal DEFAULT_AVERAGE_COST = new BigDecimal(1);
    private static final BigDecimal UPDATED_AVERAGE_COST = new BigDecimal(2);

    private static final BigDecimal DEFAULT_CURRENT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_CURRENT_PRICE = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/holdings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private HoldingRepository holdingRepository;

    @Mock
    private HoldingRepository holdingRepositoryMock;

    @Mock
    private HoldingService holdingServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHoldingMockMvc;

    private Holding holding;

    private Holding insertedHolding;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Holding createEntity() {
        return new Holding()
            .symbol(DEFAULT_SYMBOL)
            .quantity(DEFAULT_QUANTITY)
            .averageCost(DEFAULT_AVERAGE_COST)
            .currentPrice(DEFAULT_CURRENT_PRICE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Holding createUpdatedEntity() {
        return new Holding()
            .symbol(UPDATED_SYMBOL)
            .quantity(UPDATED_QUANTITY)
            .averageCost(UPDATED_AVERAGE_COST)
            .currentPrice(UPDATED_CURRENT_PRICE);
    }

    @BeforeEach
    public void initTest() {
        holding = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedHolding != null) {
            holdingRepository.delete(insertedHolding);
            insertedHolding = null;
        }
    }

    @Test
    @Transactional
    void createHolding() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Holding
        var returnedHolding = om.readValue(
            restHoldingMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(holding)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Holding.class
        );

        // Validate the Holding in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertHoldingUpdatableFieldsEquals(returnedHolding, getPersistedHolding(returnedHolding));

        insertedHolding = returnedHolding;
    }

    @Test
    @Transactional
    void createHoldingWithExistingId() throws Exception {
        // Create the Holding with an existing ID
        holding.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHoldingMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(holding)))
            .andExpect(status().isBadRequest());

        // Validate the Holding in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSymbolIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        holding.setSymbol(null);

        // Create the Holding, which fails.

        restHoldingMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(holding)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        holding.setQuantity(null);

        // Create the Holding, which fails.

        restHoldingMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(holding)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAverageCostIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        holding.setAverageCost(null);

        // Create the Holding, which fails.

        restHoldingMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(holding)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllHoldings() throws Exception {
        // Initialize the database
        insertedHolding = holdingRepository.saveAndFlush(holding);

        // Get all the holdingList
        restHoldingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(holding.getId().intValue())))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(sameNumber(DEFAULT_QUANTITY))))
            .andExpect(jsonPath("$.[*].averageCost").value(hasItem(sameNumber(DEFAULT_AVERAGE_COST))))
            .andExpect(jsonPath("$.[*].currentPrice").value(hasItem(sameNumber(DEFAULT_CURRENT_PRICE))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllHoldingsWithEagerRelationshipsIsEnabled() throws Exception {
        when(holdingServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restHoldingMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(holdingServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllHoldingsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(holdingServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restHoldingMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(holdingRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getHolding() throws Exception {
        // Initialize the database
        insertedHolding = holdingRepository.saveAndFlush(holding);

        // Get the holding
        restHoldingMockMvc
            .perform(get(ENTITY_API_URL_ID, holding.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(holding.getId().intValue()))
            .andExpect(jsonPath("$.symbol").value(DEFAULT_SYMBOL))
            .andExpect(jsonPath("$.quantity").value(sameNumber(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.averageCost").value(sameNumber(DEFAULT_AVERAGE_COST)))
            .andExpect(jsonPath("$.currentPrice").value(sameNumber(DEFAULT_CURRENT_PRICE)));
    }

    @Test
    @Transactional
    void getNonExistingHolding() throws Exception {
        // Get the holding
        restHoldingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHolding() throws Exception {
        // Initialize the database
        insertedHolding = holdingRepository.saveAndFlush(holding);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the holding
        Holding updatedHolding = holdingRepository.findById(holding.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedHolding are not directly saved in db
        em.detach(updatedHolding);
        updatedHolding
            .symbol(UPDATED_SYMBOL)
            .quantity(UPDATED_QUANTITY)
            .averageCost(UPDATED_AVERAGE_COST)
            .currentPrice(UPDATED_CURRENT_PRICE);

        restHoldingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedHolding.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedHolding))
            )
            .andExpect(status().isOk());

        // Validate the Holding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedHoldingToMatchAllProperties(updatedHolding);
    }

    @Test
    @Transactional
    void putNonExistingHolding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        holding.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHoldingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, holding.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(holding))
            )
            .andExpect(status().isBadRequest());

        // Validate the Holding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHolding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        holding.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHoldingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(holding))
            )
            .andExpect(status().isBadRequest());

        // Validate the Holding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHolding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        holding.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHoldingMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(holding)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Holding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHoldingWithPatch() throws Exception {
        // Initialize the database
        insertedHolding = holdingRepository.saveAndFlush(holding);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the holding using partial update
        Holding partialUpdatedHolding = new Holding();
        partialUpdatedHolding.setId(holding.getId());

        partialUpdatedHolding.symbol(UPDATED_SYMBOL).currentPrice(UPDATED_CURRENT_PRICE);

        restHoldingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHolding.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHolding))
            )
            .andExpect(status().isOk());

        // Validate the Holding in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHoldingUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedHolding, holding), getPersistedHolding(holding));
    }

    @Test
    @Transactional
    void fullUpdateHoldingWithPatch() throws Exception {
        // Initialize the database
        insertedHolding = holdingRepository.saveAndFlush(holding);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the holding using partial update
        Holding partialUpdatedHolding = new Holding();
        partialUpdatedHolding.setId(holding.getId());

        partialUpdatedHolding
            .symbol(UPDATED_SYMBOL)
            .quantity(UPDATED_QUANTITY)
            .averageCost(UPDATED_AVERAGE_COST)
            .currentPrice(UPDATED_CURRENT_PRICE);

        restHoldingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHolding.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHolding))
            )
            .andExpect(status().isOk());

        // Validate the Holding in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHoldingUpdatableFieldsEquals(partialUpdatedHolding, getPersistedHolding(partialUpdatedHolding));
    }

    @Test
    @Transactional
    void patchNonExistingHolding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        holding.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHoldingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, holding.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(holding))
            )
            .andExpect(status().isBadRequest());

        // Validate the Holding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHolding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        holding.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHoldingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(holding))
            )
            .andExpect(status().isBadRequest());

        // Validate the Holding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHolding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        holding.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHoldingMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(holding)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Holding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHolding() throws Exception {
        // Initialize the database
        insertedHolding = holdingRepository.saveAndFlush(holding);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the holding
        restHoldingMockMvc
            .perform(delete(ENTITY_API_URL_ID, holding.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return holdingRepository.count();
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

    protected Holding getPersistedHolding(Holding holding) {
        return holdingRepository.findById(holding.getId()).orElseThrow();
    }

    protected void assertPersistedHoldingToMatchAllProperties(Holding expectedHolding) {
        assertHoldingAllPropertiesEquals(expectedHolding, getPersistedHolding(expectedHolding));
    }

    protected void assertPersistedHoldingToMatchUpdatableProperties(Holding expectedHolding) {
        assertHoldingAllUpdatablePropertiesEquals(expectedHolding, getPersistedHolding(expectedHolding));
    }
}
