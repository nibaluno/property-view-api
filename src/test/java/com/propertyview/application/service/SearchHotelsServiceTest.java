package com.propertyview.application.service;

import com.propertyview.application.dto.HotelSummary;
import com.propertyview.domain.model.*;
import com.propertyview.domain.port.HotelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static com.propertyview.support.HotelFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchHotelsServiceTest {

    @Mock HotelRepository hotels;
    @InjectMocks SearchHotelsService service;

    @Test
    void delegatesCriteriaToRepositoryAndMapsResult() {
        HotelSearchCriteria criteria = HotelSearchCriteria.of(null, null, "Minsk", null, null);
        HotelOverview overview = withId(1L, hotel("A", "Hilton", "Minsk", "Belarus")).toOverview();
        when(hotels.search(criteria)).thenReturn(List.of(overview));

        List<HotelSummary> result = service.search(criteria);

        assertThat(result).extracting(HotelSummary::id).containsExactly(1L);
    }
}
