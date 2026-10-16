package com.propertyview.application.histogram;

import java.util.Map;
import java.util.function.Supplier;

/**
 * a generic implementation of the {@link HistogramProvider} strategy,
 * which delegates data retrieval to a repository via a supplier function ({@link Supplier}).
 */
public final class RepositoryHistogramProvider implements HistogramProvider {
    private final HistogramParam param;
    private final Supplier<Map<String, Long>> source;

    public RepositoryHistogramProvider(HistogramParam param, Supplier<Map<String, Long>> source) {
        this.param = param;
        this.source = source;
    }

    @Override
    public HistogramParam param() { return param; }

    @Override
    public Map<String, Long> histogram() { return source.get(); }
}
