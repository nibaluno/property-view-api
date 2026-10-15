package com.propertyview.infrastructure.persistence.adapter;

import com.propertyview.domain.exception.HotelNotFoundException;
import com.propertyview.domain.model.*;
import com.propertyview.domain.port.HotelRepository;
import com.propertyview.infrastructure.persistence.mapper.HotelPersistenceMapper;
import com.propertyview.infrastructure.persistence.mapper.ValueObjectMapperImpl;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.Map;

import static com.propertyview.support.HotelFixtures.*;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import({HotelRepositoryAdapter.class, HotelPersistenceMapper.class, ValueObjectMapperImpl.class})
class HotelRepositoryAdapterTest {

    @Autowired HotelRepository repository;
    @Autowired EntityManager em;
    @Autowired JdbcTemplate jdbc;

    private void flushAndClear() { em.flush(); em.clear(); }

    @Test
    void liquibaseMigrationsAreApplied() {
        List<String> ids = jdbc.queryForList("select id from DATABASECHANGELOG", String.class);
        assertThat(ids).contains("001-create-hotel", "001-create-amenity", "001-create-hotel-amenity",
            "002-uq-amenity-normalized-name", "002-idx-hotel-city");
    }

    @Test
    void amenityNormalizedNameIsUniqueInDatabase() {
        jdbc.update("insert into amenity(name, normalized_name) values ('Pool', 'pool')");
        assertThatThrownBy(() -> jdbc.update("insert into amenity(name, normalized_name) values ('POOL', 'pool')"))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void savesNewHotelAndReadsItBack() {
        Hotel saved = repository.save(hotel("DoubleTree by Hilton Minsk", "Hilton", "Minsk", "Belarus"));
        flushAndClear();

        assertThat(saved.id()).isNotNull();
        Hotel loaded = repository.findById(saved.id()).orElseThrow();
        assertThat(loaded.name()).isEqualTo("DoubleTree by Hilton Minsk");
        assertThat(loaded.address().city()).isEqualTo("Minsk");
        assertThat(loaded.arrivalTime().checkOut()).isNotNull();
        assertThat(loaded.amenities()).isEmpty();
    }

    @Test
    void keepsAmenityInsertionOrder() {
        Hotel saved = repository.save(hotel("A", "Hilton", "Minsk", "Belarus"));
        saved.addAmenities(amenities("Free parking", "Free WiFi", "Concierge"));
        repository.save(saved);
        flushAndClear();

        assertThat(repository.findById(saved.id()).orElseThrow().amenities())
            .extracting(Amenity::name).containsExactly("Free parking", "Free WiFi", "Concierge");
    }

    @Test
    void reusesAmenityRowsCaseInsensitively() {
        Hotel a = repository.save(hotel("A", "Hilton", "Minsk", "Belarus"));
        a.addAmenities(amenities("Free WiFi"));
        repository.save(a);
        Hotel b = repository.save(hotel("B", "Marriott", "Moscow", "Russia"));
        b.addAmenities(amenities("free wifi"));
        repository.save(b);
        flushAndClear();

        assertThat(jdbc.queryForObject("select count(*) from amenity", Integer.class)).isEqualTo(1);
        assertThat(repository.findById(b.id()).orElseThrow().amenities())
            .extracting(Amenity::name).containsExactly("Free WiFi");
    }

    @Test
    void addingSameAmenityTwiceIsIdempotent() {
        Hotel h = repository.save(hotel("A", "Hilton", "Minsk", "Belarus"));
        h.addAmenities(amenities("Pool"));
        Hotel once = repository.save(h);
        once.addAmenities(amenities("pool"));
        repository.save(once);
        flushAndClear();

        assertThat(repository.findById(h.id()).orElseThrow().amenities()).hasSize(1);
    }

    @Test
    void findByIdReturnsEmptyForUnknownId() {
        assertThat(repository.findById(999_999L)).isEmpty();
    }

    @Test
    void savingUnknownExistingHotelFails() {
        Hotel ghost = Hotel.restore(999_999L, "Ghost", null, "None",
            new Address(1, "St", "X", "Y", "1"), new Contacts("1", "g@x.com"),
            new ArrivalTime(java.time.LocalTime.NOON, null), List.of());
        assertThatThrownBy(() -> repository.save(ghost)).isInstanceOf(HotelNotFoundException.class);
    }

    private void seed() {
        Hotel h1 = repository.save(hotel("DoubleTree by Hilton Minsk", "Hilton", "Minsk", "Belarus"));
        h1.addAmenities(amenities("Free WiFi", "Free parking"));
        repository.save(h1);
        Hotel h2 = repository.save(hotel("Hilton Moscow Leningradskaya", "Hilton", "Moscow", "Russia"));
        h2.addAmenities(amenities("Free WiFi"));
        repository.save(h2);
        repository.save(hotel("Hotel Mogilev", "Local", "Mogilev", "Belarus"));
        flushAndClear();
    }

    @Test
    void listsOverviewsOrderedById() {
        seed();
        List<HotelOverview> all = repository.findAllOverviews();
        assertThat(all).hasSize(3);
        assertThat(all).extracting(HotelOverview::id).isSorted();
        assertThat(all.get(0).address().toSingleLine()).isEqualTo("1 Main Street, Minsk, 100000, Belarus");
    }

    @Test
    void searchesByCityIgnoringCase() {
        seed();
        assertThat(repository.search(HotelSearchCriteria.of(null, null, "minsk", null, null)))
            .extracting(HotelOverview::name).containsExactly("DoubleTree by Hilton Minsk");
    }

    @Test
    void searchesByNameContaining() {
        seed();
        assertThat(repository.search(HotelSearchCriteria.of("HILTON", null, null, null, null))).hasSize(2);
    }

    @Test
    void searchesByCountryAndBrandTogether() {
        seed();
        assertThat(repository.search(HotelSearchCriteria.of(null, "hilton", null, "belarus", null)))
            .extracting(HotelOverview::name).containsExactly("DoubleTree by Hilton Minsk");
    }

    @Test
    void searchByMultipleAmenitiesRequiresAll() {
        seed();
        assertThat(repository.search(HotelSearchCriteria.of(null, null, null, null,
            List.of("free wifi", "Free parking")))).hasSize(1);
        assertThat(repository.search(HotelSearchCriteria.of(null, null, null, null,
            List.of("Free WiFi")))).hasSize(2);
    }

    @Test
    void emptyCriteriaReturnsEverything() {
        seed();
        assertThat(repository.search(HotelSearchCriteria.of(null, null, null, null, null))).hasSize(3);
    }

    @Test
    void searchTreatsLikeWildcardsLiterally() {
        seed();
        assertThat(repository.search(HotelSearchCriteria.of("%", null, null, null, null))).isEmpty();
    }

    @Test
    void countsGroupedByEachParameter() {
        seed();
        assertThat(repository.countByBrand()).isEqualTo(Map.of("Hilton", 2L, "Local", 1L));
        assertThat(repository.countByCity()).isEqualTo(Map.of("Minsk", 1L, "Moscow", 1L, "Mogilev", 1L));
        assertThat(repository.countByCountry()).isEqualTo(Map.of("Belarus", 2L, "Russia", 1L));
        assertThat(repository.countByAmenity()).isEqualTo(Map.of("Free WiFi", 2L, "Free parking", 1L));
    }
}
