package com.propertyview.presentation.web.controller;

import com.propertyview.application.dto.HotelSummary;
import com.propertyview.application.port.in.CreateHotelUseCase;
import com.propertyview.application.port.in.GetHotelUseCase;
import com.propertyview.application.port.in.GetHotelsUseCase;
import com.propertyview.presentation.web.dto.CreateHotelRequest;
import com.propertyview.presentation.web.dto.HotelDetailsResponse;
import com.propertyview.presentation.web.dto.HotelSummaryResponse;
import com.propertyview.presentation.web.mapper.HotelWebMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

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

    @GetMapping
    public List<HotelSummaryResponse> getAll() {
        return getHotels.getAll().stream().map(HotelWebMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public HotelDetailsResponse getById(@PathVariable("id") long id) {
        return HotelWebMapper.toResponse(getHotel.getById(id));
    }

    @PostMapping
    public ResponseEntity<HotelSummaryResponse> create(@Valid @RequestBody CreateHotelRequest request) {
        HotelSummary created = createHotel.create(HotelWebMapper.toCommand(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(HotelWebMapper.toResponse(created));
    }
}
