package com.propertyview.application.histogram;

import java.util.Map;

/**
 * strategy interface for histogram statistics providers
 * each implementation encapsulates the logic for counting the number of hotels for a specific parameter
 */
public interface HistogramProvider {
    HistogramParam param();
    Map<String, Long> histogram();
}
