package com.propertyview.presentation.web.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "name", "description", "address", "phone"})
public record HotelSummaryResponse(Long id, String name, String description, String address, String phone) {}
