package com.propertyview.presentation.web.controller;

import com.propertyview.application.dto.*;
import com.propertyview.application.port.in.*;
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
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HotelController.class)
class HotelControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean CreateHotelUseCase createHotel;
    @MockitoBean GetHotelsUseCase getHotels;
    @MockitoBean GetHotelUseCase getHotel;

    private static final String VALID_REQUEST = """
            {
              "name": "DoubleTree by Hilton Minsk",
              "description": "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms",
              "brand": "Hilton",
              "address": {"houseNumber": 9, "street": "Pobediteley Avenue", "city": "Minsk",
                          "country": "Belarus", "postCode": "220004"},
              "contacts": {"phone": "+375 17 309-80-00", "email": "doubletreeminsk.info@hilton.com"},
              "arrivalTime": {"checkIn": "14:00", "checkOut": "12:00"}
            }
            """;

    private static final HotelSummary SUMMARY = new HotelSummary(1L, "DoubleTree by Hilton Minsk", "desc",
        "9 Pobediteley Avenue, Minsk, 220004, Belarus", "+375 17 309-80-00");

    @Test
    void createReturns201WithLocationAndSummary() throws Exception {
        when(createHotel.create(any())).thenReturn(SUMMARY);

        mockMvc.perform(post("/hotels").contentType(MediaType.APPLICATION_JSON).content(VALID_REQUEST))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", endsWith("/hotels/1")))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.address").value("9 Pobediteley Avenue, Minsk, 220004, Belarus"))
            .andExpect(jsonPath("$.phone").value("+375 17 309-80-00"));

        ArgumentCaptor<CreateHotelCommand> captor = ArgumentCaptor.forClass(CreateHotelCommand.class);
        verify(createHotel).create(captor.capture());
        CreateHotelCommand command = captor.getValue();
        assertThat(command.address().houseNumber()).isEqualTo(9);
        assertThat(command.arrivalTime().checkOut()).isEqualTo(LocalTime.of(12, 0));
    }

    @Test
    void createAcceptsMissingOptionalFields() throws Exception {
        when(createHotel.create(any())).thenReturn(SUMMARY);
        String body = """
                {"name": "H", "brand": "B",
                 "address": {"houseNumber": 1, "street": "S", "city": "C", "country": "X", "postCode": "1"},
                 "contacts": {"phone": "1", "email": "h@example.com"},
                 "arrivalTime": {"checkIn": "14:00"}}
                """;
        mockMvc.perform(post("/hotels").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated());

        ArgumentCaptor<CreateHotelCommand> captor = ArgumentCaptor.forClass(CreateHotelCommand.class);
        verify(createHotel).create(captor.capture());
        assertThat(captor.getValue().description()).isNull();
        assertThat(captor.getValue().arrivalTime().checkOut()).isNull();
    }

    @Test
    void createRejectsInvalidFieldsWithProblemDetail() throws Exception {
        String body = """
                {"name": " ", "brand": "Hilton",
                 "address": {"houseNumber": 0, "street": "S", "city": "C", "country": "X", "postCode": "1"},
                 "contacts": {"phone": "1", "email": "not-an-email"},
                 "arrivalTime": {"checkIn": "14:00"}}
                """;
        mockMvc.perform(post("/hotels").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.title").value("Validation failed"))
            .andExpect(jsonPath("$.errors.name").exists())
            .andExpect(jsonPath("$.errors['address.houseNumber']").exists())
            .andExpect(jsonPath("$.errors['contacts.email']").exists());
        verifyNoInteractions(createHotel);
    }

    @Test
    void createRejectsMissingRequiredObjects() throws Exception {
        mockMvc.perform(post("/hotels").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"H\", \"brand\": \"B\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors.address").exists())
            .andExpect(jsonPath("$.errors.contacts").exists())
            .andExpect(jsonPath("$.errors.arrivalTime").exists());
    }

    @Test
    void createRejectsMalformedTimeAndBrokenJson() throws Exception {
        String badTime = VALID_REQUEST.replace("\"14:00\"", "\"25:00\"");
        mockMvc.perform(post("/hotels").contentType(MediaType.APPLICATION_JSON).content(badTime))
            .andExpect(status().isBadRequest());
        mockMvc.perform(post("/hotels").contentType(MediaType.APPLICATION_JSON).content("{not json"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(createHotel);
    }

    @Test
    void getAllReturnsBriefList() throws Exception {
        when(getHotels.getAll()).thenReturn(List.of(SUMMARY));

        mockMvc.perform(get("/hotels"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value("DoubleTree by Hilton Minsk"))
            .andExpect(jsonPath("$[0].address").value("9 Pobediteley Avenue, Minsk, 220004, Belarus"));
    }

    @Test
    void getByIdReturnsFullStructure() throws Exception {
        when(getHotel.getById(1L)).thenReturn(new HotelDetails(1L, "DoubleTree by Hilton Minsk", "desc", "Hilton",
            new Address(9, "Pobediteley Avenue", "Minsk", "Belarus", "220004"),
            new Contacts("+375 17 309-80-00", "doubletreeminsk.info@hilton.com"),
            new ArrivalTime(LocalTime.of(14, 0), LocalTime.of(12, 0)),
            List.of("Free parking", "Free WiFi")));

        mockMvc.perform(get("/hotels/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.brand").value("Hilton"))
            .andExpect(jsonPath("$.address.houseNumber").value(9))
            .andExpect(jsonPath("$.address.postCode").value("220004"))
            .andExpect(jsonPath("$.contacts.email").value("doubletreeminsk.info@hilton.com"))
            .andExpect(jsonPath("$.arrivalTime.checkIn").value("14:00"))
            .andExpect(jsonPath("$.arrivalTime.checkOut").value("12:00"))
            .andExpect(jsonPath("$.amenities[0]").value("Free parking"));
    }

    @Test
    void getByIdReturns404ProblemForUnknownHotel() throws Exception {
        when(getHotel.getById(42L)).thenThrow(new HotelNotFoundException(42L));

        mockMvc.perform(get("/hotels/42"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.title").value("Hotel not found"))
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getByIdRejectsNonNumericId() throws Exception {
        mockMvc.perform(get("/hotels/abc")).andExpect(status().isBadRequest());
    }
}
