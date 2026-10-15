package com.propertyview.application.port.in;

import com.propertyview.application.dto.HotelDetails;

public interface GetHotelUseCase {
    HotelDetails getById(long id);
}
