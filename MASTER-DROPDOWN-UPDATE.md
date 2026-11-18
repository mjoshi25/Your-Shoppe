# ASMJ Shoppe – Master Dropdown Foundation (Initial Rollout)

## Included
- MongoDB-backed `marketplace_master_data` collection for reusable dropdown options.
- Admin page at `/admin/master-data` to add, edit, activate and deactivate options.
- Read-only JSON endpoint `/api/master-data/{group}` with optional `parent` filter for dependent dropdowns.
- Idempotent startup seeding for approval modules, clarification reasons, requested document types, employer organisation types, job employment types, rental fuel types and transmission types.
- Approval submission module dropdown now loads from the master-data collection.
- Admin clarification request form now offers reason and multi-select required document types; selected values are stored in approval history.
- Employer organisation type and job employment type dropdowns now load from master data.
- Admin navigation link to Master Data Management.

## Security and data handling
- Only `/admin/master-data/**` is writable and is protected by the existing ADMIN security matcher.
- `/api/master-data/**` is read-only and permitted for public form/filter use.
- Deactivation preserves old saved values and history; no master-data delete operation is provided.
- Option codes are normalized and unique within a group.

## Groups seeded
- `APPROVAL_MODULE`
- `APPROVAL_REQUEST_REASON`
- `APPROVAL_DOCUMENT_TYPE`
- `EMPLOYER_ORGANISATION_TYPE`
- `JOB_EMPLOYMENT_TYPE`
- `RENTAL_FUEL_TYPE`
- `RENTAL_TRANSMISSION`

## Integration notes
This is the first implementation increment, not a claim that every form in the 89-template application has been converted. Existing product, service, rental and marketplace module category systems remain authoritative where already database-backed. The remaining forms/search filters need module-by-module integration and backend enum/validation review before they can safely be converted.

## Validation
- Source archive and file inventory checked.
- Maven compilation was not run because Maven is not installed in the execution environment.
- MongoDB startup seeding, Thymeleaf rendering and browser flows require local testing before Render deployment.
