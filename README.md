# ACME Salary Management

Small, locally runnable HR salary-record application for a 10,000-person global workforce. See [REQUIREMENTS.md](REQUIREMENTS.md) for goals, scope, and exclusions.

## Run

Requires Java 8 or later and Maven 3.6 or later.

```sh
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080). The first startup creates an in-memory H2 database and inserts 10,000 deterministic sample employees. The database resets when the application stops. Sample email addresses use the reserved `.example` domain.

AngularJS 1.8 and Bootstrap 5 are loaded from public CDNs; an internet connection is required to load the interface assets.

## API

- `GET /api/employees?search=&country=&department=&page=0&size=25&sort=lastName&direction=asc` — filtered, sorted, paginated directory (maximum page size 100)
- `GET /api/employees/{id}`, `POST /api/employees`, `PUT /api/employees/{id}`, `DELETE /api/employees/{id}` — salary-record CRUD
- `GET /api/dashboard` — headcount and payroll/average salary by currency and by country+currency
- `GET /api/metadata` — available country, department, and currency values

Salary inputs are positive annual amounts. Currency is an ISO-style three-letter code selected from the supported currencies. Summaries do not convert or combine currencies.

## Test

```sh
mvn test
```

The integration tests exercise deterministic seed size, filtered paging, currency-safe dashboard aggregates, CRUD, validation, duplicates, and sort/page bounds.
