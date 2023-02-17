package de.intagau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.intagau.IntegrationTest;
import de.intagau.domain.Storage;
import de.intagau.repository.StorageRepository;
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
 * Integration tests for the {@link StorageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StorageResourceIT {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/storages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStorageMockMvc;

    private Storage storage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Storage createEntity(EntityManager em) {
        Storage storage = new Storage().key(DEFAULT_KEY);
        return storage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Storage createUpdatedEntity(EntityManager em) {
        Storage storage = new Storage().key(UPDATED_KEY);
        return storage;
    }

    @BeforeEach
    public void initTest() {
        storage = createEntity(em);
    }

    @Test
    @Transactional
    void createStorage() throws Exception {
        int databaseSizeBeforeCreate = storageRepository.findAll().size();
        // Create the Storage
        restStorageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(storage))
            )
            .andExpect(status().isCreated());

        // Validate the Storage in the database
        List<Storage> storageList = storageRepository.findAll();
        assertThat(storageList).hasSize(databaseSizeBeforeCreate + 1);
        Storage testStorage = storageList.get(storageList.size() - 1);
        assertThat(testStorage.getKey()).isEqualTo(DEFAULT_KEY);
    }

    @Test
    @Transactional
    void createStorageWithExistingId() throws Exception {
        // Create the Storage with an existing ID
        storage.setId(1L);

        int databaseSizeBeforeCreate = storageRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStorageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(storage))
            )
            .andExpect(status().isBadRequest());

        // Validate the Storage in the database
        List<Storage> storageList = storageRepository.findAll();
        assertThat(storageList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = storageRepository.findAll().size();
        // set the field null
        storage.setKey(null);

        // Create the Storage, which fails.

        restStorageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(storage))
            )
            .andExpect(status().isBadRequest());

        List<Storage> storageList = storageRepository.findAll();
        assertThat(storageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStorages() throws Exception {
        // Initialize the database
        storageRepository.saveAndFlush(storage);

        // Get all the storageList
        restStorageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(storage.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)));
    }

    @Test
    @Transactional
    void getStorage() throws Exception {
        // Initialize the database
        storageRepository.saveAndFlush(storage);

        // Get the storage
        restStorageMockMvc
            .perform(get(ENTITY_API_URL_ID, storage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(storage.getId().intValue()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY));
    }

    @Test
    @Transactional
    void getNonExistingStorage() throws Exception {
        // Get the storage
        restStorageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStorage() throws Exception {
        // Initialize the database
        storageRepository.saveAndFlush(storage);

        int databaseSizeBeforeUpdate = storageRepository.findAll().size();

        // Update the storage
        Storage updatedStorage = storageRepository.findById(storage.getId()).get();
        // Disconnect from session so that the updates on updatedStorage are not directly saved in db
        em.detach(updatedStorage);
        updatedStorage.key(UPDATED_KEY);

        restStorageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStorage.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedStorage))
            )
            .andExpect(status().isOk());

        // Validate the Storage in the database
        List<Storage> storageList = storageRepository.findAll();
        assertThat(storageList).hasSize(databaseSizeBeforeUpdate);
        Storage testStorage = storageList.get(storageList.size() - 1);
        assertThat(testStorage.getKey()).isEqualTo(UPDATED_KEY);
    }

    @Test
    @Transactional
    void putNonExistingStorage() throws Exception {
        int databaseSizeBeforeUpdate = storageRepository.findAll().size();
        storage.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStorageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, storage.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(storage))
            )
            .andExpect(status().isBadRequest());

        // Validate the Storage in the database
        List<Storage> storageList = storageRepository.findAll();
        assertThat(storageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStorage() throws Exception {
        int databaseSizeBeforeUpdate = storageRepository.findAll().size();
        storage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStorageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(storage))
            )
            .andExpect(status().isBadRequest());

        // Validate the Storage in the database
        List<Storage> storageList = storageRepository.findAll();
        assertThat(storageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStorage() throws Exception {
        int databaseSizeBeforeUpdate = storageRepository.findAll().size();
        storage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStorageMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(storage))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Storage in the database
        List<Storage> storageList = storageRepository.findAll();
        assertThat(storageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStorageWithPatch() throws Exception {
        // Initialize the database
        storageRepository.saveAndFlush(storage);

        int databaseSizeBeforeUpdate = storageRepository.findAll().size();

        // Update the storage using partial update
        Storage partialUpdatedStorage = new Storage();
        partialUpdatedStorage.setId(storage.getId());

        restStorageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStorage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStorage))
            )
            .andExpect(status().isOk());

        // Validate the Storage in the database
        List<Storage> storageList = storageRepository.findAll();
        assertThat(storageList).hasSize(databaseSizeBeforeUpdate);
        Storage testStorage = storageList.get(storageList.size() - 1);
        assertThat(testStorage.getKey()).isEqualTo(DEFAULT_KEY);
    }

    @Test
    @Transactional
    void fullUpdateStorageWithPatch() throws Exception {
        // Initialize the database
        storageRepository.saveAndFlush(storage);

        int databaseSizeBeforeUpdate = storageRepository.findAll().size();

        // Update the storage using partial update
        Storage partialUpdatedStorage = new Storage();
        partialUpdatedStorage.setId(storage.getId());

        partialUpdatedStorage.key(UPDATED_KEY);

        restStorageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStorage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStorage))
            )
            .andExpect(status().isOk());

        // Validate the Storage in the database
        List<Storage> storageList = storageRepository.findAll();
        assertThat(storageList).hasSize(databaseSizeBeforeUpdate);
        Storage testStorage = storageList.get(storageList.size() - 1);
        assertThat(testStorage.getKey()).isEqualTo(UPDATED_KEY);
    }

    @Test
    @Transactional
    void patchNonExistingStorage() throws Exception {
        int databaseSizeBeforeUpdate = storageRepository.findAll().size();
        storage.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStorageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, storage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(storage))
            )
            .andExpect(status().isBadRequest());

        // Validate the Storage in the database
        List<Storage> storageList = storageRepository.findAll();
        assertThat(storageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStorage() throws Exception {
        int databaseSizeBeforeUpdate = storageRepository.findAll().size();
        storage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStorageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(storage))
            )
            .andExpect(status().isBadRequest());

        // Validate the Storage in the database
        List<Storage> storageList = storageRepository.findAll();
        assertThat(storageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStorage() throws Exception {
        int databaseSizeBeforeUpdate = storageRepository.findAll().size();
        storage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStorageMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(storage))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Storage in the database
        List<Storage> storageList = storageRepository.findAll();
        assertThat(storageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStorage() throws Exception {
        // Initialize the database
        storageRepository.saveAndFlush(storage);

        int databaseSizeBeforeDelete = storageRepository.findAll().size();

        // Delete the storage
        restStorageMockMvc
            .perform(delete(ENTITY_API_URL_ID, storage.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Storage> storageList = storageRepository.findAll();
        assertThat(storageList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
