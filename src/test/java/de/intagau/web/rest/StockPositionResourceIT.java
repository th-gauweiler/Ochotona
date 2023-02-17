package de.intagau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.intagau.IntegrationTest;
import de.intagau.domain.StockPosition;
import de.intagau.domain.Storage;
import de.intagau.repository.StockPositionRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link StockPositionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StockPositionResourceIT {

    private static final Integer DEFAULT_AMOUNT = 1;
    private static final Integer UPDATED_AMOUNT = 2;

    private static final String DEFAULT_SERIAL_NO = "AAAAAAAAAA";
    private static final String UPDATED_SERIAL_NO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/stock-positions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StockPositionRepository stockPositionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockPositionMockMvc;

    private StockPosition stockPosition;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockPosition createEntity(EntityManager em) {
        StockPosition stockPosition = new StockPosition().amount(DEFAULT_AMOUNT).serialNo(DEFAULT_SERIAL_NO);
        // Add required entity
        Storage storage;
        if (TestUtil.findAll(em, Storage.class).isEmpty()) {
            storage = StorageResourceIT.createEntity(em);
            em.persist(storage);
            em.flush();
        } else {
            storage = TestUtil.findAll(em, Storage.class).get(0);
        }
        stockPosition.setInherit(storage);
        return stockPosition;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockPosition createUpdatedEntity(EntityManager em) {
        StockPosition stockPosition = new StockPosition().amount(UPDATED_AMOUNT).serialNo(UPDATED_SERIAL_NO);
        // Add required entity
        Storage storage;
        if (TestUtil.findAll(em, Storage.class).isEmpty()) {
            storage = StorageResourceIT.createUpdatedEntity(em);
            em.persist(storage);
            em.flush();
        } else {
            storage = TestUtil.findAll(em, Storage.class).get(0);
        }
        stockPosition.setInherit(storage);
        return stockPosition;
    }

    @BeforeEach
    public void initTest() {
        stockPosition = createEntity(em);
    }

    @Test
    @Transactional
    void createStockPosition() throws Exception {
        int databaseSizeBeforeCreate = stockPositionRepository.findAll().size();
        // Create the StockPosition
        restStockPositionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPosition))
            )
            .andExpect(status().isCreated());

        // Validate the StockPosition in the database
        List<StockPosition> stockPositionList = stockPositionRepository.findAll();
        assertThat(stockPositionList).hasSize(databaseSizeBeforeCreate + 1);
        StockPosition testStockPosition = stockPositionList.get(stockPositionList.size() - 1);
        assertThat(testStockPosition.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testStockPosition.getSerialNo()).isEqualTo(DEFAULT_SERIAL_NO);
    }

    @Test
    @Transactional
    void createStockPositionWithExistingId() throws Exception {
        // Create the StockPosition with an existing ID
        stockPosition.setId(1L);

        int databaseSizeBeforeCreate = stockPositionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockPositionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPosition))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockPosition in the database
        List<StockPosition> stockPositionList = stockPositionRepository.findAll();
        assertThat(stockPositionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllStockPositions() throws Exception {
        // Initialize the database
        stockPositionRepository.saveAndFlush(stockPosition);

        // Get all the stockPositionList
        restStockPositionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockPosition.getId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.[*].serialNo").value(hasItem(DEFAULT_SERIAL_NO)));
    }

    @Test
    @Transactional
    void getStockPosition() throws Exception {
        // Initialize the database
        stockPositionRepository.saveAndFlush(stockPosition);

        // Get the stockPosition
        restStockPositionMockMvc
            .perform(get(ENTITY_API_URL_ID, stockPosition.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockPosition.getId().intValue()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT))
            .andExpect(jsonPath("$.serialNo").value(DEFAULT_SERIAL_NO));
    }

    @Test
    @Transactional
    void getNonExistingStockPosition() throws Exception {
        // Get the stockPosition
        restStockPositionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStockPosition() throws Exception {
        // Initialize the database
        stockPositionRepository.saveAndFlush(stockPosition);

        int databaseSizeBeforeUpdate = stockPositionRepository.findAll().size();

        // Update the stockPosition
        StockPosition updatedStockPosition = stockPositionRepository.findById(stockPosition.getId()).get();
        // Disconnect from session so that the updates on updatedStockPosition are not directly saved in db
        em.detach(updatedStockPosition);
        updatedStockPosition.amount(UPDATED_AMOUNT).serialNo(UPDATED_SERIAL_NO);

        restStockPositionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStockPosition.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedStockPosition))
            )
            .andExpect(status().isOk());

        // Validate the StockPosition in the database
        List<StockPosition> stockPositionList = stockPositionRepository.findAll();
        assertThat(stockPositionList).hasSize(databaseSizeBeforeUpdate);
        StockPosition testStockPosition = stockPositionList.get(stockPositionList.size() - 1);
        assertThat(testStockPosition.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testStockPosition.getSerialNo()).isEqualTo(UPDATED_SERIAL_NO);
    }

    @Test
    @Transactional
    void putNonExistingStockPosition() throws Exception {
        int databaseSizeBeforeUpdate = stockPositionRepository.findAll().size();
        stockPosition.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockPositionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockPosition.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPosition))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockPosition in the database
        List<StockPosition> stockPositionList = stockPositionRepository.findAll();
        assertThat(stockPositionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockPosition() throws Exception {
        int databaseSizeBeforeUpdate = stockPositionRepository.findAll().size();
        stockPosition.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockPositionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPosition))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockPosition in the database
        List<StockPosition> stockPositionList = stockPositionRepository.findAll();
        assertThat(stockPositionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockPosition() throws Exception {
        int databaseSizeBeforeUpdate = stockPositionRepository.findAll().size();
        stockPosition.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockPositionMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPosition))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockPosition in the database
        List<StockPosition> stockPositionList = stockPositionRepository.findAll();
        assertThat(stockPositionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStockPositionWithPatch() throws Exception {
        // Initialize the database
        stockPositionRepository.saveAndFlush(stockPosition);

        int databaseSizeBeforeUpdate = stockPositionRepository.findAll().size();

        // Update the stockPosition using partial update
        StockPosition partialUpdatedStockPosition = new StockPosition();
        partialUpdatedStockPosition.setId(stockPosition.getId());

        partialUpdatedStockPosition.amount(UPDATED_AMOUNT);

        restStockPositionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockPosition.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockPosition))
            )
            .andExpect(status().isOk());

        // Validate the StockPosition in the database
        List<StockPosition> stockPositionList = stockPositionRepository.findAll();
        assertThat(stockPositionList).hasSize(databaseSizeBeforeUpdate);
        StockPosition testStockPosition = stockPositionList.get(stockPositionList.size() - 1);
        assertThat(testStockPosition.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testStockPosition.getSerialNo()).isEqualTo(DEFAULT_SERIAL_NO);
    }

    @Test
    @Transactional
    void fullUpdateStockPositionWithPatch() throws Exception {
        // Initialize the database
        stockPositionRepository.saveAndFlush(stockPosition);

        int databaseSizeBeforeUpdate = stockPositionRepository.findAll().size();

        // Update the stockPosition using partial update
        StockPosition partialUpdatedStockPosition = new StockPosition();
        partialUpdatedStockPosition.setId(stockPosition.getId());

        partialUpdatedStockPosition.amount(UPDATED_AMOUNT).serialNo(UPDATED_SERIAL_NO);

        restStockPositionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockPosition.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockPosition))
            )
            .andExpect(status().isOk());

        // Validate the StockPosition in the database
        List<StockPosition> stockPositionList = stockPositionRepository.findAll();
        assertThat(stockPositionList).hasSize(databaseSizeBeforeUpdate);
        StockPosition testStockPosition = stockPositionList.get(stockPositionList.size() - 1);
        assertThat(testStockPosition.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testStockPosition.getSerialNo()).isEqualTo(UPDATED_SERIAL_NO);
    }

    @Test
    @Transactional
    void patchNonExistingStockPosition() throws Exception {
        int databaseSizeBeforeUpdate = stockPositionRepository.findAll().size();
        stockPosition.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockPositionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockPosition.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockPosition))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockPosition in the database
        List<StockPosition> stockPositionList = stockPositionRepository.findAll();
        assertThat(stockPositionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockPosition() throws Exception {
        int databaseSizeBeforeUpdate = stockPositionRepository.findAll().size();
        stockPosition.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockPositionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockPosition))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockPosition in the database
        List<StockPosition> stockPositionList = stockPositionRepository.findAll();
        assertThat(stockPositionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockPosition() throws Exception {
        int databaseSizeBeforeUpdate = stockPositionRepository.findAll().size();
        stockPosition.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockPositionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockPosition))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockPosition in the database
        List<StockPosition> stockPositionList = stockPositionRepository.findAll();
        assertThat(stockPositionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStockPosition() throws Exception {
        // Initialize the database
        stockPositionRepository.saveAndFlush(stockPosition);

        int databaseSizeBeforeDelete = stockPositionRepository.findAll().size();

        // Delete the stockPosition
        restStockPositionMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockPosition.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<StockPosition> stockPositionList = stockPositionRepository.findAll();
        assertThat(stockPositionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
