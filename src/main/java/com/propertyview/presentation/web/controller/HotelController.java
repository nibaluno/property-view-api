package com.propertyview.presentation.web.controller;

import com.propertyview.application.dto.HotelSummary;
import com.propertyview.application.port.in.CreateHotelUseCase;
import com.propertyview.application.port.in.GetHotelUseCase;
import com.propertyview.application.port.in.GetHotelsUseCase;
import com.propertyview.presentation.web.dto.CreateHotelRequest;
import com.propertyview.presentation.web.dto.HotelDetailsResponse;
import com.propertyview.presentation.web.dto.HotelSummaryResponse;
import com.propertyview.presentation.web.mapper.HotelWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

/**
 * REST controller for creating hotels and retrieving their lists or details
 */
@Tag(name = "Hotels", description = "Create hotels and read their details")
@RestController
@RequestMapping("/hotels")
public class HotelController {
    private final CreateHotelUseCase createHotel;
    private final GetHotelsUseCase getHotels;
    private final GetHotelUseCase getHotel;

    public HotelController(CreateHotelUseCase createHotel, GetHotelsUseCase getHotels, GetHotelUseCase getHotel) {
        this.createHotel = createHotel;
        this.getHotels = getHotels;
        this.getHotel = getHotel;
    }

    @Operation(summary = "List all hotels", description = "Brief information about every hotel, ordered by id.")
    @GetMapping
    public List<HotelSummaryResponse> getAll() {
        return getHotels.getAll().stream().map(HotelWebMapper::toResponse).toList();
    }

    @Operation(summary = "Get hotel details", description = "Extended information about one hotel, including its amenities.")
    @ApiResponse(responseCode = "200", description = "Hotel found")
    @ApiResponse(responseCode = "404", description = "Hotel not found (application/problem+json)")
    @GetMapping("/{id}")
    public HotelDetailsResponse getById(@Parameter(description = "Hotel id", example = "1") @PathVariable("id") long id) {
        return HotelWebMapper.toResponse(getHotel.getById(id));
    }

    @Operation(summary = "Create a hotel", description = "Creates a hotel without amenities; add them via POST /hotels/{id}/amenities.")
    @ApiResponse(responseCode = "201", description = "Created; the Location header points to the new hotel")
    @ApiResponse(responseCode = "400", description = "Validation failed (application/problem+json)")
    @PostMapping
    public ResponseEntity<HotelSummaryResponse> create(@Valid @RequestBody CreateHotelRequest request) {
        HotelSummary created = createHotel.create(HotelWebMapper.toCommand(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(HotelWebMapper.toResponse(created));
    }
}
