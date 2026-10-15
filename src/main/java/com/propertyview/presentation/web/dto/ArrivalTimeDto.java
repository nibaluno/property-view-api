package com.propertyview.presentation.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

@JsonPropertyOrder({"checkIn", "checkOut"})
public record ArrivalTimeDto(
    @Schema(type = "string", example = "14:00", pattern = "^([01]\\d|2[0-3]):[0-5]\\d$")
    @NotNull @JsonFormat(pattern = "HH:mm") LocalTime checkIn,

    @Schema(type = "string", example = "12:00", pattern = "^([01]\\d|2[0-3]):[0-5]\\d$")
    @JsonFormat(pattern = "HH:mm") LocalTime checkOut) {}
