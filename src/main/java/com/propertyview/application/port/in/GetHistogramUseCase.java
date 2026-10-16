package com.propertyview.application.port.in;

import com.propertyview.application.histogram.HistogramParam;

import java.util.Map;

/**
 * input port (use case interface)
 * responsible for retrieving aggregated statistics (histograms) on the number of hotels,
 * grouped by a specified parameter (brand, city, country, amenities)
 */
public interface GetHistogramUseCase {
    Map<String, Long> getHistogram(HistogramParam param);
}
