# property-view-api

RESTful API for managing hotels. Java 21, Spring Boot 4, Spring Data JPA, Liquibase, H2.

## Requirements
- JDK 21
- Maven 3.9+

## Run
    mvn spring-boot:run

The app starts on http://localhost:8092 with an empty in-memory H2 database.
All endpoints share the prefix `/property-view`.

- Swagger UI: http://localhost:8092/property-view/swagger-ui.html
- OpenAPI JSON: http://localhost:8092/property-view/v3/api-docs

## API
| Method | Path | Description |
|---|---|---|
| GET | /property-view/hotels | List of hotels (brief) |
| GET | /property-view/hotels/{id} | Hotel details |
| GET | /property-view/search | Search by name, brand, city, country, amenities |
| POST | /property-view/hotels | Create a hotel |
| POST | /property-view/hotels/{id}/amenities | Add amenities to a hotel |
| GET | /property-view/histogram/{param} | Count of hotels grouped by brand, city, country or amenities |

## Examples

Create a hotel (`description` and `arrivalTime.checkOut` are optional):

    curl -i -X POST http://localhost:8092/property-view/hotels \
      -H 'Content-Type: application/json' -d '{
      "name": "DoubleTree by Hilton Minsk",
      "description": "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms ...",
      "brand": "Hilton",
      "address": {"houseNumber": 9, "street": "Pobediteley Avenue", "city": "Minsk", "country": "Belarus", "postCode": "220004"},
      "contacts": {"phone": "+375 17 309-80-00", "email": "doubletreeminsk.info@hilton.com"},
      "arrivalTime": {"checkIn": "14:00", "checkOut": "12:00"}
    }'

Response (`201 Created`, header `Location: .../property-view/hotels/1`):

    {
      "id": 1,
      "name": "DoubleTree by Hilton Minsk",
      "description": "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms ...",
      "address": "9 Pobediteley Avenue, Minsk, 220004, Belarus",
      "phone": "+375 17 309-80-00"
    }

Add amenities (returns the full updated hotel):

    curl -X POST http://localhost:8092/property-view/hotels/1/amenities \
      -H 'Content-Type: application/json' \
      -d '["Free parking", "Free WiFi", "Concierge"]'

Read, search and count:

    curl http://localhost:8092/property-view/hotels
    curl http://localhost:8092/property-view/hotels/1
    curl "http://localhost:8092/property-view/search?city=minsk"
    curl -G http://localhost:8092/property-view/search \
      --data-urlencode "amenities=Free WiFi" --data-urlencode "amenities=Concierge"
    curl http://localhost:8092/property-view/histogram/city

Histogram response:

    { "Minsk": 1, "Moscow": 2 }

## How the API behaves
- `POST /hotels` returns `201 Created` with a `Location` header and the brief hotel info.
- `POST /hotels/{id}/amenities` returns `200 OK` with the full details of the updated hotel.
- Duplicate amenities are ignored without an error. "Free WiFi" and "free wifi" are the same amenity.
  The first spelling is kept, and amenities keep the order in which they were added.
- All fields are required except `description` and `arrivalTime.checkOut`.
  A missing or invalid field returns `400 Bad Request` with the list of problems.
- Search: `name` matches a part of the name, `brand`, `city` and `country` must match completely.
  Letter case is ignored everywhere. `amenities` can be repeated (`?amenities=A&amenities=B`)
  or comma-separated, and the hotel must have all of them.
  All filters are combined with AND. Without filters the search returns all hotels.
- Histogram: an unknown parameter returns `400 Bad Request`. Keys are sorted alphabetically.
- An unknown hotel id returns `404 Not Found`.
- Errors are returned as `application/problem+json`.
- Time is written as `HH:mm`.
- The database is empty at start, so the first created hotel gets `id = 1`.

## Architecture
Clean Architecture in a single Maven module, so `mvn spring-boot:run` works from the project root.
Layers are separated by packages, and ArchUnit tests check that dependencies point inward.
See [ADR-001](docs/adr/0001-architecture-and-stack.md).

| Package | What is inside |
|---|---|
| `domain` | Hotel, value objects, exceptions, repository port. No frameworks. |
| `application` | Use cases and histogram strategies. No frameworks. |
| `infrastructure` | JPA adapter, Specifications, mappers, Liquibase, configuration. |
| `presentation` | Controllers, request/response DTOs, error handler. |

Patterns used: Repository (port and adapter), Use case, Strategy (histogram), Specification (search), DTO and Mapper.

## Database
The schema is created only by Liquibase (YAML files in `src/main/resources/db/changelog`).
Hibernate only validates it.

| Profile | Purpose |
|---|---|
| (default) | in-memory H2, no seed data |
| `demo` | adds demo hotels (Liquibase context `demo`) |
| `local` | H2 console and SQL logging |
| `postgres` | PostgreSQL |
| `mysql` | MySQL |

Switching to another database (needs Docker):

    docker compose --profile postgres up -d --wait
    mvn spring-boot:run -Ppostgres -Dspring-boot.run.profiles=postgres

    docker compose --profile mysql up -d --wait
    mvn spring-boot:run -Pmysql -Dspring-boot.run.profiles=mysql

`-P` adds the JDBC driver, `-Dspring-boot.run.profiles` selects the connection settings.
The compose databases use ports 5433 (PostgreSQL) and 3307 (MySQL) to avoid clashes with local installations.
Another storage (for example MongoDB) would need only a new implementation of the `HotelRepository` port.

Note: MySQL compares text case-insensitively by default, so histogram values that differ only by case are merged there.

## Tests

    mvn verify

- unit tests for the domain and services
- `@DataJpaTest` for migrations, repository and query counts
- `@WebMvcTest` for controllers and validation
- an end-to-end test with `@SpringBootTest` over real HTTP
- ArchUnit tests for the layer rules

GitHub Actions runs `mvn verify` on JDK 21 for every pull request.

## Known limitations
**Concurrent Amenity Creation:** If two concurrent requests attempt to create the exact same new amenity simultaneously, a database uniqueness violation (`uq_amenity_normalized_name`) may occur due to race conditions. This is acceptable for the scope of this technical task and can be resolved in a production environment by adding transaction retries or a find-or-create upsert mechanism.
