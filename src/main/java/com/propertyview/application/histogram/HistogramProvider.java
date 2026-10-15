package com.propertyview.application.histogram;

import java.util.Map;

public interface HistogramProvider {
    HistogramParam param();
    Map<String, Long> histogram();
}
