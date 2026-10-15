package com.propertyview.presentation.web.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;


@JsonPropertyOrder({"houseNumber", "street", "city", "country", "postCode"})
public record AddressDto(
    @NotNull @Positive Integer houseNumber,
    @NotBlank @Size(max = 255) String street,
    @NotBlank @Size(max = 255) String city,
    @NotBlank @Size(max = 255) String country,
    @NotBlank @Size(max = 32) String postCode)
{}
