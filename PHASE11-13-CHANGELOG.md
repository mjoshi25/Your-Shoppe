# ASMJ Shoppe – Phases 11, 12 and 13

## Phase 11 – Order tracking and invoice
- Embedded order status history records placement, vendor status transitions and cancellation with actor, role, note and timestamp.
- Customer and owning vendor can view order timeline and printable invoice; admin can access these views.
- Invoice page supports browser Print / Save as PDF.
- Customer/vendor cancellation restocks quantities once and records stock movement.

## Phase 12 – Vendor dashboard and inventory
- Vendor dashboard shows product count, order count, completed payment-verified sales and low-stock count.
- Inventory screen supports vendor-scoped stock adjustment, low-stock flag at quantity <= 5 and recent stock movement history.
- Order stock reductions, manual adjustments and cancellation restocks are recorded in `stock_movements`.

## Phase 13 – Product reviews
- Only the customer who owns a completed and payment-verified order containing the product can review it.
- One review per product per order. Ratings 1–5, title, comment and optional JPG/PNG/WEBP photo.
- New reviews are PENDING until admin moderation.
- Vendor can reply only to approved reviews belonging to their products.
- Public product detail shows approved reviews and vendor replies.

## MongoDB collections added
- `stock_movements`
- `product_reviews`

Existing orders gain embedded `statusHistory` and `stockRestocked` fields. Existing orders remain readable; older orders without a history show a legacy timeline message. No manual migration is required.

## Verification
Run `mvn clean test` and `mvn spring-boot:run` before deployment.
