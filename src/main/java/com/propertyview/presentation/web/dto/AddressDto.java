package com.propertyview.presentation.web.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@JsonPropertyOrder({"houseNumber", "street", "city", "country", "postCode"})
public record AddressDto(

    @Schema(example = "9") @NotNull @Positive Integer houseNumber,
    @Schema(example = "Pobediteley Avenue") @NotBlank @Size(max = 255) String street,
    @Schema(example = "Minsk") @NotBlank @Size(max = 255) String city,
    @Schema(example = "Belarus") @NotBlank @Size(max = 255) String country,
    @Schema(example = "220004") @NotBlank @Size(max = 32) String postCode) {}
