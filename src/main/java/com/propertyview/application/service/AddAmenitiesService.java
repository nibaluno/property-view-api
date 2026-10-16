package com.propertyview.application.service;

import com.propertyview.application.dto.AddAmenitiesCommand;
import com.propertyview.application.dto.HotelDetails;
import com.propertyview.application.port.in.AddAmenitiesUseCase;
import com.propertyview.domain.exception.HotelNotFoundException;
import com.propertyview.domain.model.Amenity;
import com.propertyview.domain.model.Hotel;
import com.propertyview.domain.port.HotelRepository;
import java.util.List;

/**
 * an application service that implements the {@link AddAmenitiesUseCase} input port
 * it coordinates the scenario of adding new amenities to an existing hotel aggregate.
 */
public class AddAmenitiesService implements AddAmenitiesUseCase {
    private final HotelRepository hotels;

    public AddAmenitiesService(HotelRepository hotels) { this.hotels = hotels; }


    @Override
    public HotelDetails addAmenities(AddAmenitiesCommand command) {
        Hotel hotel = hotels.findById(command.hotelId())
            .orElseThrow(() -> new HotelNotFoundException(command.hotelId()));
        List<Amenity> amenities = command.amenities().stream().map(Amenity::new).toList();
        hotel.addAmenities(amenities);
        return HotelDetails.from(hotels.save(hotel));
    }
}
