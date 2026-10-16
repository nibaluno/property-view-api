package com.propertyview.presentation.web.controller;

import com.propertyview.application.dto.AddAmenitiesCommand;
import com.propertyview.application.port.in.AddAmenitiesUseCase;
import com.propertyview.domain.model.Amenity;
import com.propertyview.presentation.web.dto.HotelDetailsResponse;
import com.propertyview.presentation.web.mapper.HotelWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST controller for appending amenities to an existing hotel
 */
@Tag(name = "Hotels")
@RestController
@RequestMapping("/hotels/{id}/amenities")
public class HotelAmenityController {

    private final AddAmenitiesUseCase addAmenities;

    public HotelAmenityController(AddAmenitiesUseCase addAmenities) { this.addAmenities = addAmenities; }

    @Operation(summary = "Add amenities to a hotel",
        description = "Adds amenities to the hotel. Duplicates (case-insensitive) are ignored, insertion order is kept.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
        examples = @ExampleObject(value = "[\"Free parking\", \"Free WiFi\", \"Concierge\"]")))

    @ApiResponse(responseCode = "200", description = "Updated hotel details")
    @ApiResponse(responseCode = "400", description = "Blank or too long amenity name (application/problem+json)")
    @ApiResponse(responseCode = "404", description = "Hotel not found (application/problem+json)")
    @PostMapping


    public HotelDetailsResponse add(
        @Parameter(description = "Hotel id", example = "1") @PathVariable("id") long id,
        @RequestBody @NotNull @Size(max = 100)
        List<@NotBlank @Size(max = Amenity.MAX_LENGTH) String> amenities) {
        return HotelWebMapper.toResponse(addAmenities.addAmenities(new AddAmenitiesCommand(id, amenities)));
    }
}
