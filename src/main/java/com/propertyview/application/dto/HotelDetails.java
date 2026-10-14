package com.propertyview.application.dto;

import com.propertyview.domain.model.*;

import java.util.List;

public record HotelDetails(
    Long id, String name, String description, String brand, Address address,
    Contacts contacts, ArrivalTime arrivalTime, List<String> amenities) {

    public static HotelDetails from(Hotel h) {

        return new HotelDetails(h.id(), h.name(), h.description(), h.brand(), h.address(), h.contacts(),
            h.arrivalTime(), h.amenities().stream().map(Amenity::name).toList());
    }
}
