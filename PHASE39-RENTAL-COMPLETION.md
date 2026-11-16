# Phase 39 — Rental booking, trip and settlement workflows

Built on Phase 38. This release keeps rentals in separate MongoDB collections and does not change marketplace product orders, advertising, customer order payments or the existing seller commission ledger.

## Included
- Customer accepts the vehicle owner's final quote before a booking becomes CONFIRMED.
- Customer can cancel REQUESTED, QUOTED and CONFIRMED bookings before trip start. Trips in progress/completed require support intervention.
- Date-range overlap checks are applied before owner quote acceptance and again when the customer accepts the quote. CONFIRMED and IN_PROGRESS bookings block the vehicle for overlapping dates.
- Customer can submit UPI UTR and screenshot after confirming the quote, or choose cash-on-trip. UPI proof is verified by the vehicle owner.
- Owner records trip start and end odometer readings. Final odometer must exceed start odometer.
- UPI payment must be verified before trip start. Cash is recorded by the owner at trip completion and must equal the accepted quote.
- Admin can configure a separate rental commission rate (default 5%, configurable 0–30%). Commission is calculated on completed, paid rentals and stored on the rental booking, not the marketplace order ledger.
- Vendor submits rental commission UTR and proof; admin verifies it separately.
- Rental vehicle, booking, payment, trip, quote and commission actions write to the existing marketplace audit-event collection.
- Customer and owner views show map links for pickup/drop coordinates.
- Duplicate vehicle registration numbers and duplicate rental UPI references are rejected.

## Deliberate production safeguards / remaining limitations
- Driver licence and insurance numbers/expiry are captured and checked for current validity. This release does not upload identity documents to public Cloudinary URLs. A private, access-controlled document store and admin document-review workflow must be configured before accepting licence/insurance scans.
- Date-level overlap protection is conservative: it blocks another confirmed booking on any overlapping calendar date, even if times differ. It avoids double booking but can be refined to time-window availability after operational testing.
- Payments are direct between customer and vehicle owner. ASMJ Shoppe does not collect rental fare. Direct refunds must be arranged by the owner with the customer; this release does not initiate refunds or a central payment reversal.
- Rental commission settlement is separate and per completed booking; weekly grouped rental settlement statements and overdue-owner booking restrictions remain a follow-on enhancement.
- Driver is assigned to the vehicle at registration. A separate multi-driver fleet roster/dispatch assignment workflow is not included.

## Validation
Static template/JS/CSS/ZIP checks only. Maven compilation, automated Spring tests, MongoDB integration, Cloudinary uploads and live Google Maps/Routes tests must be run by the project owner before production release.
