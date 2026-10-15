package com.propertyview.presentation.web.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;

@JsonPropertyOrder({"phone", "email"})
public record ContactsDto(
    @NotBlank @Size(max = 64) String phone,
    @NotBlank @Email @Size(max = 255) String email) {}
