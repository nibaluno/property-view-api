package com.propertyview.presentation.web.controller;

import com.propertyview.application.dto.AddAmenitiesCommand;
import com.propertyview.application.dto.HotelDetails;
import com.propertyview.application.port.in.AddAmenitiesUseCase;
import com.propertyview.domain.exception.HotelNotFoundException;
import com.propertyview.domain.model.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HotelAmenityController.class)
class HotelAmenityControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean AddAmenitiesUseCase addAmenities;

    private static HotelDetails details(List<String> amenities) {
        return new HotelDetails(1L, "H", null, "Hilton",
            new Address(1, "S", "Minsk", "Belarus", "1"), new Contacts("1", "h@example.com"),
            new ArrivalTime(LocalTime.of(14, 0), null), amenities);
    }

    @Test
    void addsAmenitiesAndReturnsUpdatedHotel() throws Exception {
        when(addAmenities.addAmenities(any())).thenReturn(details(List.of("Free WiFi", "Pool")));

        mockMvc.perform(post("/hotels/1/amenities").contentType(MediaType.APPLICATION_JSON)
                .content("[\"Free WiFi\", \"Pool\"]"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.amenities.length()").value(2))
            .andExpect(jsonPath("$.amenities[1]").value("Pool"));

        ArgumentCaptor<AddAmenitiesCommand> captor = ArgumentCaptor.forClass(AddAmenitiesCommand.class);
        verify(addAmenities).addAmenities(captor.capture());
        assertThat(captor.getValue().hotelId()).isEqualTo(1L);
        assertThat(captor.getValue().amenities()).containsExactly("Free WiFi", "Pool");
    }

    @Test
    void rejectsBlankElement() throws Exception {
        mockMvc.perform(post("/hotels/1/amenities").contentType(MediaType.APPLICATION_JSON)
                .content("[\"ok\", \" \"]"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(addAmenities);
    }

    @Test
    void rejectsBodyThatIsNotAnArray() throws Exception {
        mockMvc.perform(post("/hotels/1/amenities").contentType(MediaType.APPLICATION_JSON)
                .content("{\"a\": 1}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void unknownHotelGives404() throws Exception {
        when(addAmenities.addAmenities(any())).thenThrow(new HotelNotFoundException(7L));
        mockMvc.perform(post("/hotels/7/amenities").contentType(MediaType.APPLICATION_JSON).content("[\"x\"]"))
            .andExpect(status().isNotFound());
    }
}
