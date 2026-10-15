package com.propertyview.support;

import com.propertyview.domain.model.*;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public final class HotelFixtures {
    private HotelFixtures() {}

    public static Hotel hotel(String name, String brand, String city, String country) {
        return Hotel.create(name, "Description of " + name, brand,
            new Address(1, "Main Street", city, country, "100000"),
            new Contacts("+375 17 000-00-00", name.replace(' ', '.').toLowerCase() + "@example.com"),
            new ArrivalTime(LocalTime.of(14, 0), LocalTime.of(12, 0)));
    }

    public static List<Amenity> amenities(String... names) {
        return Arrays.stream(names).map(Amenity::new).toList();
    }
}
