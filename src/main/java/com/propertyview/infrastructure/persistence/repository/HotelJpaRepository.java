package com.propertyview.infrastructure.persistence.repository;

import com.propertyview.infrastructure.persistence.entity.HotelEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;


public  interface HotelJpaRepository extends JpaRepository<HotelEntity, Long>, JpaSpecificationExecutor<HotelEntity>{

    @Query("select h from HotelEntity h left join fetch h.amenities where h.id = :id")
    Optional<HotelEntity> findByIdWithAmenities(@Param("id") Long id);

    @Query("select h.brand as label, count(h) as total from HotelEntity h group by h.brand")
    List<CountRow> countByBrand();

    @Query("select h.address.city as label, count(h) as total from HotelEntity h group by h.address.city")
    List<CountRow> countByCity();

    @Query("select h.address.country as label, count(h) as total from HotelEntity h group by h.address.country")
    List<CountRow> countByCountry();

    @Query("select a.name as label, count(distinct h.id) as total from HotelEntity h join h.amenities a group by a.name")
    List<CountRow> countByAmenity();
}
