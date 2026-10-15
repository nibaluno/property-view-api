package com.propertyview.presentation.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record CreateHotelRequest(
    @Schema(example = "DoubleTree by Hilton Minsk") @NotBlank @Size(max = 255) String name,
    @Schema(example = "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms ...") @Size(max = 4000) String description,
    @Schema(example = "Hilton") @NotBlank @Size(max = 255) String brand,
    @NotNull @Valid AddressDto address,
    @NotNull @Valid ContactsDto contacts,
    @NotNull @Valid ArrivalTimeDto arrivalTime) {}
