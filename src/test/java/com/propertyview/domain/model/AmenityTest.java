package com.propertyview.domain.model;

import com.propertyview.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class AmenityTest {
    @Test void normalizesWhitespaceAndKeepsOriginalCase() {
        assertThat(new Amenity("  Free   WiFi ").name()).isEqualTo("Free WiFi");
    }
    @Test void equalityIgnoresCase() {
        assertThat(new Amenity("Free WiFi")).isEqualTo(new Amenity("free wifi"))
            .hasSameHashCodeAs(new Amenity("FREE  WIFI"));
    }
    @Test void rejectsBlank() {
        assertThatThrownBy(() -> new Amenity("  ")).isInstanceOf(DomainValidationException.class);
    }
}
