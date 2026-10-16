package com.propertyview.application.port.in;

import com.propertyview.application.dto.HotelSummary;
import com.propertyview.domain.model.HotelSearchCriteria;

import java.util.List;

/**
 * input port (use case interface)
 * responsible for searching and filtering a list of hotels based on specified criteria
 * (name, brand, city, country, amenities)
 */
public interface SearchHotelsUseCase {
    List<HotelSummary> search(HotelSearchCriteria criteria);
}
