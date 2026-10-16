package com.propertyview.infrastructure.persistence.repository;

/**
 * spring data projection interface for fetching grouped histogram counts
 */
public interface CountRow {
    String getLabel();
    Long getTotal();
}
