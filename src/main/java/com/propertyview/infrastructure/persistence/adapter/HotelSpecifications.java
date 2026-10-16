package com.propertyview.infrastructure.persistence.adapter;

import com.propertyview.domain.model.Amenity;
import com.propertyview.domain.model.HotelSearchCriteria;
import com.propertyview.infrastructure.persistence.entity.AmenityEntity;
import com.propertyview.infrastructure.persistence.entity.HotelEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * builds JPA criteria queries for dynamic hotel search based on {@link HotelSearchCriteria}.
 */
final class HotelSpecifications {
    private HotelSpecifications() {}

    static Specification<HotelEntity> from(HotelSearchCriteria c) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (c.name() != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + escapeLike(lower(c.name())) + "%", '\\'));
            }
            if (c.brand() != null) {
                predicates.add(cb.equal(cb.lower(root.get("brand")), lower(c.brand())));
            }
            if (c.city() != null) {
                predicates.add(cb.equal(cb.lower(root.get("address").get("city")), lower(c.city())));
            }
            if (c.country() != null) {
                predicates.add(cb.equal(cb.lower(root.get("address").get("country")), lower(c.country())));
            }
            for (Amenity amenity : c.amenities()) {
                Subquery<Long> sub = query.subquery(Long.class);
                Root<HotelEntity> subRoot = sub.from(HotelEntity.class);
                Join<HotelEntity, AmenityEntity> join = subRoot.join("amenities");
                sub.select(subRoot.get("id")).where(
                    cb.equal(subRoot.get("id"), root.get("id")),
                    cb.equal(join.get("normalizedName"), amenity.normalizedName()));
                predicates.add(cb.exists(sub));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static String lower(String s) { return s.toLowerCase(Locale.ROOT); }

    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
