package com.propertyview.application.dto;

import java.util.List;

public record AddAmenitiesCommand(long hotelId, List<String> amenities) {}
