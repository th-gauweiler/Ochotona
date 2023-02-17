package de.intagau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.intagau.IntegrationTest;
import de.intagau.domain.Storage;
import de.intagau.domain.StorageRoom;
import de.intagau.repository.StorageRoomRepository;
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
 * Integration tests for the {@link StorageRoomResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StorageRoomResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/storage-rooms";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StorageRoomRepository storageRoomRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStorageRoomMockMvc;

    private StorageRoom storageRoom;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StorageRoom createEntity(EntityManager em) {
        StorageRoom storageRoom = new StorageRoom().name(DEFAULT_NAME);
        // Add required entity
        Storage storage;
        if (TestUtil.findAll(em, Storage.class).isEmpty()) {
            storage = StorageResourceIT.createEntity(em);
            em.persist(storage);
            em.flush();
        } else {
            storage = TestUtil.findAll(em, Storage.class).get(0);
        }
        storageRoom.setInherit(storage);
        return storageRoom;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StorageRoom createUpdatedEntity(EntityManager em) {
        StorageRoom storageRoom = new StorageRoom().name(UPDATED_NAME);
        // Add required entity
        Storage storage;
        if (TestUtil.findAll(em, Storage.class).isEmpty()) {
            storage = StorageResourceIT.createUpdatedEntity(em);
            em.persist(storage);
            em.flush();
        } else {
            storage = TestUtil.findAll(em, Storage.class).get(0);
        }
        storageRoom.setInherit(storage);
        return storageRoom;
    }

    @BeforeEach
    public void initTest() {
        storageRoom = createEntity(em);
    }

    @Test
    @Transactional
    void createStorageRoom() throws Exception {
        int databaseSizeBeforeCreate = storageRoomRepository.findAll().size();
        // Create the StorageRoom
        restStorageRoomMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(storageRoom))
            )
            .andExpect(status().isCreated());

        // Validate the StorageRoom in the database
        List<StorageRoom> storageRoomList = storageRoomRepository.findAll();
        assertThat(storageRoomList).hasSize(databaseSizeBeforeCreate + 1);
        StorageRoom testStorageRoom = storageRoomList.get(storageRoomList.size() - 1);
        assertThat(testStorageRoom.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createStorageRoomWithExistingId() throws Exception {
        // Create the StorageRoom with an existing ID
        storageRoom.setId(1L);

        int databaseSizeBeforeCreate = storageRoomRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStorageRoomMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(storageRoom))
            )
            .andExpect(status().isBadRequest());

        // Validate the StorageRoom in the database
        List<StorageRoom> storageRoomList = storageRoomRepository.findAll();
        assertThat(storageRoomList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllStorageRooms() throws Exception {
        // Initialize the database
        storageRoomRepository.saveAndFlush(storageRoom);

        // Get all the storageRoomList
        restStorageRoomMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(storageRoom.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getStorageRoom() throws Exception {
        // Initialize the database
        storageRoomRepository.saveAndFlush(storageRoom);

        // Get the storageRoom
        restStorageRoomMockMvc
            .perform(get(ENTITY_API_URL_ID, storageRoom.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(storageRoom.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingStorageRoom() throws Exception {
        // Get the storageRoom
        restStorageRoomMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStorageRoom() throws Exception {
        // Initialize the database
        storageRoomRepository.saveAndFlush(storageRoom);

        int databaseSizeBeforeUpdate = storageRoomRepository.findAll().size();

        // Update the storageRoom
        StorageRoom updatedStorageRoom = storageRoomRepository.findById(storageRoom.getId()).get();
        // Disconnect from session so that the updates on updatedStorageRoom are not directly saved in db
        em.detach(updatedStorageRoom);
        updatedStorageRoom.name(UPDATED_NAME);

        restStorageRoomMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStorageRoom.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedStorageRoom))
            )
            .andExpect(status().isOk());

        // Validate the StorageRoom in the database
        List<StorageRoom> storageRoomList = storageRoomRepository.findAll();
        assertThat(storageRoomList).hasSize(databaseSizeBeforeUpdate);
        StorageRoom testStorageRoom = storageRoomList.get(storageRoomList.size() - 1);
        assertThat(testStorageRoom.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingStorageRoom() throws Exception {
        int databaseSizeBeforeUpdate = storageRoomRepository.findAll().size();
        storageRoom.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStorageRoomMockMvc
            .perform(
                put(ENTITY_API_URL_ID, storageRoom.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(storageRoom))
            )
            .andExpect(status().isBadRequest());

        // Validate the StorageRoom in the database
        List<StorageRoom> storageRoomList = storageRoomRepository.findAll();
        assertThat(storageRoomList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStorageRoom() throws Exception {
        int databaseSizeBeforeUpdate = storageRoomRepository.findAll().size();
        storageRoom.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStorageRoomMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(storageRoom))
            )
            .andExpect(status().isBadRequest());

        // Validate the StorageRoom in the database
        List<StorageRoom> storageRoomList = storageRoomRepository.findAll();
        assertThat(storageRoomList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStorageRoom() throws Exception {
        int databaseSizeBeforeUpdate = storageRoomRepository.findAll().size();
        storageRoom.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStorageRoomMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(storageRoom))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StorageRoom in the database
        List<StorageRoom> storageRoomList = storageRoomRepository.findAll();
        assertThat(storageRoomList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStorageRoomWithPatch() throws Exception {
        // Initialize the database
        storageRoomRepository.saveAndFlush(storageRoom);

        int databaseSizeBeforeUpdate = storageRoomRepository.findAll().size();

        // Update the storageRoom using partial update
        StorageRoom partialUpdatedStorageRoom = new StorageRoom();
        partialUpdatedStorageRoom.setId(storageRoom.getId());

        restStorageRoomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStorageRoom.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStorageRoom))
            )
            .andExpect(status().isOk());

        // Validate the StorageRoom in the database
        List<StorageRoom> storageRoomList = storageRoomRepository.findAll();
        assertThat(storageRoomList).hasSize(databaseSizeBeforeUpdate);
        StorageRoom testStorageRoom = storageRoomList.get(storageRoomList.size() - 1);
        assertThat(testStorageRoom.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateStorageRoomWithPatch() throws Exception {
        // Initialize the database
        storageRoomRepository.saveAndFlush(storageRoom);

        int databaseSizeBeforeUpdate = storageRoomRepository.findAll().size();

        // Update the storageRoom using partial update
        StorageRoom partialUpdatedStorageRoom = new StorageRoom();
        partialUpdatedStorageRoom.setId(storageRoom.getId());

        partialUpdatedStorageRoom.name(UPDATED_NAME);

        restStorageRoomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStorageRoom.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStorageRoom))
            )
            .andExpect(status().isOk());

        // Validate the StorageRoom in the database
        List<StorageRoom> storageRoomList = storageRoomRepository.findAll();
        assertThat(storageRoomList).hasSize(databaseSizeBeforeUpdate);
        StorageRoom testStorageRoom = storageRoomList.get(storageRoomList.size() - 1);
        assertThat(testStorageRoom.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingStorageRoom() throws Exception {
        int databaseSizeBeforeUpdate = storageRoomRepository.findAll().size();
        storageRoom.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStorageRoomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, storageRoom.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(storageRoom))
            )
            .andExpect(status().isBadRequest());

        // Validate the StorageRoom in the database
        List<StorageRoom> storageRoomList = storageRoomRepository.findAll();
        assertThat(storageRoomList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStorageRoom() throws Exception {
        int databaseSizeBeforeUpdate = storageRoomRepository.findAll().size();
        storageRoom.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStorageRoomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(storageRoom))
            )
            .andExpect(status().isBadRequest());

        // Validate the StorageRoom in the database
        List<StorageRoom> storageRoomList = storageRoomRepository.findAll();
        assertThat(storageRoomList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStorageRoom() throws Exception {
        int databaseSizeBeforeUpdate = storageRoomRepository.findAll().size();
        storageRoom.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStorageRoomMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(storageRoom))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StorageRoom in the database
        List<StorageRoom> storageRoomList = storageRoomRepository.findAll();
        assertThat(storageRoomList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStorageRoom() throws Exception {
        // Initialize the database
        storageRoomRepository.saveAndFlush(storageRoom);

        int databaseSizeBeforeDelete = storageRoomRepository.findAll().size();

        // Delete the storageRoom
        restStorageRoomMockMvc
            .perform(delete(ENTITY_API_URL_ID, storageRoom.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<StorageRoom> storageRoomList = storageRoomRepository.findAll();
        assertThat(storageRoomList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
