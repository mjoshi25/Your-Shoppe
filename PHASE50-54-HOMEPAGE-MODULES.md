# ASMJ Shoppe – Phases 50–54 Homepage Modules

## Phase 50 – Events & ticket booking
- Public catalogue of admin-approved events with keyword and city filters.
- Organisers can submit event listings with category, venue, city, event date, capacity and indicative ticket price.
- Authenticated customers can submit a registration request and ticket quantity.
- Optional capacity is tracked; requests exceeding the remaining available capacity are rejected.
- Admin can review listings and confirm/cancel requests. Confirmed event requests receive an ASMJ ticket reference.
- Payment collection, payment proof verification, QR-code ticket scanning and automated email/SMS delivery are not included in this release. The reference is shown in the customer's request history.

## Phase 51 – Courses & training
- Public approved course catalogue with keyword and city filters.
- Course providers can submit course level, delivery mode, duration, schedule, dates and fee.
- Authenticated users can send enrolment enquiries with learner quantity and requirements.
- Admin listing moderation and request status management.
- LMS lesson hosting, online classroom integration, attendance and certificate generation are future enhancements.

## Phase 52 – Wholesale & B2B enquiries
- Approved supplier listings with product details, indicative price and minimum order quantity.
- Buyers can send a quotation request with required quantity and message.
- Admin can update enquiry status to quoted, confirmed, in progress, completed or cancelled.
- Negotiated contracts, purchase orders, invoicing and payment settlement are not included.

## Phase 53 – Local delivery & logistics
- Approved delivery provider listings with service area/PIN codes, delivery mode and indicative starting charge.
- Customers can request delivery with pickup and drop addresses.
- Admin can track requests through submitted, confirmed, in progress, completed and cancelled statuses.
- Live GPS tracking, route optimisation, driver mobile app, distance-based automated fare calculation and proof of delivery are future enhancements.

## Phase 54 – Freelance & professional portfolio
- Approved freelancer/professional listings with category, skills, engagement type, work mode and portfolio URL.
- Customers can send project enquiries and requirements.
- Admin can moderate profiles and manage request statuses.
- Escrow, milestone payments, timesheets, ratings and dispute resolution are future enhancements.

## Shared implementation
- MongoDB collections: `marketplace_extension_listings` and `marketplace_extension_requests`.
- Public pages show APPROVED listings only.
- New submissions default to PENDING and are not public until admin approval.
- Listing submission and request creation require authentication.
- Admin moderation is protected by the existing ADMIN role rule.
- Image and portfolio fields accept HTTPS URLs in this release; direct Cloudinary upload is not added here.
- Homepage discovery cards and navigation links now open the five modules; prior business-directory and classifieds links remain available.

## Validation
- ZIP integrity and source-level structural checks performed.
- Maven compilation, Spring startup, MongoDB integration, browser testing and Render deployment were not performed because Maven is not installed in the build environment.

## Pre-deployment test checklist
1. Sign in as a standard user and submit one listing in each module; verify each is PENDING.
2. As admin, approve listings; verify public catalogues display only approved entries.
3. As a guest, verify listing catalogues are accessible but create/request actions redirect to login.
4. Submit an event request for 1 ticket; confirm it appears in My requests. Confirm as admin and verify the ticket reference appears.
5. Set an event capacity and request more tickets than remain; verify the request is rejected. Request within capacity and verify remaining capacity decreases.
6. Submit a course enrolment enquiry, wholesale quote, delivery request and freelancer project enquiry; update statuses from the admin page.
7. Verify users cannot access admin moderation routes.
8. Test homepage cards and navigation at desktop, tablet and mobile widths.
9. Run `mvn clean package -DskipTests`, then test against a staging MongoDB database before Render deployment.
