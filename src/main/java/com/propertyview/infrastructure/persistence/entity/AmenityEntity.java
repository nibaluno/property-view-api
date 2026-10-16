package com.propertyview.infrastructure.persistence.entity;

import jakarta.persistence.*;

/**
 * JPA entity representing a hotel amenity with a unique normalized name
 */
@Entity
@Table(name = "amenity")
public class AmenityEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "normalized_name", nullable = false)
    private String normalizedName;

    protected AmenityEntity() {}


    public AmenityEntity(String name, String normalizedName) {
        this.name = name;
        this.normalizedName = normalizedName;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getNormalizedName() { return normalizedName; }
}
