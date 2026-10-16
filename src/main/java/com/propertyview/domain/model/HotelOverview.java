package com.propertyview.domain.model;

/**
 * a projection of a hotel, optimized for display in lists and search results
 */
public record HotelOverview(Long id, String name, String description, Address address, String phone) {}
