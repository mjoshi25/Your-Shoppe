# ASMJ Shoppe — Phases 21 & 22

## Phase 21: vendor-configurable road-distance delivery pricing

- Added vendor settings for enabling distance pricing, free-distance allowance (km), and per-additional-kilometre rate.
- Existing fixed delivery charge remains available when distance pricing is disabled; pickup remains free.
- Added server-side Google Routes API integration using the Routes API `computeRoutes` endpoint, DRIVE mode, and the `routes.distanceMeters` field mask.
- Customer checkout can request a road-distance quote after entering full address, city, state and PIN code. Existing vendor serviceable-PIN validation remains in force.
- Each started kilometre after the free allowance is chargeable (ceiling rounding). For example, 5 km free and 12 km road distance means 7 chargeable km.
- Quote is stored server-side in the session, bound to vendor and normalized address fingerprint, and expires after 15 minutes. Checkout refuses missing, expired or address-mismatched quotes.
- Order snapshots include origin address, road distance, free distance, rate, chargeable kilometres and final delivery charge. Invoice and tracking show the calculated distance.
- API key is server-side only and read from `GOOGLE_MAPS_API_KEY`; it is never rendered into browser HTML/JavaScript.
- No Geocoding or Places API is needed for this address-to-address Routes API flow.

## Phase 22: six-month Seller Excellence Rewards

- Admin can configure a six-calendar-month period, minimum verified completed orders, five reward titles and reward amounts.
- Provisional ranking considers approved vendors only. Qualifying orders must be COMPLETED, payment VERIFIED, and completed during the configured period.
- Net merchandise sales are calculated as subtotal minus coupon discount; delivery charges are excluded.
- Eligibility requires the configured minimum completed-order count and no overdue marketplace commission.
- Tie-breakers: average approved product rating within the same six-month period, lower cancellation rate for orders placed during the period, then higher completed-order count.
- Admin reviews provisional rankings and publishes winners explicitly. Published awards are retained as historical records and displayed in vendor reward history.
- Added vendor workspace and admin navigation links.

## Deployment configuration

Add this Render environment variable after creating and restricting the Google Routes API key:

`GOOGLE_MAPS_API_KEY=<your-restricted-server-side-key>`

Do not commit or share the actual key. See README.md under “Google Maps Routes API setup” for Google Cloud Console and Render steps.

## Important validation notes

- Existing vendor fixed delivery charges remain active unless the vendor explicitly enables distance pricing.
- Distance pricing is not usable until `GOOGLE_MAPS_API_KEY` is configured and the vendor has a complete shop address under Profile Settings.
- If Google Routes cannot calculate a route, distance-priced delivery checkout is blocked with a clear error; the application does not silently use straight-line distance or the old fixed charge. Pickup remains available where enabled.
- Reward ranking uses the application's available completed/payment-verified order records. The current data model has no separate refund flag/workflow, so externally refunded orders cannot be automatically identified or excluded. Cancelled orders are excluded from completed sales.
- Reward cash amounts are editable by admin. Initial suggested values are ₹5,000 / ₹3,000 / ₹2,000 / ₹1,000 / ₹500; confirm the actual benefit budget before publishing winners.
- Maven was unavailable in the build environment (`mvn: command not found`), and the project has no Maven wrapper. Therefore Maven compilation, unit tests, Spring context startup, MongoDB tests and live Google API calls have NOT been run. Static checks only are reported for this archive.
