package com.propertyview.application.service;

import com.propertyview.application.dto.HotelSummary;
import com.propertyview.application.port.in.SearchHotelsUseCase;
import com.propertyview.domain.model.HotelSearchCriteria;
import com.propertyview.domain.port.HotelRepository;
import java.util.List;

/**
 * an application service that implements the {@link SearchHotelsUseCase} input port
 * it coordinates the process of searching for and filtering hotels based on the provided criteria
 */
public class SearchHotelsService implements SearchHotelsUseCase {
    private final HotelRepository hotels;

    public SearchHotelsService(HotelRepository hotels) { this.hotels = hotels; }

    @Override
    public List<HotelSummary> search(HotelSearchCriteria criteria) {
        return hotels.search(criteria).stream().map(HotelSummary::from).toList();
    }
}
