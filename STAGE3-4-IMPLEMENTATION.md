# ASMJ Shoppe — Stage 3 & 4 implementation notes

## Stage 3: module-linked approval decisions
- The Approval Centre now validates that the selected submitter owns the referenced source record before creating a case. The source title is loaded from the record rather than trusting a manually entered title.
- Supported linked record modules: vendor account, product, rental vehicle, service provider, service listing, employer profile, job posting, marketplace extension listings (events/courses/wholesale/delivery/freelancer), and community listings (business/classified).
- A final decision is allowed only after the submitter has responded (`SUBMITTED`). Approval/rejection updates the linked record using its module-native status convention. Vendor decisions use `VendorManagementService` so the existing vendor status transition and review-event history are preserved.
- Clarification requests/responses do not approve or reject the source item. They keep the source pending while the support case moves between `ACTION_REQUIRED` and `SUBMITTED`.
- Unsupported module types and mismatched owner emails are rejected.

## Stage 4: parameterization foundation
- Added MongoDB-backed `marketplace_parameters` with key, label, group, value, type, description, enabled flag, update timestamp and actor.
- Added Admin → Marketplace parameters UI to create/edit/enable/disable parameters with validation for BOOLEAN and NUMBER values.
- Added `MarketplaceParameterService` with fallback values for safe reads.
- Migrated product approval requirement to the database key `products.require-approval`; the default is `true`, preserving the existing property default. A default record is seeded only when absent.
- Existing dedicated admin settings (rental commission, service commission, categories/subcategories, advertising placements/packages, etc.) are intentionally retained rather than duplicated. Further parameters must be migrated module-by-module so each saved setting actually drives its server-side business logic.

## Important security and operational notes
- Approval documents still use the existing Cloudinary `uploadDocument` behaviour. This does not provide private/authenticated delivery; sensitive identity, licence, registration and financial documents must not be uploaded until private delivery and authenticated retrieval are implemented.
- The common case approval endpoint updates source records only for supported modules. Module-specific constraints still apply; e.g. rental eligibility still depends on valid driver licence and insurance dates, even after approval.
- Admin routes remain protected by Spring Security. Submitters can access only cases whose ownerUserId matches their account.
- ZIP/source validation does not replace runtime testing. Run Maven tests, verify MongoDB collection names and exercise each module workflow locally before Render deployment.
