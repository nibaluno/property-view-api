package com.propertyview.domain.model;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

public class HotelSearchCriteriaTest {
    @Test void blankValuesBecomeNullAndAmenitiesAreDeduplicated() {
        var c = HotelSearchCriteria.of(" ", null, " Minsk ", "", Arrays.asList("Free WiFi", "free wifi", " ", null));
        assertThat(c.name()).isNull();
        assertThat(c.city()).isEqualTo("Minsk");
        assertThat(c.amenities()).hasSize(1);
        assertThat(c.isEmpty()).isFalse();
        assertThat(HotelSearchCriteria.of(null, null, null, null, null).isEmpty()).isTrue();
    }
}
