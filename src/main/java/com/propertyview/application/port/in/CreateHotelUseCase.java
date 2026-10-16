package com.propertyview.application.port.in;

import com.propertyview.application.dto.CreateHotelCommand;
import com.propertyview.application.dto.HotelSummary;

/**
 * input port (use case interface)
 * responsible for creating a new hotel in the system
 */
public interface CreateHotelUseCase {
    HotelSummary create(CreateHotelCommand command);
}
