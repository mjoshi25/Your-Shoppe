# ASMJ Shoppe — Phases 33–37 Paid Advertising

## Business decisions
- Vendors purchase optional marketplace advertising using direct UPI payment to ASMJ Shoppe.
- Pricing is fixed per placement and duration; the purchased amount and duration are snapshotted on the campaign.
- Admin verifies UTR/payment evidence and approves creative separately.
- Approved campaigns are scheduled by India-local dates (`Asia/Kolkata`).
- Advertising payments and reporting are separate from customer order payments, refunds and weekly vendor commissions.

## Phase 33 — Foundation
- Added MongoDB documents/repositories for placements, packages, campaigns, advertising settings and events.
- Default placement inventory and sample fixed-price packages are initialized on first advertising admin/vendor visit if the collections are empty.
- Campaign lifecycle uses separate payment and creative approval states.
- Admin-configurable UPI ID, payee name, QR image and enable/disable switch.

## Phase 34 — Vendor campaign management
- Vendor can create campaigns, select a placement and fixed package, choose dates, promote an owned active product or active category, enter an internal destination, upload desktop/mobile banners and optionally target PIN codes.
- Banner assets and payment evidence use existing Cloudinary integration.
- UPI proof includes UTR/reference and screenshot/document.
- Vendor can revise and resubmit a rejected creative without paying again when payment was already verified.

## Phase 35 — Admin controls
- Admin can configure placements, capacity and package prices/durations.
- Admin separately verifies/rejects payment and approves/rejects creative.
- Admin can suspend a campaign, record a full manual UPI refund with refund UTR/reason/optional proof, and review revenue, impressions and clicks. A refunded campaign is removed from ad serving and excluded from verified revenue totals.
- Capacity is checked for overlapping approved campaigns before approval.

## Phase 36 — Display
- Sponsored banners/cards are inserted into homepage hero/promotional slots, category/search pages, product details and cart.
- Sponsored label is visible; no ad is inserted into checkout/payment forms.
- Only campaigns with verified payment, approved creative, enabled placement and active date range are served.
- Optional PIN targeting is applied only after a shopper sets a six-digit promotion PIN. Category banners respect selected category.
- Destination links are restricted to internal marketplace paths to prevent open redirects.

## Phase 37 — Measurement and scheduling
- Impression/click events are deduplicated per campaign, event type, browser session and India-local date; session IDs are stored as SHA-256 hashes.
- Campaign totals and CTR are shown to vendors; totals/revenue shown to admins.
- Eligible campaigns rotate daily within each placement and are limited by placement concurrent capacity.
- Daily scheduled job marks ended campaigns expired.

## Configuration / operational notes
- No new payment gateway or environment secret is required.
- Existing Cloudinary credentials must be configured for banner and payment-proof uploads.
- Admin must configure the advertising UPI ID and payee details before vendors can submit campaigns.
- The sample package prices are starting values and can be changed by admin. They are not contractual price recommendations.
- Campaign payment verification is manual; no bank/UPI API integration is claimed.

## Validation limitations
- Maven is not installed in the build workspace, so `mvn clean test` and Spring Boot runtime tests could not be run here.
- Browser visual testing and live MongoDB/Cloudinary/UPI flows remain to be run locally or in staging.
- No production repository update or Render deployment was performed.
