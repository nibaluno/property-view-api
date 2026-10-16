package com.propertyview.presentation.web.mapper;

import com.propertyview.application.dto.*;
import com.propertyview.domain.model.*;
import com.propertyview.presentation.web.dto.*;

/**
 * manual mapper converting between web request/response dtos and application commands/models
 */
public final class HotelWebMapper {
    private HotelWebMapper() {}


    public static CreateHotelCommand toCommand(CreateHotelRequest r) {
        AddressDto a = r.address();
        ContactsDto c = r.contacts();
        ArrivalTimeDto t = r.arrivalTime();
        return new CreateHotelCommand(r.name(), r.description(), r.brand(),
            new Address(a.houseNumber(), a.street(), a.city(), a.country(), a.postCode()),
            new Contacts(c.phone(), c.email()),
            new ArrivalTime(t.checkIn(), t.checkOut()));
    }


    public static HotelSummaryResponse toResponse(HotelSummary s) {
        return new HotelSummaryResponse(s.id(), s.name(), s.description(), s.address(), s.phone());
    }


    public static HotelDetailsResponse toResponse(HotelDetails d) {
        Address a = d.address();
        Contacts c = d.contacts();
        ArrivalTime t = d.arrivalTime();

        return new HotelDetailsResponse(d.id(), d.name(), d.description(), d.brand(),
            new AddressDto(a.houseNumber(), a.street(), a.city(), a.country(), a.postCode()),
            new ContactsDto(c.phone(), c.email()),
            new ArrivalTimeDto(t.checkIn(), t.checkOut()),
            d.amenities());
    }
}
