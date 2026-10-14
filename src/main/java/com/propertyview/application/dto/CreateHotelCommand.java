package com.propertyview.application.dto;

import com.propertyview.domain.model.Address;
import com.propertyview.domain.model.ArrivalTime;
import com.propertyview.domain.model.Contacts;

public record CreateHotelCommand(String name, String description, String brand, Address address, Contacts contacts, ArrivalTime arrivalTime) {}
