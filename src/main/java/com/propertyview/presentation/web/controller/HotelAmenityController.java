package com.propertyview.presentation.web.controller;

import com.propertyview.application.dto.AddAmenitiesCommand;
import com.propertyview.application.port.in.AddAmenitiesUseCase;
import com.propertyview.domain.model.Amenity;
import com.propertyview.presentation.web.dto.HotelDetailsResponse;
import com.propertyview.presentation.web.mapper.HotelWebMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/hotels/{id}/amenities")
public class HotelAmenityController {

    private final AddAmenitiesUseCase addAmenities;

    public HotelAmenityController(AddAmenitiesUseCase addAmenities) { this.addAmenities = addAmenities; }
    @PostMapping
    public HotelDetailsResponse add(
        @PathVariable("id") long id,
        @RequestBody @NotNull @Size(max = 100)
        List<@NotBlank @Size(max = Amenity.MAX_LENGTH) String> amenities) {
        return HotelWebMapper.toResponse(addAmenities.addAmenities(new AddAmenitiesCommand(id, amenities)));
    }
}
