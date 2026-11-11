# ASMJ Shoppe – Phases 16–19

## Phase 16 – Security and audit preparation
- Session fixation protection and browser security response headers.
- Restrict order scheduling endpoints to enabled customers and approved vendors; controller verifies ownership.
- Added MongoDB audit event records and admin `/admin/audit` viewer for order placement, UPI evidence, payment review, schedule changes and commission settlement actions.

## Phase 17 – Pan-India delivery PIN codes
- Vendor can configure up to 1000 six-digit serviceable PIN codes.
- Checkout validates the entered delivery PIN against the selected vendor's configured coverage on the server and exposes an immediate serviceability check before order submission.
- Pickup remains independent of delivery coverage.

## Phase 18 – Pickup/delivery scheduling
- Vendor configures separate weekday slots for delivery and pickup, each with a capacity.
- Slot format: `DAY|HH:mm|HH:mm|capacity`.
- Customer chooses a date (tomorrow to 60 days) and a matching weekday slot.
- Order stores requested date and slot; a customer/vendor can propose a more precise time and the other party accepts or declines.
- Agreed custom time is retained on the order; proposals and responses remain in the order record.

## Phase 19 – Direct-to-vendor UPI and commission
- Checkout no longer asks for UPI UTR or screenshot. UPI orders are created with payment pending.
- Customer sees the vendor UPI ID snapshot and can open a UPI app link, then submit UTR and screenshot from My Orders.
- Duplicate UPI reference submissions across orders are rejected. Admin verifies or rejects submitted evidence and can leave a review note.
- Customer order payment remains a customer-to-vendor payment. Vendor commission remains a separate vendor-to-admin weekly settlement.

## Operational limitations / required verification
- Slot capacity check uses a count-then-save pattern and is not atomic under simultaneous checkout requests. For high traffic, implement an atomic slot reservation collection/transaction before broad scale.
- Customer/vendor order time negotiation is an order-scoped conversation panel, not a real-time WebSocket chat. Existing product chat remains unchanged.
- UPI verification is manual evidence review; it does not confirm bank settlement automatically.
- Unpaid UPI order expiry and automatic stock release are not automated in this release.
- Existing vendor profiles must configure serviceable PIN codes and slots before accepting new delivery/pickup orders.
- No Maven executable is available in the build environment; Maven compile/tests are not claimed as passed.
- Commission eligible amount currently follows the existing implementation: merchandise subtotal before coupon discount, excluding delivery charge. Confirm whether future policy should instead deduct coupon discounts before commission is calculated.
- UPI deep link is a convenience only; the app does not initiate or reconcile bank transfers.
