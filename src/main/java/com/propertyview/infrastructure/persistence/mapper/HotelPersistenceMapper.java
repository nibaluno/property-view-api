package com.propertyview.infrastructure.persistence.mapper;

import com.propertyview.domain.model.*;
import com.propertyview.infrastructure.persistence.entity.*;
import org.springframework.stereotype.Component;

@Component
public class HotelPersistenceMapper {
    private final ValueObjectMapper valueObjects;

    public HotelPersistenceMapper(ValueObjectMapper valueObjects) { this.valueObjects = valueObjects; }

    public HotelOverview toOverview(HotelEntity e) {
        return new HotelOverview(e.getId(), e.getName(), e.getDescription(),
            valueObjects.toDomain(e.getAddress()), e.getContacts().phone());
    }

    public Hotel toDomain(HotelEntity e) {

        return Hotel.restore(e.getId(), e.getName(), e.getDescription(), e.getBrand(),
            valueObjects.toDomain(e.getAddress()),
            valueObjects.toDomain(e.getContacts()),
            valueObjects.toDomain(e.getArrivalTime()),
            e.getAmenities().stream().map(a -> new Amenity(a.getName())).toList());
    }

    public HotelEntity toNewEntity(Hotel h) {

        return new HotelEntity(h.name(), h.description(), h.brand(),
            valueObjects.toEmbeddable(h.address()),
            valueObjects.toEmbeddable(h.contacts()),
            valueObjects.toEmbeddable(h.arrivalTime()));
    }

    public AmenityEntity toEntity(Amenity a) {
        return new AmenityEntity(a.name(), a.normalizedName());
    }
}
