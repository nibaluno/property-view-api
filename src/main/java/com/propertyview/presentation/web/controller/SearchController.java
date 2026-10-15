package com.propertyview.presentation.web.controller;

import com.propertyview.application.port.in.SearchHotelsUseCase;
import com.propertyview.domain.model.HotelSearchCriteria;
import com.propertyview.presentation.web.dto.HotelSummaryResponse;
import com.propertyview.presentation.web.mapper.HotelWebMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {
    private final SearchHotelsUseCase searchHotels;

    public SearchController(SearchHotelsUseCase searchHotels) { this.searchHotels = searchHotels; }

    @GetMapping
    public List<HotelSummaryResponse> search(
        @RequestParam(name = "name", required = false) String name,
        @RequestParam(name = "brand", required = false) String brand,
        @RequestParam(name = "city", required = false) String city,
        @RequestParam(name = "country", required = false) String country,
        @RequestParam(name = "amenities", required = false) List<String> amenities) {
        HotelSearchCriteria criteria = HotelSearchCriteria.of(name, brand, city, country, amenities);
        return searchHotels.search(criteria).stream().map(HotelWebMapper::toResponse).toList();
    }
}
