package de.intagau.domain;

import static org.assertj.core.api.Assertions.assertThat;

import de.intagau.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StorageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Storage.class);
        Storage storage1 = new Storage();
        storage1.setId(1L);
        Storage storage2 = new Storage();
        storage2.setId(storage1.getId());
        assertThat(storage1).isEqualTo(storage2);
        storage2.setId(2L);
        assertThat(storage1).isNotEqualTo(storage2);
        storage1.setId(null);
        assertThat(storage1).isNotEqualTo(storage2);
    }
}
