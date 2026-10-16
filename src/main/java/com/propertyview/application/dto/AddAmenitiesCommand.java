package com.propertyview.application.dto;

import java.util.List;

/**
 * application-level command object carrying the target hotel identifier
 * and the list of raw amenity names to be added in the "Add Amenities" use case
 */
public record AddAmenitiesCommand(long hotelId, List<String> amenities) {}
