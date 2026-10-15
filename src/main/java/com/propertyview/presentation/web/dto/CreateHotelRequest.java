package com.propertyview.presentation.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record CreateHotelRequest(
    @NotBlank @Size(max = 255) String name,
    @Size(max = 4000) String description,
    @NotBlank @Size(max = 255) String brand,
    @NotNull @Valid AddressDto address,
    @NotNull @Valid ContactsDto contacts,
    @NotNull @Valid ArrivalTimeDto arrivalTime) {}
