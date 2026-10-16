package com.propertyview.infrastructure.persistence.adapter;

import com.propertyview.domain.model.Hotel;
import com.propertyview.domain.model.HotelSearchCriteria;
import com.propertyview.domain.port.HotelRepository;
import com.propertyview.infrastructure.persistence.mapper.HotelPersistenceMapper;
import com.propertyview.infrastructure.persistence.mapper.ValueObjectMapperImpl;
import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import java.util.ArrayList;
import java.util.List;

import static com.propertyview.support.HotelFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@Import({HotelRepositoryAdapter.class, HotelPersistenceMapper.class, ValueObjectMapperImpl.class})
class QueryCountTest {

    @Autowired HotelRepository repository;
    @Autowired EntityManager em;

    private List<Long> seed(int hotels) {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < hotels; i++) {
            Hotel h = repository.save(hotel("Hotel " + i, "Brand" + (i % 2), "City" + i, "Country"));
            h.addAmenities(amenities("Free WiFi", "Parking", "Pool " + i));
            repository.save(h);
            ids.add(h.id());
        }
        em.flush();
        em.clear();
        return ids;
    }

    private long statementsDuring(Runnable action) {
        em.clear();
        Statistics statistics = em.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
        statistics.clear();
        action.run();
        return statistics.getPrepareStatementCount();
    }

    @Test
    void listingDoesNotQueryAmenitiesPerHotel() {
        seed(10);
        assertThat(statementsDuring(() -> repository.findAllOverviews())).isEqualTo(1);
    }

    @Test
    void searchIsASingleQueryEvenWithAmenityFilters() {
        seed(10);
        HotelSearchCriteria criteria = HotelSearchCriteria.of("hotel", "brand1", null, "country", List.of("free wifi", "parking"));
        assertThat(statementsDuring(() -> repository.search(criteria))).isEqualTo(1);
    }

    @Test
    void detailsLoadHotelWithAmenitiesInOneQuery() {
        List<Long> ids = seed(10);
        assertThat(statementsDuring(() -> repository.findById(ids.get(0)).orElseThrow())).isEqualTo(1);
    }

    @Test
    void histogramsAreSingleAggregateQueries() {
        seed(10);
        assertThat(statementsDuring(() -> repository.countByBrand())).isEqualTo(1);
        assertThat(statementsDuring(() -> repository.countByCity())).isEqualTo(1);
        assertThat(statementsDuring(() -> repository.countByCountry())).isEqualTo(1);
        assertThat(statementsDuring(() -> repository.countByAmenity())).isEqualTo(1);
    }
}
