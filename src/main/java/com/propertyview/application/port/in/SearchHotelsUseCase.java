package com.propertyview.application.port.in;

import com.propertyview.application.dto.HotelSummary;
import com.propertyview.domain.model.HotelSearchCriteria;

import java.util.List;

public interface SearchHotelsUseCase {
    List<HotelSummary> search(HotelSearchCriteria criteria);
}
