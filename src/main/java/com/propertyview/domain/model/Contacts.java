package com.propertyview.domain.model;

/**
 * a value object containing a hotel's contact details
 */
import static com.propertyview.domain.model.Preconditions.requireText;

public record Contacts(String phone, String email) {
    public Contacts {
        phone = requireText(phone, "contacts.phone");
        email = requireText(email, "contacts.email");
    }
}
