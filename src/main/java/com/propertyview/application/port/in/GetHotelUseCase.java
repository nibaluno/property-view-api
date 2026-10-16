package com.propertyview.application.port.in;

import com.propertyview.application.dto.HotelDetails;

/**
 * input port (use case interface)
 * responsible for retrieving detailed information about a specific hotel by its identifier
 */
public interface GetHotelUseCase {
    HotelDetails getById(long id);
}
