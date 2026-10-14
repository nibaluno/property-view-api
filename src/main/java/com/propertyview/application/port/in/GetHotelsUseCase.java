package com.propertyview.application.port.in;

import com.propertyview.application.dto.HotelSummary;

import java.util.List;

public interface GetHotelsUseCase {
    List<HotelSummary> getAll();
}
