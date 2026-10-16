package com.propertyview.application.port.in;

import com.propertyview.application.dto.AddAmenitiesCommand;
import com.propertyview.application.dto.HotelDetails;

/**
 * input port (use case interface)
 * responsible for adding a list of amenities to an existing hotel
 */
public interface AddAmenitiesUseCase {
    HotelDetails addAmenities(AddAmenitiesCommand command);
}
