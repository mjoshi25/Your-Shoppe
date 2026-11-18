# ASMJ Shoppe — Emergency Services ES-01 to ES-06

This release adds the initial Emergency Services foundation and customer-facing directory/request flow.

## Included
- MongoDB models and repositories for emergency provider profiles and customer assistance requests.
- Admin-managed master-data groups `EMERGENCY_CATEGORY` and `EMERGENCY_SUBCATEGORY`, with initial categories/subcategories seeded once.
- India State seed values. City and PIN-code options continue to be maintained by Admin in Master Data Management using the existing parent-code hierarchy (`INDIA_CITY` parent = State code; `INDIA_PINCODE` parent = City code).
- Vendor emergency-provider profile form with service location, coverage PIN codes, availability, pricing disclosure and Cloudinary supporting-document uploads.
- Automatic creation of an `EMERGENCY_PROVIDER` approval case when the provider explicitly submits the profile. Existing Approval Centre controls review, clarification and final decision. Only `APPROVED` provider profiles appear publicly.
- Public emergency provider directory with category/PIN filtering and direct `tel:` call links.
- Customer assistance-request form with server-side State → City → PIN validation. Matching approved and available providers covering the selected PIN are notified without exposing the customer's exact address in the notification.
- Customer request history page.
- Navigation links and role-based security rules.

## Admin configuration
1. Open Admin Dashboard → Master Data Management.
2. Add/edit `INDIA_CITY` options with `parentCode` equal to the State code.
3. Add/edit `INDIA_PINCODE` options with `parentCode` equal to the City code; the code should be the six-digit PIN.
4. Manage emergency categories/subcategories under `EMERGENCY_CATEGORY` and `EMERGENCY_SUBCATEGORY`.

## User routes
- Public directory: `/emergency`
- Vendor profile: `/emergency/providers/register` (approved Vendor account)
- Customer request form: `/emergency/requests/new`
- Customer request history: `/emergency/requests/mine`

## Safety and rollout notes
- The public directory is not an emergency dispatch service. The UI tells users in India to call 112 for immediate danger/life-threatening emergencies.
- Provider response is not guaranteed. No ETA, dispatch, live tracking, payment collection or provider accept/reject workflow is included in ES-01 to ES-06; those belong to later phases.
- Only customer-owned requests are displayed in the customer history route. Provider request management and Admin monitoring/complaints are later phases.
- Emergency provider profiles use existing Vendor accounts; no new login role is introduced.
- Do not deploy until local build and functional tests pass and the owner approves deployment.

## Verification
ZIP integrity was checked. Maven build was not run in this environment because Maven is not installed. Please run `mvn clean test` locally before deployment.
