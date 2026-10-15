package com.propertyview.presentation.web.controller;

import com.propertyview.application.port.in.SearchHotelsUseCase;
import com.propertyview.domain.model.HotelSearchCriteria;
import com.propertyview.presentation.web.dto.HotelSummaryResponse;
import com.propertyview.presentation.web.mapper.HotelWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Tag(name = "Search", description = "Find hotels by attributes")
@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchHotelsUseCase searchHotels;


    public SearchController(SearchHotelsUseCase searchHotels) { this.searchHotels = searchHotels; }

    @Operation(summary = "Search hotels",
        description = "All filters are optional and combined with AND. brand/city/country: case-insensitive equality; "
            + "name: case-insensitive substring; amenities: the hotel must have all of them. "
            + "Returns the same brief form as GET /hotels.")
    @GetMapping
    public List<HotelSummaryResponse> search(

        @Parameter(description = "Part of the hotel name", example = "double")
        @RequestParam(name = "name", required = false) String name,


        @Parameter(description = "Brand", example = "Hilton")
        @RequestParam(name = "brand", required = false) String brand,


        @Parameter(description = "City", example = "minsk")
        @RequestParam(name = "city", required = false) String city,


        @Parameter(description = "Country", example = "Belarus")
        @RequestParam(name = "country", required = false) String country,

        @Parameter(description = "Amenity; repeat the parameter or use a comma-separated list", example = "Free WiFi")
        @RequestParam(name = "amenities", required = false) List<String> amenities) {
        HotelSearchCriteria criteria = HotelSearchCriteria.of(name, brand, city, country, amenities);
        return searchHotels.search(criteria).stream().map(HotelWebMapper::toResponse).toList();
    }
}
