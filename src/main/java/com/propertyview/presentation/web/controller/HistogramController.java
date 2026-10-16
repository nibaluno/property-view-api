package com.propertyview.presentation.web.controller;

import com.propertyview.application.histogram.HistogramParam;
import com.propertyview.application.port.in.GetHistogramUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * REST controller for retrieving hotel counts grouped by specific attributes
 */
@Tag(name = "Histogram", description = "Hotel counts grouped by an attribute")
@RestController
@RequestMapping("/histogram")
public class HistogramController {
    private final GetHistogramUseCase histogram;

    public HistogramController(GetHistogramUseCase histogram) { this.histogram = histogram; }

    @Operation(summary = "Count hotels grouped by an attribute",
        description = "Returns a map {value: number of hotels}. For amenities: {amenity: number of hotels having it}.")
    @ApiResponse(responseCode = "200", description = "Counts, keys sorted alphabetically")
    @ApiResponse(responseCode = "400", description = "Unsupported parameter (application/problem+json)")
    @GetMapping("/{param}")
    public Map<String, Long> get(
        @Parameter(description = "Attribute to group by", example = "city",
            schema = @Schema(allowableValues = {"brand", "city", "country", "amenities"}))
        @PathVariable("param") String param) {
        return histogram.getHistogram(HistogramParam.fromValue(param));
    }
}
