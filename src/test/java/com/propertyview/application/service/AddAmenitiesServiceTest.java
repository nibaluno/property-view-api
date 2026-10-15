package com.propertyview.application.service;

import com.propertyview.application.dto.AddAmenitiesCommand;
import com.propertyview.application.dto.HotelDetails;
import com.propertyview.domain.exception.DomainValidationException;
import com.propertyview.domain.exception.HotelNotFoundException;
import com.propertyview.domain.model.Hotel;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddAmenitiesServiceTest {

    @Mock HotelRepository hotels;
    @InjectMocks AddAmenitiesService service;

    private Hotel existingWithWifi() {
        Hotel h = withId(1L, hotel("A", "Hilton", "Minsk", "Belarus"));
        h.addAmenities(amenities("Free WiFi"));
        return h;
    }

    @Test
    void addsNewAmenitiesAndIgnoresCaseInsensitiveDuplicates() {
        when(hotels.findById(1L)).thenReturn(Optional.of(existingWithWifi()));
        when(hotels.save(any(Hotel.class))).thenAnswer(inv -> inv.getArgument(0));

        HotelDetails result = service.addAmenities(new AddAmenitiesCommand(1L, List.of("free wifi", "Pool")));

        assertThat(result.amenities()).containsExactly("Free WiFi", "Pool");
    }

    @Test
    void unknownHotelIsNotSaved() {
        when(hotels.findById(5L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.addAmenities(new AddAmenitiesCommand(5L, List.of("Pool"))))
            .isInstanceOf(HotelNotFoundException.class);
        verify(hotels, never()).save(any());
    }

    @Test
    void blankAmenityIsRejectedBeforeSaving() {
        when(hotels.findById(1L)).thenReturn(Optional.of(existingWithWifi()));
        assertThatThrownBy(() -> service.addAmenities(new AddAmenitiesCommand(1L, List.of("Pool", " "))))
            .isInstanceOf(DomainValidationException.class);
        verify(hotels, never()).save(any());
    }
}
