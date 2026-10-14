package com.propertyview.application.port.in;

import com.propertyview.application.dto.CreateHotelCommand;
import com.propertyview.application.dto.HotelSummary;

public interface CreateHotelUseCase {
    HotelSummary create(CreateHotelCommand command);
}
