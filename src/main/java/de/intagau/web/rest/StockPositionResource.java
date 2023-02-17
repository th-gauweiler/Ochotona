package de.intagau.web.rest;

import de.intagau.domain.StockPosition;
import de.intagau.repository.StockPositionRepository;
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
 * REST controller for managing {@link de.intagau.domain.StockPosition}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class StockPositionResource {

    private final Logger log = LoggerFactory.getLogger(StockPositionResource.class);

    private static final String ENTITY_NAME = "stockPosition";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StockPositionRepository stockPositionRepository;

    public StockPositionResource(StockPositionRepository stockPositionRepository) {
        this.stockPositionRepository = stockPositionRepository;
    }

    /**
     * {@code POST  /stock-positions} : Create a new stockPosition.
     *
     * @param stockPosition the stockPosition to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockPosition, or with status {@code 400 (Bad Request)} if the stockPosition has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/stock-positions")
    public ResponseEntity<StockPosition> createStockPosition(@Valid @RequestBody StockPosition stockPosition) throws URISyntaxException {
        log.debug("REST request to save StockPosition : {}", stockPosition);
        if (stockPosition.getId() != null) {
            throw new BadRequestAlertException("A new stockPosition cannot already have an ID", ENTITY_NAME, "idexists");
        }
        StockPosition result = stockPositionRepository.save(stockPosition);
        return ResponseEntity
            .created(new URI("/api/stock-positions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /stock-positions/:id} : Updates an existing stockPosition.
     *
     * @param id the id of the stockPosition to save.
     * @param stockPosition the stockPosition to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockPosition,
     * or with status {@code 400 (Bad Request)} if the stockPosition is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stockPosition couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/stock-positions/{id}")
    public ResponseEntity<StockPosition> updateStockPosition(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StockPosition stockPosition
    ) throws URISyntaxException {
        log.debug("REST request to update StockPosition : {}, {}", id, stockPosition);
        if (stockPosition.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockPosition.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockPositionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        StockPosition result = stockPositionRepository.save(stockPosition);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockPosition.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /stock-positions/:id} : Partial updates given fields of an existing stockPosition, field will ignore if it is null
     *
     * @param id the id of the stockPosition to save.
     * @param stockPosition the stockPosition to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockPosition,
     * or with status {@code 400 (Bad Request)} if the stockPosition is not valid,
     * or with status {@code 404 (Not Found)} if the stockPosition is not found,
     * or with status {@code 500 (Internal Server Error)} if the stockPosition couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/stock-positions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StockPosition> partialUpdateStockPosition(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StockPosition stockPosition
    ) throws URISyntaxException {
        log.debug("REST request to partial update StockPosition partially : {}, {}", id, stockPosition);
        if (stockPosition.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockPosition.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockPositionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StockPosition> result = stockPositionRepository
            .findById(stockPosition.getId())
            .map(existingStockPosition -> {
                if (stockPosition.getAmount() != null) {
                    existingStockPosition.setAmount(stockPosition.getAmount());
                }
                if (stockPosition.getSerialNo() != null) {
                    existingStockPosition.setSerialNo(stockPosition.getSerialNo());
                }

                return existingStockPosition;
            })
            .map(stockPositionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockPosition.getId().toString())
        );
    }

    /**
     * {@code GET  /stock-positions} : get all the stockPositions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stockPositions in body.
     */
    @GetMapping("/stock-positions")
    public List<StockPosition> getAllStockPositions() {
        log.debug("REST request to get all StockPositions");
        return stockPositionRepository.findAll();
    }

    /**
     * {@code GET  /stock-positions/:id} : get the "id" stockPosition.
     *
     * @param id the id of the stockPosition to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockPosition, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/stock-positions/{id}")
    public ResponseEntity<StockPosition> getStockPosition(@PathVariable Long id) {
        log.debug("REST request to get StockPosition : {}", id);
        Optional<StockPosition> stockPosition = stockPositionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(stockPosition);
    }

    /**
     * {@code DELETE  /stock-positions/:id} : delete the "id" stockPosition.
     *
     * @param id the id of the stockPosition to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/stock-positions/{id}")
    public ResponseEntity<Void> deleteStockPosition(@PathVariable Long id) {
        log.debug("REST request to delete StockPosition : {}", id);
        stockPositionRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
