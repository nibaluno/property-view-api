package com.propertyview.application.service;

import com.propertyview.application.dto.*;
import com.propertyview.domain.exception.HotelNotFoundException;
import com.propertyview.domain.model.*;
import com.propertyview.domain.port.HotelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

import static com.propertyview.support.HotelFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotelQueryServiceTest {

    @Mock HotelRepository hotels;
    @InjectMocks HotelQueryService service;

    @Test
    void getAllMapsOverviewsToSummaries() {
        Hotel h = withId(1L, hotel("A", "Hilton", "Minsk", "Belarus"));
        when(hotels.findAllOverviews()).thenReturn(List.of(h.toOverview()));

        List<HotelSummary> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).address()).isEqualTo("1 Main Street, Minsk, 100000, Belarus");
    }

    @Test
    void getByIdReturnsDetailsWithAmenitiesInOrder() {
        Hotel h = withId(1L, hotel("A", "Hilton", "Minsk", "Belarus"));
        h.addAmenities(amenities("Free parking", "Free WiFi"));
        when(hotels.findById(1L)).thenReturn(Optional.of(h));

        HotelDetails details = service.getById(1L);

        assertThat(details.amenities()).containsExactly("Free parking", "Free WiFi");
        assertThat(details.brand()).isEqualTo("Hilton");
    }

    @Test
    void getByIdThrowsWhenMissing() {
        when(hotels.findById(5L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(5L)).isInstanceOf(HotelNotFoundException.class);
    }
}
