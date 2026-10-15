package com.propertyview.presentation.web.controller;

import com.propertyview.application.dto.HotelSummary;
import com.propertyview.application.port.in.CreateHotelUseCase;
import com.propertyview.presentation.web.dto.CreateHotelRequest;
import com.propertyview.presentation.web.dto.HotelSummaryResponse;
import com.propertyview.presentation.web.mapper.HotelWebMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/hotels")
public class HotelController {
    private final CreateHotelUseCase createHotel;

    public HotelController(CreateHotelUseCase createHotel) { this.createHotel = createHotel; }

    @PostMapping
    public ResponseEntity<HotelSummaryResponse> create(@Valid @RequestBody CreateHotelRequest request) {
        HotelSummary created = createHotel.create(HotelWebMapper.toCommand(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(HotelWebMapper.toResponse(created));
    }
}
