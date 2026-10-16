package com.propertyview.domain.model;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static com.propertyview.domain.model.Preconditions.optionalText;

/**
 * a value object that encapsulates filtering criteria for hotel searches
 */
public record HotelSearchCriteria(String name, String brand, String city, String country, Set<Amenity> amenities)  {

    public HotelSearchCriteria {
        name = optionalText(name);
        brand = optionalText(brand);
        city = optionalText(city);
        country = optionalText(country);
        amenities = amenities == null ? Set.of() : Set.copyOf(amenities);
    }

    public static HotelSearchCriteria of(String name, String brand,
                                         String city, String country,
                                         Collection<String> amenityNames)
    {
        Set<Amenity> amenities = amenityNames == null ? Set.of() : amenityNames.stream()
            .filter(n -> n != null && !n.isBlank())
            .map(Amenity::new)
            .collect(Collectors.toSet());
        return new HotelSearchCriteria(name, brand, city, country, amenities);
    }

    public boolean isEmpty() {
        return name == null && brand == null && city == null && country == null && amenities.isEmpty();
    }
}
