package com.propertyview.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record AddressEmbeddable(
    @Column(name = "house_number", nullable = false) int houseNumber,
    @Column(name = "street", nullable = false) String street,
    @Column(name = "city", nullable = false) String city,
    @Column(name = "country", nullable = false) String country,
    @Column(name = "post_code", nullable = false, length = 32) String postCode) {}
