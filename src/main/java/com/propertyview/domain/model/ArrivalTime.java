package com.propertyview.domain.model;

import java.time.LocalTime;

import static com.propertyview.domain.model.Preconditions.requireNonNull;

/**
 * a value object that defines the rules for hotel check-in and check-out
 */
public record ArrivalTime(LocalTime checkIn, LocalTime checkOut) {
    public ArrivalTime {
        requireNonNull(checkIn, "arrivalTime.checkIn");
    }
}
