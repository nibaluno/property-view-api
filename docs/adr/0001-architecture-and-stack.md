# ADR-001: Architecture, stack and key decisions

- Status: Accepted
- Date: 2026-10-14

## Context
Test task: a RESTful hotels API (6 endpoints), prefix `property-view`, port 8092.
Stack required by the task: Maven, Java 21, Spring Boot, Spring Data JPA, Liquibase, H2.
The app must start with `mvn spring-boot:run` and will be checked by automated tests.

## Decisions
1. **Java 21, Spring Boot 4.0.8** (verified via Spring Initializr and successful build).
2. **Clean Architecture in ONE Maven module.** `mvn spring-boot:run` from the root must work,
   so layers are separated by packages and protected by ArchUnit tests (Dependency Rule:
   source code dependencies point inward).
3. **Layers:** `domain` (no Spring/JPA) -> `application` (use cases, ports) ->
   `infrastructure` (JPA adapters) and `presentation` (REST adapters).
4. **Schema is managed only by Liquibase** (YAML changelog); `ddl-auto=validate`.
5. **No seed data by default.** Demo data only with Liquibase context `demo`
   (the automated checker likely expects an empty DB and `id=1` for the first hotel).
6. **Common prefix** via `server.servlet.context-path=/property-view`.
7. **Search** via Spring Data Specifications; **histogram** via the Strategy pattern.
8. **DB switching** through profiles and DB-agnostic Liquibase changelogs; no native SQL.
9. **Commits:** Conventional Commits, merged via "Rebase and merge" to keep history readable.
10. **Repository port is attribute-oriented:** `HotelSearchCriteria` and `HotelOverview` live in the domain;
    histograms are served by `countByBrand/City/Country/Amenity` (Strategy providers live in application).
11. **Amenities** are a separate table with `normalized_name` (case-insensitive uniqueness, portable across DBs)
    and `sort_order` in the link table (insertion order is preserved).
12. **Search semantics:** `name` contains (case-insensitive); brand/city/country equals (case-insensitive);
    several amenities must all match.
13. Application services are plain classes registered in infrastructure/config/UseCaseConfig (no Spring annotations in application).
## Consequences
- Slightly more code (separate domain/entity models and mappers) in exchange for testability
  and replaceable infrastructure.
- Layer rules are enforced by tests, not by Maven module boundaries.

## Open questions / assumptions (to be confirmed with the customer)
| # | Question | Default assumption |
|---|---|---|
| 1 | POST /hotels status code | 201 + Location |
| 2 | Response of POST /hotels/{id}/amenities | 200 + hotel details |
| 3 | Search case sensitivity | case-insensitive |
| 4 | Search with several amenities | all must match (AND) |
| 5 | Duplicate amenities | ignored (idempotent) |
| 6 | Unknown histogram parameter | 400 |
| 7 | Typos in JSON examples | treated as typos |
| 8 | Required fields | all except description and checkOut |
| 9 | Mongo | only via port/adapter, not implemented |
| 10 | Description in the brief list | full text |
| 11 | /search: should name be "contains" or "equals"? | contains, case-insensitive |
