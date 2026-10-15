package com.propertyview.infrastructure.persistence.repository;

import com.propertyview.infrastructure.persistence.entity.AmenityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;


public interface AmenityJpaRepository extends JpaRepository<AmenityEntity, Long> {
    List<AmenityEntity> findByNormalizedNameIn(Collection<String> normalizedNames);
}
