# ASMJ Marketplace – Phase 14 & 15

Built on the Phase 11–13 baseline. Existing Java 17 / Spring Boot / Thymeleaf / MongoDB / Cloudinary architecture is retained.

## Phase 14 – Admin reports
- `/admin/reports`: date-filtered order and sales overview.
- Completed sales count only orders whose payment is verified and whose order status is COMPLETED.
- Vendor-wise completed sales and top products by units sold.
- Marketplace customer, approved vendor and product counts.
- Commission collected/outstanding from the commission ledger.
- `/admin/reports/export.csv`: Excel-compatible UTF-8 CSV order export with quoted fields and spreadsheet-formula prefix protection.

## Phase 15 – Customer engagement
- Customer wishlist (`wishlist_items`).
- Recently viewed products for signed-in customers (`recently_viewed_products`).
- Reorder from an owned prior order, with current stock and one-vendor cart checks.
- Admin-managed coupons (`discount_coupons`) with percentage/fixed discounts, minimum order, optional cap and optional total usage limit.
- Coupon is recalculated on checkout; order stores coupon code and discount amount.
- Customer availability subscriptions (`stock_alert_subscriptions`); when a vendor raises stock above zero, subscriptions are marked as notified and shown in the customer's in-app availability page. This phase does not send email/SMS/push notifications.

## New routes
- Admin: `/admin/reports`, `/admin/reports/export.csv`, `/admin/coupons`
- Customer: `/customer/wishlist`, `/customer/recently-viewed`, `/customer/alerts`, `/customer/offers`
- Customer POST: `/customer/wishlist/{id}/add`, `/customer/wishlist/{id}/remove`, `/customer/products/{id}/stock-alert`, `/customer/orders/{id}/reorder`
- Coupon: `/coupon/apply`, `/coupon/remove`

## Notes / limitations
- Report download is CSV formatted for Excel compatibility, not a native `.xlsx` workbook.
- Coupon usage count is a simple document update and is not a distributed atomic reservation; for large concurrent promotions, move usage enforcement to an atomic MongoDB update/transaction or dedicated coupon redemption collection.
- Availability alerts are in-app status updates only; no external messaging provider is configured.
- Recently viewed tracking is recorded for signed-in customers only.
- Coupon discount is subtracted from the order total; review commission policy if commission should be calculated on pre-discount versus post-discount merchandise value.
