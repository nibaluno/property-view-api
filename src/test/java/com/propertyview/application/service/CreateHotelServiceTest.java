package com.propertyview.application.service;

import com.propertyview.application.dto.*;
import com.propertyview.domain.exception.DomainValidationException;
import com.propertyview.domain.model.*;
import com.propertyview.domain.port.HotelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalTime;

import static com.propertyview.support.HotelFixtures.withId;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateHotelServiceTest {

    @Mock HotelRepository hotels;
    @InjectMocks CreateHotelService service;

    private static CreateHotelCommand command(String name) {
        return new CreateHotelCommand(name, null, "Hilton",
            new Address(9, "Pobediteley Avenue", "Minsk", "Belarus", "220004"),
            new Contacts("+375 17 309-80-00", "doubletreeminsk.info@hilton.com"),
            new ArrivalTime(LocalTime.of(14, 0), null));
    }

    @Test
    void savesNewHotelAndReturnsSummary() {
        when(hotels.save(any(Hotel.class))).thenAnswer(inv -> withId(1L, inv.getArgument(0)));

        HotelSummary result = service.create(command("DoubleTree by Hilton Minsk"));

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.address()).isEqualTo("9 Pobediteley Avenue, Minsk, 220004, Belarus");
        ArgumentCaptor<Hotel> captor = ArgumentCaptor.forClass(Hotel.class);
        verify(hotels).save(captor.capture());
        assertThat(captor.getValue().id()).isNull();
        assertThat(captor.getValue().arrivalTime().checkOut()).isNull();
    }

    @Test
    void invalidCommandNeverReachesRepository() {
        assertThatThrownBy(() -> service.create(command(" "))).isInstanceOf(DomainValidationException.class);
        verifyNoInteractions(hotels);
    }
}
