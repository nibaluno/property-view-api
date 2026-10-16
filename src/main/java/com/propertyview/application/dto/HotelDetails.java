package com.propertyview.application.dto;

import com.propertyview.domain.model.*;

import java.util.List;

/**
 * application-level dto representing the extended, full information of a specific hotel,
 * including structured value objects and a flattened list of amenity names
 */
public record HotelDetails(
    Long id, String name, String description, String brand, Address address,
    Contacts contacts, ArrivalTime arrivalTime, List<String> amenities) {

    public static HotelDetails from(Hotel h) {

        return new HotelDetails(h.id(), h.name(), h.description(), h.brand(), h.address(), h.contacts(),
            h.arrivalTime(), h.amenities().stream().map(Amenity::name).toList());
    }
}
