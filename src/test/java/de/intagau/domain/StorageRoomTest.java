package de.intagau.domain;

import static org.assertj.core.api.Assertions.assertThat;

import de.intagau.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StorageRoomTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StorageRoom.class);
        StorageRoom storageRoom1 = new StorageRoom();
        storageRoom1.setId(1L);
        StorageRoom storageRoom2 = new StorageRoom();
        storageRoom2.setId(storageRoom1.getId());
        assertThat(storageRoom1).isEqualTo(storageRoom2);
        storageRoom2.setId(2L);
        assertThat(storageRoom1).isNotEqualTo(storageRoom2);
        storageRoom1.setId(null);
        assertThat(storageRoom1).isNotEqualTo(storageRoom2);
    }
}
