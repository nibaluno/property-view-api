package com.propertyview.presentation.web.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * web dto representing a brief summary of a hotel
 */
@JsonPropertyOrder({"id", "name", "description", "address", "phone"})
public record HotelSummaryResponse(Long id, String name, String description, String address, String phone) {}
