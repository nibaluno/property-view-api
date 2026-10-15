package com.propertyview.presentation.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

@JsonPropertyOrder({"checkIn", "checkOut"})
public record ArrivalTimeDto(
    @NotNull @JsonFormat(pattern = "HH:mm") LocalTime checkIn,
    @JsonFormat(pattern = "HH:mm") LocalTime checkOut) {}
