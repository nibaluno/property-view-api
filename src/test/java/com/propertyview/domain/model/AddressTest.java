package com.propertyview.domain.model;

import com.propertyview.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class AddressTest {
    @Test void formatsSingleLineAsInTask() {
        var a = new Address(9, "Pobediteley Avenue", "Minsk", "Belarus", "220004");
        assertThat(a.toSingleLine()).isEqualTo("9 Pobediteley Avenue, Minsk, 220004, Belarus");
    }
    @Test void rejectsNonPositiveHouseNumber() {
        assertThatThrownBy(() -> new Address(0, "St", "Minsk", "Belarus", "1"))
            .isInstanceOf(DomainValidationException.class);
    }
    @Test void rejectsBlankStreet() {
        assertThatThrownBy(() -> new Address(1, " ", "Minsk", "Belarus", "1"))
            .isInstanceOf(DomainValidationException.class);
    }
}
