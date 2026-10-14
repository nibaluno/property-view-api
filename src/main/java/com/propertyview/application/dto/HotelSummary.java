package com.propertyview.application.dto;

import com.propertyview.domain.model.Hotel;
import com.propertyview.domain.model.HotelOverview;

public record HotelSummary(Long id, String name, String description, String address, String phone) {
    public static HotelSummary from(HotelOverview o) {
        return new HotelSummary(o.id(), o.name(), o.description(), o.address().toSingleLine(), o.phone());
    }
    public static HotelSummary from(Hotel h) { return from(h.toOverview()); }
}
