package com.propertyview.application;

import java.util.Map;

public interface HistogramProvider {
    HistogramParam param();
    Map<String, Long> histogram();
}
