// Hotel.java: агрегат
package com.propertyview.domain.model;

import java.util.*;
import static com.propertyview.domain.model.Preconditions.*;

public final class Hotel {

    private final Long id;
    private final String name;
    private final String description;
    private final String brand;
    private final Address address;
    private final Contacts contacts;
    private final ArrivalTime arrivalTime;
    private final Set<Amenity> amenities;


    private Hotel(Long id, String name, String description, String brand, Address address,
        Contacts contacts, ArrivalTime arrivalTime, Collection<Amenity> amenities) {

        this.id = id;
        this.name = requireText(name, "name");
        this.description = optionalText(description);
        this.brand = requireText(brand, "brand");
        this.address = requireNonNull(address, "address");
        this.contacts = requireNonNull(contacts, "contacts");
        this.arrivalTime = requireNonNull(arrivalTime, "arrivalTime");
        this.amenities = new LinkedHashSet<>(amenities);
    }


    public static Hotel create(String name, String description, String brand,
                               Address address, Contacts contacts, ArrivalTime arrivalTime) {
        return new Hotel(null, name, description, brand, address, contacts, arrivalTime, List.of());
    }


    public static Hotel restore(Long id, String name, String description, String brand, Address address,
                                Contacts contacts, ArrivalTime arrivalTime, Collection<Amenity> amenities) {
        return new Hotel(requireNonNull(id, "id"), name, description, brand,
            address, contacts, arrivalTime, amenities);
    }


    public boolean addAmenities(Collection<Amenity> toAdd) {
        requireNonNull(toAdd, "amenities");
        boolean changed = false;
        for (Amenity a : toAdd) changed |= amenities.add(requireNonNull(a, "amenity"));
        return changed;
    }

    public HotelOverview toOverview() {
        return new HotelOverview(id, name, description, address, contacts.phone());
    }



    public Long id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public String brand() { return brand; }
    public Address address() { return address; }
    public Contacts contacts() { return contacts; }
    public ArrivalTime arrivalTime() { return arrivalTime; }
    public Set<Amenity> amenities() { return Collections.unmodifiableSet(amenities); }
}
