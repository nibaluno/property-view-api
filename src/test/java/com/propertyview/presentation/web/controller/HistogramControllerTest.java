package com.propertyview.presentation.web.controller;

import com.propertyview.application.histogram.HistogramParam;
import com.propertyview.application.port.in.GetHistogramUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HistogramController.class)
class HistogramControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean GetHistogramUseCase histogram;

    @Test
    void returnsCountsByCity() throws Exception {
        Map<String, Long> data = new LinkedHashMap<>();
        data.put("Minsk", 1L);
        data.put("Moscow", 2L);
        when(histogram.getHistogram(HistogramParam.CITY)).thenReturn(data);

        mockMvc.perform(get("/histogram/city"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.Minsk").value(1))
            .andExpect(jsonPath("$.Moscow").value(2));
    }

    @Test
    void parameterIsCaseInsensitive() throws Exception {
        when(histogram.getHistogram(HistogramParam.AMENITIES)).thenReturn(Map.of("Free WiFi", 20L));
        mockMvc.perform(get("/histogram/Amenities"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$['Free WiFi']").value(20));
    }

    @Test
    void unknownParameterGives400WithSupportedList() throws Exception {
        mockMvc.perform(get("/histogram/stars"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("brand, city, country, amenities")));
    }
}
