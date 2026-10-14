package com.propertyview.domain.model;

import com.propertyview.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class HotelTest {
    private Hotel newHotel() {
        return Hotel.create("DoubleTree by Hilton Minsk", null, "Hilton",
            new Address(9, "Pobediteley Avenue", "Minsk", "Belarus", "220004"),
            new Contacts("+375 17 309-80-00", "doubletreeminsk.info@hilton.com"),
            new ArrivalTime(LocalTime.of(14, 0), null));
    }

    @Test void newHotelHasNoIdAndNoAmenities() {
        Hotel h = newHotel();
        assertThat(h.id()).isNull();
        assertThat(h.amenities()).isEmpty();
        assertThat(h.arrivalTime().checkOut()).isNull();
    }
    @Test void addAmenitiesKeepsOrderAndIgnoresDuplicates() {
        Hotel h = newHotel();
        assertThat(h.addAmenities(List.of(new Amenity("Free WiFi"), new Amenity("Concierge")))).isTrue();
        assertThat(h.addAmenities(List.of(new Amenity("free wifi")))).isFalse();
        assertThat(h.amenities()).extracting(Amenity::name).containsExactly("Free WiFi", "Concierge");
    }
    @Test void overviewUsesPhoneAndAddress() {
        HotelOverview o = newHotel().toOverview();
        assertThat(o.phone()).isEqualTo("+375 17 309-80-00");
        assertThat(o.address().toSingleLine()).startsWith("9 Pobediteley Avenue");
    }
    @Test void restoreRequiresId() {
        Hotel h = newHotel();
        assertThatThrownBy(() -> Hotel.restore(null, h.name(), null, h.brand(), h.address(),
            h.contacts(), h.arrivalTime(), List.of())).isInstanceOf(DomainValidationException.class);
    }
}
