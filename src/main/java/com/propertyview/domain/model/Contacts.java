// Contacts.java
package com.propertyview.domain.model;

import static com.propertyview.domain.model.Preconditions.requireText;

public record Contacts(String phone, String email) {
    public Contacts {
        phone = requireText(phone, "contacts.phone");
        email = requireText(email, "contacts.email");
    }
}
