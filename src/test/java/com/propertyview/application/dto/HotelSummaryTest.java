package com.propertyview.application.dto;

import com.propertyview.domain.model.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class HotelSummaryTest {
    @Test
    void mapsOverviewToSingleLineAddress() {
        var o = new HotelOverview(1L, "DoubleTree by Hilton Minsk", "desc",
            new Address(9, "Pobediteley Avenue", "Minsk", "Belarus", "220004"), "+375 17 309-80-00");
        var s = HotelSummary.from(o);
        assertThat(s.address()).isEqualTo("9 Pobediteley Avenue, Minsk, 220004, Belarus");
        assertThat(s.phone()).isEqualTo("+375 17 309-80-00");
    }
}
