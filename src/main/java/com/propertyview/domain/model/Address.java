package com.propertyview.domain.model;

import static com.propertyview.domain.model.Preconditions.requirePositive;
import static com.propertyview.domain.model.Preconditions.requireText;

public record Address(int houseNumber, String street, String city, String country, String postCode) {
    public Address {
        requirePositive(houseNumber, "address.houseNumber");
        street = requireText(street, "address.street");
        city = requireText(city, "address.city");
        country = requireText(country, "address.country");
        postCode = requireText(postCode, "address.postCode");
    }


    public String toSingleLine() {
        return "%d %s, %s, %s, %s".formatted(houseNumber, street, city, postCode, country);
    }
    }
