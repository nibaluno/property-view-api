package com.propertyview.presentation.web.controller;

import com.propertyview.application.dto.HotelSummary;
import com.propertyview.application.port.in.SearchHotelsUseCase;
import com.propertyview.domain.model.Amenity;
import com.propertyview.domain.model.HotelSearchCriteria;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
class SearchControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean SearchHotelsUseCase search;

    private HotelSearchCriteria capturedCriteria() {
        ArgumentCaptor<HotelSearchCriteria> captor = ArgumentCaptor.forClass(HotelSearchCriteria.class);
        verify(search).search(captor.capture());
        return captor.getValue();
    }

    @Test
    void passesAllFiltersToUseCase() throws Exception {
        when(search.search(any())).thenReturn(List.of(new HotelSummary(1L, "H", null, "addr", "phone")));

        mockMvc.perform(get("/search").param("name", "Double").param("brand", "Hilton")
                .param("city", "minsk").param("country", "Belarus")
                .param("amenities", "Free WiFi", "Pool"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].address").value("addr"));

        HotelSearchCriteria c = capturedCriteria();
        assertThat(c.name()).isEqualTo("Double");
        assertThat(c.city()).isEqualTo("minsk");
        assertThat(c.amenities()).containsExactlyInAnyOrder(new Amenity("Free WiFi"), new Amenity("Pool"));
    }

    @Test
    void acceptsCommaSeparatedAmenities() throws Exception {
        when(search.search(any())).thenReturn(List.of());
        mockMvc.perform(get("/search").param("amenities", "Free WiFi,Pool")).andExpect(status().isOk());
        assertThat(capturedCriteria().amenities()).hasSize(2);
    }

    @Test
    void noParametersMeansEmptyCriteria() throws Exception {
        when(search.search(any())).thenReturn(List.of());
        mockMvc.perform(get("/search")).andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());
        assertThat(capturedCriteria().isEmpty()).isTrue();
    }
}
