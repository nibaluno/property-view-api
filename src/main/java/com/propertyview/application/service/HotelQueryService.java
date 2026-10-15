package com.propertyview.application.service;

import com.propertyview.application.dto.HotelDetails;
import com.propertyview.application.dto.HotelSummary;
import com.propertyview.application.port.in.GetHotelUseCase;
import com.propertyview.application.port.in.GetHotelsUseCase;
import com.propertyview.domain.exception.HotelNotFoundException;
import com.propertyview.domain.port.HotelRepository;
import java.util.List;

public class HotelQueryService implements GetHotelsUseCase, GetHotelUseCase {
    private final HotelRepository hotels;

    public HotelQueryService(HotelRepository hotels) { this.hotels = hotels; }

    @Override
    public List<HotelSummary> getAll() {
        return hotels.findAllOverviews().stream().map(HotelSummary::from).toList();
    }

    @Override
    public HotelDetails getById(long id) {
        return hotels.findById(id).map(HotelDetails::from)
            .orElseThrow(() -> new HotelNotFoundException(id));
    }
}
