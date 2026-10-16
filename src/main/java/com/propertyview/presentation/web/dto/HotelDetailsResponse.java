package com.propertyview.presentation.web.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

/**
 * web dto representing the detailed view of a hotel, including its amenities
 */
@JsonPropertyOrder({"id", "name", "description", "brand", "address", "contacts", "arrivalTime", "amenities"})
public record HotelDetailsResponse(
    Long id, String name, String description, String brand,
    AddressDto address, ContactsDto contacts, ArrivalTimeDto arrivalTime,
    List<String> amenities)
{}
