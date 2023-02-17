package de.intagau.domain;

import static org.assertj.core.api.Assertions.assertThat;

import de.intagau.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockPositionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockPosition.class);
        StockPosition stockPosition1 = new StockPosition();
        stockPosition1.setId(1L);
        StockPosition stockPosition2 = new StockPosition();
        stockPosition2.setId(stockPosition1.getId());
        assertThat(stockPosition1).isEqualTo(stockPosition2);
        stockPosition2.setId(2L);
        assertThat(stockPosition1).isNotEqualTo(stockPosition2);
        stockPosition1.setId(null);
        assertThat(stockPosition1).isNotEqualTo(stockPosition2);
    }
}
