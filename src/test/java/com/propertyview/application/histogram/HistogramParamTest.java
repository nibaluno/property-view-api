package com.propertyview.application.histogram;

import com.propertyview.application.HistogramParam;
import com.propertyview.domain.exception.UnsupportedHistogramParamException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class HistogramParamTest {
    @Test void parsesCaseInsensitively() {
        Assertions.assertThat(HistogramParam.fromValue(" City ")).isEqualTo(HistogramParam.CITY);
        assertThat(HistogramParam.fromValue("AMENITIES")).isEqualTo(HistogramParam.AMENITIES);
    }
    @Test void unknownValueListsSupported() {
        assertThatThrownBy(() -> HistogramParam.fromValue("stars"))
            .isInstanceOf(UnsupportedHistogramParamException.class)
            .hasMessageContaining("brand, city, country, amenities");
    }
}
