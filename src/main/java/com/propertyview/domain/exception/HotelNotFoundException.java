package com.propertyview.domain.exception;

public class HotelNotFoundException extends DomainException {

    private final long hotelId;
    public HotelNotFoundException(long hotelId) {
        super("Hotel with id " + hotelId + " was not found");
        this.hotelId = hotelId;
    }
    public long hotelId() { return hotelId; }
}
