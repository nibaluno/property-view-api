package com.propertyview.application.service;

import com.propertyview.application.port.in.CreateHotelUseCase;

import com.propertyview.application.dto.CreateHotelCommand;
import com.propertyview.application.dto.HotelSummary;
import com.propertyview.domain.model.Hotel;
import com.propertyview.domain.port.HotelRepository;

/**
 * an application service that implements the {@link CreateHotelUseCase} input port
 * it coordinates the scenario for creating a new hotel, utilizing a domain factory
 */
public class CreateHotelService implements CreateHotelUseCase {
    private final HotelRepository hotels;

    public CreateHotelService(HotelRepository hotels) { this.hotels = hotels; }

    @Override
    public HotelSummary create(CreateHotelCommand c) {
        Hotel hotel = Hotel.create(c.name(), c.description(), c.brand(), c.address(), c.contacts(), c.arrivalTime());
        return HotelSummary.from(hotels.save(hotel));
    }
}
