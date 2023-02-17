package de.intagau.web.rest;

import de.intagau.domain.Storage;
import de.intagau.repository.StorageRepository;
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
 * REST controller for managing {@link de.intagau.domain.Storage}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class StorageResource {

    private final Logger log = LoggerFactory.getLogger(StorageResource.class);

    private static final String ENTITY_NAME = "storage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StorageRepository storageRepository;

    public StorageResource(StorageRepository storageRepository) {
        this.storageRepository = storageRepository;
    }

    /**
     * {@code POST  /storages} : Create a new storage.
     *
     * @param storage the storage to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new storage, or with status {@code 400 (Bad Request)} if the storage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/storages")
    public ResponseEntity<Storage> createStorage(@Valid @RequestBody Storage storage) throws URISyntaxException {
        log.debug("REST request to save Storage : {}", storage);
        if (storage.getId() != null) {
            throw new BadRequestAlertException("A new storage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Storage result = storageRepository.save(storage);
        return ResponseEntity
            .created(new URI("/api/storages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /storages/:id} : Updates an existing storage.
     *
     * @param id the id of the storage to save.
     * @param storage the storage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated storage,
     * or with status {@code 400 (Bad Request)} if the storage is not valid,
     * or with status {@code 500 (Internal Server Error)} if the storage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/storages/{id}")
    public ResponseEntity<Storage> updateStorage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Storage storage
    ) throws URISyntaxException {
        log.debug("REST request to update Storage : {}, {}", id, storage);
        if (storage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, storage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!storageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Storage result = storageRepository.save(storage);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, storage.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /storages/:id} : Partial updates given fields of an existing storage, field will ignore if it is null
     *
     * @param id the id of the storage to save.
     * @param storage the storage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated storage,
     * or with status {@code 400 (Bad Request)} if the storage is not valid,
     * or with status {@code 404 (Not Found)} if the storage is not found,
     * or with status {@code 500 (Internal Server Error)} if the storage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/storages/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Storage> partialUpdateStorage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Storage storage
    ) throws URISyntaxException {
        log.debug("REST request to partial update Storage partially : {}, {}", id, storage);
        if (storage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, storage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!storageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Storage> result = storageRepository
            .findById(storage.getId())
            .map(existingStorage -> {
                if (storage.getKey() != null) {
                    existingStorage.setKey(storage.getKey());
                }

                return existingStorage;
            })
            .map(storageRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, storage.getId().toString())
        );
    }

    /**
     * {@code GET  /storages} : get all the storages.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of storages in body.
     */
    @GetMapping("/storages")
    public List<Storage> getAllStorages() {
        log.debug("REST request to get all Storages");
        return storageRepository.findAll();
    }

    /**
     * {@code GET  /storages/:id} : get the "id" storage.
     *
     * @param id the id of the storage to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the storage, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/storages/{id}")
    public ResponseEntity<Storage> getStorage(@PathVariable Long id) {
        log.debug("REST request to get Storage : {}", id);
        Optional<Storage> storage = storageRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(storage);
    }

    /**
     * {@code DELETE  /storages/:id} : delete the "id" storage.
     *
     * @param id the id of the storage to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/storages/{id}")
    public ResponseEntity<Void> deleteStorage(@PathVariable Long id) {
        log.debug("REST request to delete Storage : {}", id);
        storageRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
