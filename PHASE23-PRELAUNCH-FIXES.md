# ASMJ Shoppe — Phase 23 Pre-launch Corrections

This release builds on the combined Phase 21–22 release.

## 1. Searchable address selection
- Added Google Places Autocomplete search to customer delivery address and vendor business/shop address fields.
- Search accepts Indian place suggestions and fills the selected formatted address plus city, state and PIN code where those components are available.
- Manual address entry remains available if the browser key is not configured or Places is unavailable.
- Routes API remains a separate server-side integration used for road-distance pricing.

### Additional Google Maps configuration
- Keep `GOOGLE_MAPS_API_KEY` as the server-side Routes API key, restricted to Routes API.
- Enable Maps JavaScript API and Places API (New) in the same Google Cloud project.
- Create a second browser key, restrict it by HTTP referrers to the production domain and localhost development origin, and restrict APIs to Maps JavaScript API and Places API (New).
- Set Render environment variable `GOOGLE_MAPS_BROWSER_API_KEY`.
- Never commit either real key. The browser key is visible in page source by design and must have HTTP-referrer and API restrictions.

## 2. Payment only after vendor acceptance
- Checkout places the order without collecting or requesting payment.
- UPI payment instructions and proof submission appear only after the vendor changes the order to ACCEPTED.
- The UPI proof endpoint independently rejects submissions unless the order is ACCEPTED.
- Vendor UPI verification is allowed only for an accepted order.
- UPI orders cannot move to PROCESSING until verified payments recorded against the order cover the full order total.
- COD remains payable at delivery/pickup; the vendor records COD receipts after completion.

## 3. Payment/refund transaction ledger
- Added embedded payment transaction rows on each order: PAYMENT or REFUND, method, amount, reference, note/reason, recorded-by, timestamp and optional proof attachment.
- UPI payment amount is entered by the customer and added to the ledger only when the vendor verifies the evidence.
- Supports multiple partial UPI payments; processing remains blocked until the total order amount has been verified.
- Vendor records COD amount received with receipt evidence. Multiple receipts can be entered for partial collections.
- Vendor records partial or full refunds after an order is completed or cancelled, categorized as merchandise, delivery or other, with reference, reason and optional proof. Only merchandise refunds reduce net merchandise sales used for Seller Excellence ranking.
- Customer, vendor and admin order views display the payment/refund history and totals.
- Admin reports show payment/refund transaction totals based on transaction recorded date and provide a separate transaction-level CSV export.
- Order CSV export also includes total payments, refunds and net received.
- Seller Excellence ranking now subtracts vendor-recorded merchandise refunds from net merchandise sales and excludes fully refunded orders from qualifying completed sales.

## Accounting notes and limitations
- Payment/refund entries are manually recorded by the vendor because customers pay vendors directly. They are not automatically confirmed by a bank, UPI provider or payment gateway.
- Historical orders marked VERIFIED before this ledger existed do not have a transaction-by-transaction audit trail. The order's existing verified status is used as a compatibility fallback for its paid amount; no historical payment transaction rows are fabricated.
- Refunds are categorized as merchandise, delivery or other. Refunds are visible in reports, but commission ledger entries are not automatically recalculated when a refund is recorded. Admin should review/reconcile commission for refunded completed orders before settlement.

## Validation
- Java source parse check: passed (no syntax parse errors).
- Changed HTML templates parsed: passed.
- Inline JavaScript syntax check: passed.
- ZIP integrity check: passed after packaging.
- Maven compile, JUnit, Spring startup, MongoDB integration, browser UI and live Google API calls: not run in this environment (`mvn` is not installed and the project has no Maven wrapper).
