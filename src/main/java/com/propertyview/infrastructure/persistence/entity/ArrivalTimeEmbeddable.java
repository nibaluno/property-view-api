package com.propertyview.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalTime;

/**
 * JPA embeddable representation of the {@link com.propertyview.domain.model.ArrivalTime} value object
 */
@Embeddable
public record ArrivalTimeEmbeddable(
    @Column(name = "check_in", nullable = false) LocalTime checkIn,
    @Column(name = "check_out") LocalTime checkOut) {}
