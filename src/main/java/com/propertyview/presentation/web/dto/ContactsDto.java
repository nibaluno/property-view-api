package com.propertyview.presentation.web.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@JsonPropertyOrder({"phone", "email"})
public record ContactsDto(
    @Schema(example = "+375 17 309-80-00") @NotBlank @Size(max = 64) String phone,
    @Schema(example = "doubletreeminsk.info@hilton.com") @NotBlank @Email @Size(max = 255) String email) {}
