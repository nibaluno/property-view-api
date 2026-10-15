package com.propertyview.application.service;

import com.propertyview.application.histogram.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class HistogramServiceTest {

    private static HistogramProvider provider(HistogramParam param, Map<String, Long> data) {
        return new RepositoryHistogramProvider(param, () -> data);
    }

    private static List<HistogramProvider> allProviders() {
        List<HistogramProvider> all = new ArrayList<>();
        all.add(provider(HistogramParam.BRAND, Map.of("Hilton", 2L)));
        all.add(provider(HistogramParam.CITY, Map.of("Minsk", 1L)));
        all.add(provider(HistogramParam.COUNTRY, Map.of("Belarus", 1L)));
        all.add(provider(HistogramParam.AMENITIES, Map.of("Free WiFi", 20L)));
        return all;
    }

    @Test
    void dispatchesToProviderOfRequestedParam() {
        HistogramService service = new HistogramService(allProviders());
        assertThat(service.getHistogram(HistogramParam.CITY)).containsEntry("Minsk", 1L);
        assertThat(service.getHistogram(HistogramParam.AMENITIES)).containsEntry("Free WiFi", 20L);
    }

    @Test
    void failsFastWhenProviderIsMissing() {
        List<HistogramProvider> incomplete = allProviders().subList(0, 3);   // без AMENITIES
        assertThatThrownBy(() -> new HistogramService(incomplete))
            .isInstanceOf(IllegalStateException.class).hasMessageContaining("AMENITIES");
    }

    @Test
    void failsFastOnDuplicateProviders() {
        List<HistogramProvider> duplicated = allProviders();
        duplicated.add(provider(HistogramParam.CITY, Map.of()));
        assertThatThrownBy(() -> new HistogramService(duplicated))
            .isInstanceOf(IllegalStateException.class).hasMessageContaining("CITY");
    }
}
