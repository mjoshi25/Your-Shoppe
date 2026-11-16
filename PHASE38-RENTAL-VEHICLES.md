# Phase 38 — ASMJ Shoppe Rental Vehicles (with driver)

## Scope
- Adds a standalone with-driver rental module. No self-drive rentals are offered.
- Public rental catalogue lists only admin-approved vehicles with valid driver licence and insurance dates.
- Approved marketplace vendors can register rental vehicles with an assigned driver and indicative base/per-km rates.
- Admin can approve, reject or suspend rental vehicle listings.
- Customers can request one-way or round-trip rentals, select pickup/drop locations using Google Maps Places autocomplete or map pins, and submit coordinates, addresses and PIN codes.
- Google Routes API calculates road distance server-side. The server recalculates distance and indicative fare at booking submission; browser-supplied distance/fare is never trusted.
- Vehicle owners can accept or reject requests. Customers and owners have separate booking lists.
- Rental vehicles and bookings use separate MongoDB collections and do not alter marketplace product orders, customer payments, advertising or seller commissions.

## Google Maps configuration
This module reuses the existing configuration:
- `GOOGLE_MAPS_BROWSER_API_KEY`: browser Maps JavaScript API + Places API (New), HTTP-referrer restricted.
- `GOOGLE_MAPS_API_KEY`: server-side Routes API key, never exposed to browser code.
- Enable Maps JavaScript API, Places API (New), and Routes API in the same Google Cloud project. Billing must be enabled.

## Current booking behaviour
- A customer submits a request; the owner accepts or rejects it.
- The displayed fare is indicative: base fare + per-km rate, doubled for a round trip. The owner confirms final availability and fare.
- This phase does not collect customer payment or post rental transactions to the marketplace order/commission ledgers.
- Vehicle owners initially use an existing approved ASMJ Shoppe vendor account. A separate owner role/onboarding process is not introduced in this compatibility-first phase.

## Security and production checklist
- Rental vehicles and bookings are stored in `rental_vehicles` and `rental_bookings` collections.
- Customer can see only their own rental bookings; vehicle owner can see only their own vehicles and requests; admin has moderation access.
- Vehicle registration, driver licence and insurance expiry dates are captured. Secure document upload and verification must be completed before production launch; this phase intentionally does not upload sensitive identity documents to public image storage.
- Before live launch, add document verification, driver/vehicle assignment lifecycle, availability calendar and overlap prevention, final owner quote acceptance, cancellation/refund rules, payment recording, platform fee/settlement reporting, and audit events.

## Test status
Static source/template checks only. Maven build and live Google Maps/Render/MongoDB integration tests must be completed before deployment.
