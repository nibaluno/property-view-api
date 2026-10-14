package com.propertyview.domain.model;

import java.time.LocalTime;

import static com.propertyview.domain.model.Preconditions.requireNonNull;

public record ArrivalTime(LocalTime checkIn, LocalTime checkOut) {
    public ArrivalTime {
        requireNonNull(checkIn, "arrivalTime.checkIn");
    }
}
