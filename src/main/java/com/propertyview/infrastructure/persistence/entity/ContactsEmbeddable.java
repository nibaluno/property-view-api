package com.propertyview.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record ContactsEmbeddable(

    @Column(name = "phone", nullable = false, length = 64) String phone,
    @Column(name = "email", nullable = false) String email
) {}
