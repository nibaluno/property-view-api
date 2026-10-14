package com.propertyview.domain.port;
import com.propertyview.domain.model.*;
import java.util.*;

public interface HotelRepository {
    Hotel save(Hotel hotel);
    Optional<Hotel> findById(long id);
    List<HotelOverview> findAllOverviews();
    List<HotelOverview> search(HotelSearchCriteria criteria);
    Map<String, Long> countByBrand();
    Map<String, Long> countByCity();
    Map<String, Long> countByCountry();
    Map<String, Long> countByAmenity();
}
