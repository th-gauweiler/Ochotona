package de.intagau.repository;

import de.intagau.domain.StockPosition;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StockPosition entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StockPositionRepository extends JpaRepository<StockPosition, Long> {}
