package de.intagau.web.rest;

import de.intagau.domain.StorageRoom;
import de.intagau.repository.StorageRoomRepository;
import de.intagau.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.intagau.domain.StorageRoom}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class StorageRoomResource {

    private final Logger log = LoggerFactory.getLogger(StorageRoomResource.class);

    private static final String ENTITY_NAME = "storageRoom";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StorageRoomRepository storageRoomRepository;

    public StorageRoomResource(StorageRoomRepository storageRoomRepository) {
        this.storageRoomRepository = storageRoomRepository;
    }

    /**
     * {@code POST  /storage-rooms} : Create a new storageRoom.
     *
     * @param storageRoom the storageRoom to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new storageRoom, or with status {@code 400 (Bad Request)} if the storageRoom has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/storage-rooms")
    public ResponseEntity<StorageRoom> createStorageRoom(@Valid @RequestBody StorageRoom storageRoom) throws URISyntaxException {
        log.debug("REST request to save StorageRoom : {}", storageRoom);
        if (storageRoom.getId() != null) {
            throw new BadRequestAlertException("A new storageRoom cannot already have an ID", ENTITY_NAME, "idexists");
        }
        StorageRoom result = storageRoomRepository.save(storageRoom);
        return ResponseEntity
            .created(new URI("/api/storage-rooms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /storage-rooms/:id} : Updates an existing storageRoom.
     *
     * @param id the id of the storageRoom to save.
     * @param storageRoom the storageRoom to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated storageRoom,
     * or with status {@code 400 (Bad Request)} if the storageRoom is not valid,
     * or with status {@code 500 (Internal Server Error)} if the storageRoom couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/storage-rooms/{id}")
    public ResponseEntity<StorageRoom> updateStorageRoom(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StorageRoom storageRoom
    ) throws URISyntaxException {
        log.debug("REST request to update StorageRoom : {}, {}", id, storageRoom);
        if (storageRoom.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, storageRoom.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!storageRoomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        StorageRoom result = storageRoomRepository.save(storageRoom);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, storageRoom.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /storage-rooms/:id} : Partial updates given fields of an existing storageRoom, field will ignore if it is null
     *
     * @param id the id of the storageRoom to save.
     * @param storageRoom the storageRoom to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated storageRoom,
     * or with status {@code 400 (Bad Request)} if the storageRoom is not valid,
     * or with status {@code 404 (Not Found)} if the storageRoom is not found,
     * or with status {@code 500 (Internal Server Error)} if the storageRoom couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/storage-rooms/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StorageRoom> partialUpdateStorageRoom(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StorageRoom storageRoom
    ) throws URISyntaxException {
        log.debug("REST request to partial update StorageRoom partially : {}, {}", id, storageRoom);
        if (storageRoom.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, storageRoom.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!storageRoomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StorageRoom> result = storageRoomRepository
            .findById(storageRoom.getId())
            .map(existingStorageRoom -> {
                if (storageRoom.getName() != null) {
                    existingStorageRoom.setName(storageRoom.getName());
                }

                return existingStorageRoom;
            })
            .map(storageRoomRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, storageRoom.getId().toString())
        );
    }

    /**
     * {@code GET  /storage-rooms} : get all the storageRooms.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of storageRooms in body.
     */
    @GetMapping("/storage-rooms")
    public List<StorageRoom> getAllStorageRooms() {
        log.debug("REST request to get all StorageRooms");
        return storageRoomRepository.findAll();
    }

    /**
     * {@code GET  /storage-rooms/:id} : get the "id" storageRoom.
     *
     * @param id the id of the storageRoom to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the storageRoom, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/storage-rooms/{id}")
    public ResponseEntity<StorageRoom> getStorageRoom(@PathVariable Long id) {
        log.debug("REST request to get StorageRoom : {}", id);
        Optional<StorageRoom> storageRoom = storageRoomRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(storageRoom);
    }

    /**
     * {@code DELETE  /storage-rooms/:id} : delete the "id" storageRoom.
     *
     * @param id the id of the storageRoom to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/storage-rooms/{id}")
    public ResponseEntity<Void> deleteStorageRoom(@PathVariable Long id) {
        log.debug("REST request to delete StorageRoom : {}", id);
        storageRoomRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
