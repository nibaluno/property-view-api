package com.propertyview.domain.exception;

import java.util.Collection;

public class UnsupportedHistogramParamException extends DomainException {
    public UnsupportedHistogramParamException(String param, Collection<String> supported) {
        super("Unsupported histogram parameter '" + param + "'. Supported: " + String.join(", ", supported));
    }
}
