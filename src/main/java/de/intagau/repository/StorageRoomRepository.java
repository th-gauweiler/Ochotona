package de.intagau.repository;

import de.intagau.domain.StorageRoom;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StorageRoom entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StorageRoomRepository extends JpaRepository<StorageRoom, Long> {}
