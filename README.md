# property-view-api

RESTful API for managing hotels. Java 21, Spring Boot, Spring Data JPA, Liquibase, H2.

## Requirements
- JDK 21
- Maven 3.9.16

## Run
    mvn spring-boot:run

The app starts on http://localhost:8092. All endpoints share the prefix `/property-view`.

## API
| Method | Path | Description |
|---|---|---|
| GET | /property-view/hotels | List of hotels (brief) |
| GET | /property-view/hotels/{id} | Hotel details |
| GET | /property-view/search | Search by name, brand, city, country, amenities |
| POST | /property-view/hotels | Create a hotel |
| POST | /property-view/hotels/{id}/amenities | Add amenities to a hotel |
| GET | /property-view/histogram/{param} | Count of hotels grouped by brand, city, country or amenities |

_Details, request/response examples and Swagger link: TBD._

## Architecture
Clean Architecture in a single Maven module (layers: domain, application, infrastructure, presentation).
See [ADR-001](docs/adr/0001-architecture-and-stack.md).

## Profiles
| Profile | Purpose |
|---|---|
| (default) | in-memory H2, no seed data |
| `demo` | adds demo hotels (Liquibase context `demo`) |
| `local` | H2 console and SQL logging |

## Assumptions and open questions
_TBD (see ADR-001)._

## Tests
    mvn verify

## Known limitations
**Concurrent Amenity Creation:** If two concurrent requests attempt to create the exact same new amenity simultaneously, a database uniqueness violation (`uq_amenity_normalized_name`) may occur due to race conditions. This is acceptable for the scope of this technical task and can be resolved in a production environment by adding transaction retries or a find-or-create upsert mechanism.
