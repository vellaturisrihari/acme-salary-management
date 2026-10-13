# ACME Salary Management — Requirements

## Goal
Give ACME's HR manager a reliable web application to manage salary records for 10,000 employees across countries and answer basic workforce-pay questions without maintaining spreadsheets.

## Scope and features
- A Spring Boot application with an embedded H2 database, a browser-based AngularJS interface, and a deterministic seed of 10,000 employee records.
- View, search, filter, sort, and page through employee records; add, edit, and delete an employee's salary record.
- Capture employee identity, country, department, job title, annual salary, currency, and hire date. Validate required values, email, positive salary, and supported currency.
- Show headcount, payroll totals grouped by currency, and headcount and average salary grouped by country and currency. Never add or compare amounts across currencies.
- Responsive, accessible basic administration UI; REST endpoints and automated tests for core validation, CRUD, filtering, and salary summaries.

## Deliberately out of scope
- **Authentication, authorization, and audit history:** the first release is a local demonstration, not safe for deployment with real employee data. Production use requires identity integration, HR-role access control, and auditable changes.
- **Payroll execution, tax, benefits, bonuses, and payslips:** these involve country-specific rules and integrations beyond salary-record management.
- **Currency conversion and cross-currency rankings:** exchange rates and conversion dates would make totals misleading; reports stay explicitly grouped by currency.
- **Bulk spreadsheet import/export and employee self-service:** neither is needed to replace the initial HR-only spreadsheet workflow and both warrant separate validation and access-control requirements.
- **Production-scale deployment, backups, and monitoring:** embedded H2 and deterministic demo data keep setup simple; production requires a managed database and operational controls.

## Success criteria
The seeded application starts locally, exposes all 10,000 records through paginated search, supports validated salary-record CRUD, produces currency-safe summaries, and passes deterministic automated tests.
