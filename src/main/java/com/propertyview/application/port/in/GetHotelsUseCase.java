package com.propertyview.application.port.in;

import com.propertyview.application.dto.HotelSummary;

import java.util.List;

/**
 * input port (use case interface)
 * responsible for retrieving a list of summary information for all hotels
 */
public interface GetHotelsUseCase {
    List<HotelSummary> getAll();
}
