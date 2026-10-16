package com.propertyview.infrastructure.persistence.entity;


import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;


/**
 * JPA entity representing a hotel, including its embedded properties and amenities
 */
@Entity
@Table(name = "hotel")
public class HotelEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", length = 4000)
    private String description;
    @Column(name = "brand", nullable = false)
    private String brand;

    @Embedded private AddressEmbeddable address;
    @Embedded private ContactsEmbeddable contacts;
    @Embedded private ArrivalTimeEmbeddable arrivalTime;



    @ManyToMany
    @JoinTable(name = "hotel_amenity",
        joinColumns = @JoinColumn(name = "hotel_id"),
        inverseJoinColumns = @JoinColumn(name = "amenity_id"))
    @OrderColumn(name = "sort_order")
    private List<AmenityEntity> amenities = new ArrayList<>();


    protected HotelEntity() {}

    public HotelEntity(String name, String description, String brand, AddressEmbeddable address,
                       ContactsEmbeddable contacts, ArrivalTimeEmbeddable arrivalTime) {
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.address = address;
        this.contacts = contacts;
        this.arrivalTime = arrivalTime;
    }


    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getBrand() { return brand; }
    public AddressEmbeddable getAddress() { return address; }
    public ContactsEmbeddable getContacts() { return contacts; }
    public ArrivalTimeEmbeddable getArrivalTime() { return arrivalTime; }
    public List<AmenityEntity> getAmenities() { return amenities; }

}
