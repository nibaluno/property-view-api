package com.propertyview.application.port.in;

import com.propertyview.application.dto.AddAmenitiesCommand;
import com.propertyview.application.dto.HotelDetails;

public interface AddAmenitiesUseCase {
    HotelDetails addAmenities(AddAmenitiesCommand command);
}
