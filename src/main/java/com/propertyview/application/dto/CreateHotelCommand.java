package com.propertyview.application.dto;

import com.propertyview.domain.model.Address;
import com.propertyview.domain.model.ArrivalTime;
import com.propertyview.domain.model.Contacts;

/**
 * application-level command object containing all necessary parameters
 * required to execute the hotel creation use case
 */
public record CreateHotelCommand(String name, String description, String brand, Address address, Contacts contacts, ArrivalTime arrivalTime) {}
