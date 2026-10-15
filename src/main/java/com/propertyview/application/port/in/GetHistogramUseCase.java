package com.propertyview.application.port.in;

import com.propertyview.application.histogram.HistogramParam;

import java.util.Map;

public interface GetHistogramUseCase {
    Map<String, Long> getHistogram(HistogramParam param);
}
