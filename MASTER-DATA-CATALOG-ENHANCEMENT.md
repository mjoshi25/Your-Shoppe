# ASMJ Shoppe – Master Data Catalog Enhancement

## Included
- Expanded idempotent master dropdown seeds for approval, employer/job, rental, provider/service, course, event, insurance, business, wholesale, delivery, freelance, classified, product condition, payment/refund and emergency data.
- Emergency subcategories are seeded with their parent emergency category. Existing legacy emergency subcategory rows with a missing parentCode receive a safe hierarchy repair; existing labels and active/inactive states are not overwritten.
- Added starter category trees for Events, Courses, Wholesale, Delivery, Freelance, Business Directory and Classifieds. Existing categories are not replaced and existing module categories remain editable by Admin.
- Master Data Management supports edit, activate/deactivate, text search, group filter and active/inactive filter. No delete action is introduced.
- Marketplace extension categories can be renamed and activated/deactivated. Subcategories can be renamed and activated/deactivated instead of being deleted. Existing inactive subcategory values are retained.
- Public category endpoints now return active subcategories only, and server-side validation rejects inactive subcategory choices.

## Existing category systems retained
- Products: `categories` collection, existing Admin category management and seeded product category tree.
- Services: `service_categories` collection and existing service category management/seeder.
- Rentals and extension modules: `marketplace_module_categories` collection and Admin Marketplace Categories page.
- General reusable dropdowns: `marketplace_master_data` collection and `/admin/master-data`.

## Notes / scope
- This release seeds reusable configurable option groups and starter category trees; it does not convert every workflow/status enum or every hard-coded select in all templates into dynamic master data. Workflow states, payment state machines, and security-sensitive values remain code-controlled to avoid breaking validation and business logic.
- State values are seeded; City and PIN-code values remain Admin-maintained under `INDIA_CITY` and `INDIA_PINCODE`, with parentCode = State code / City code respectively.
- Seeder is additive and idempotent: existing administrator-managed records are not overwritten.

## Validation
- ZIP integrity checked after packaging.
- Maven tests and live MongoDB/Thymeleaf/browser checks were not run in this environment. Run `mvn clean test` locally and verify the Admin pages before deploying.
