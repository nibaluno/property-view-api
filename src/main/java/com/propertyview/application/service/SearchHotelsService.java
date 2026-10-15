package com.propertyview.application.service;

import com.propertyview.application.dto.HotelSummary;
import com.propertyview.application.port.in.SearchHotelsUseCase;
import com.propertyview.domain.model.HotelSearchCriteria;
import com.propertyview.domain.port.HotelRepository;
import java.util.List;

public class SearchHotelsService implements SearchHotelsUseCase {
    private final HotelRepository hotels;

    public SearchHotelsService(HotelRepository hotels) { this.hotels = hotels; }

    @Override
    public List<HotelSummary> search(HotelSearchCriteria criteria) {
        return hotels.search(criteria).stream().map(HotelSummary::from).toList();
    }
}
