package com.propertyview.presentation.web.controller;

import com.propertyview.application.histogram.HistogramParam;
import com.propertyview.application.port.in.GetHistogramUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/histogram")
public class HistogramController {
    private final GetHistogramUseCase histogram;

    public HistogramController(GetHistogramUseCase histogram) { this.histogram = histogram; }

    @GetMapping("/{param}")
    public Map<String, Long> get(@PathVariable("param") String param) {
        return histogram.getHistogram(HistogramParam.fromValue(param));
    }
}
