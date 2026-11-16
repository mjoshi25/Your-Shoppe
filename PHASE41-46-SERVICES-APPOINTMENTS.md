# ASMJ Shoppe Phases 41–46 — Services & Appointments

## Baseline
Built from `ASMJ_Shoppe_Phase_40_1_Contact_Link_Visibility_Fix.zip`. Existing product, advertising, rental and contact-query modules were retained.

## Confirmed business rules implemented
- Individual and business service-provider applications.
- Admin approval required before a provider can publish listings.
- Admin approval required before a service listing becomes publicly visible/bookable.
- Provider must accept each appointment request before it becomes accepted.
- Customers pay providers directly; the marketplace does not collect customer service payments.
- Service commission defaults to 5%, independently configurable by admin (0–50%).
- Commission is recorded per completed/confirmed booking and assigned to the Monday–Sunday week containing completion.
- Customer/provider arrange refunds directly; customers can raise complaints for admin intervention.

## Included
### Phase 41 — Foundation and discovery
- Service navigation and homepage section.
- Public service catalogue, search by title/category/PIN and detail page.
- Seeded root categories and subcategories when the service category collection is empty.

### Phase 42 — Provider onboarding and moderation
- Individual/business provider application, contact details, location/PIN coverage, experience, qualifications, professional registration number and UPI ID.
- Admin approve/reject/suspend provider workflow.
- Provider workspace and status visibility.

### Phase 43 — Listings, pricing and schedule
- Provider-created listings with category, description, pricing model, price, duration, service mode and service PIN codes.
- Service PINs must be contained in provider profile PIN coverage (except online services).
- Provider-configured weekly working days, start/end times and slot interval.
- Mandatory admin listing moderation.

### Phase 44 — Booking engine
- Customer booking request with preferred date/time, PIN, address and notes.
- Server-side schedule validation and overlapping booking checks.
- Provider accept/reject; optional agreed quotation amount.
- Customer cancellation while pending or accepted.
- Provider start and completion submission; customer completion confirmation.

### Phase 45 — Direct payment and commission
- Customer records UPI/cash payment directly to provider.
- Optional payment proof upload to Cloudinary; UPI requires UTR or proof.
- Provider verifies submitted payment; admin does not verify it.
- Commission ledger, weekly period dates and admin mark-received action.
- Providers with a commission period past due are blocked from new bookings; existing workspace remains available.

### Phase 46 — Completion, reviews and disputes
- Customer confirms completion.
- One review per booking with 1–5 rating.
- Customer complaints and admin resolution notes.
- Payment and commission history in provider/admin views.

## Important limitations before production
- Maven build and automated integration tests were not run in this environment (Maven and project wrapper are unavailable).
- MongoDB Atlas, Cloudinary, Google Maps and Render were not live-tested.
- Provider document uploads/private KYC storage and admin document-review UI are not included. Healthcare provider registration numbers/qualifications are captured as text only; healthcare listings should not be enabled for production until credential verification and secure document handling are added and reviewed.
- No business staff roster or staff-specific capacity allocation yet; one provider profile currently represents one appointment calendar.
- Booking slot choices are entered as date/time by the customer and validated server-side against provider weekly hours; this release does not render a live slot-picker calendar or holiday/leave calendar.
- No automatic notifications, appointment rescheduling workflow, direct refund evidence ledger or telemedicine/medical-record feature.
- Google Maps is not wired into service-address selection in this release; customers enter address and PIN manually.
- Cloudinary payment proof uses the existing document upload service; do not upload patient records or sensitive medical documents.
- Existing weekly service commission is tracked per booking; no consolidated downloadable weekly service statement yet.
- Slot conflict checks are application-level checks, not a MongoDB transaction/unique reservation lock. Under simultaneous requests, production-grade atomic slot reservation should be added before high-volume use.

## Configuration
No new environment variables required. Existing MongoDB Atlas and Cloudinary configuration is reused.

## Test sequence
1. Deploy to a non-production Render service or test branch first.
2. Confirm seeded service categories appear in Admin → Services & appointments.
3. Submit provider application as an individual and approve it as admin.
4. Create service listing, submit and approve as admin.
5. Browse as customer and request an appointment within configured day/time/PIN.
6. Confirm overlapping requests are rejected; provider accepts/rejects.
7. Start service, submit completion, customer confirms.
8. Submit UPI evidence or cash record; provider verifies.
9. Check commission due and admin mark-received workflow.
10. Submit review and complaint; resolve complaint as admin.

Do not replace the live Render deployment until this test sequence is complete and explicitly approved.
