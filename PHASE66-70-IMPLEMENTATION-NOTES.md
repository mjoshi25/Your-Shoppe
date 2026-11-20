# ASMJ Shoppe — Phases 66–70

Baseline: uploaded `Your-Shoppe-main (3).zip` (452 archive entries).

## Phase 66 — Vendor dashboard insights
- Added order status counters: awaiting action, in progress, completed and cancelled.
- Added average approved product rating and approved review count.
- Kept existing sales, product, order and low-stock metrics.
- Added direct dashboard access to buyer enquiries, vendor customers and customer complaints.

## Phase 67 — Vendor customer and enquiry workspace
- Added `/vendor/customers`, grouped from the vendor's own marketplace orders.
- Shows customer name/contact details already present on that vendor's orders, order count, latest order/status and total of completed, payment-verified orders.
- Added navigation to the existing `/vendor/enquiries` product-chat inbox.
- Existing inventory and stock-movement management is retained; no parallel inventory subsystem was introduced.

## Phase 68 — Product comparison
- Added customer-only comparison list using the HTTP session.
- Supports up to four active products and compare/remove actions.
- Comparison includes product, price, seller, category and stock availability.
- Added Compare actions to product catalogue cards and product details.
- Comparison selection is session-scoped; it is not persisted across devices.

## Phase 69 — Order complaint workflow
- Added customer complaint submission against an order, with ownership validation and length limits.
- Prevents multiple simultaneously open complaints for the same customer/order.
- Added customer complaint history, vendor read-only complaint view and Admin status/notes workflow.
- Admin/customer/vendor notifications are generated and complaint notifications route to the correct role-specific page.
- This workflow records complaints only. It does not automatically approve refunds, reverse vendor payments or change order/payment status.

## Phase 70 — Referral tracking and notifications
- Added customer referral code generation and referral registration attribution.
- Registration can prefill a referral code from `/register?ref=...`; invalid/inactive/non-customer referrers are rejected for customer signups.
- Referral dashboard shows attributed customer registrations.
- Existing in-app notifications remain in use, including new complaint alerts.
- No monetary/discount reward is issued in this phase; a reward programme needs separate terms, fraud controls and admin-configurable economics.

## Validation performed
- Uploaded ZIP archive integrity passed before extraction.
- Source-level checks performed for added files, route/template references, Java brace balance, form tag balance and `pom.xml` XML parsing.
- Existing routes/features were inspected before extending them.

## Not performed
- Maven compilation or test execution (Maven/Maven Wrapper not available in the working environment).
- Spring application startup, MongoDB integration, browser/mobile testing, Cloudinary integration or Render deployment.
- No production repository or Render service was modified.

## Pre-deployment test checklist
1. Log in as an approved vendor and verify the expanded dashboard metrics and Buyer Enquiries/My Customers/Customer Complaints links.
2. Confirm vendor customer rows contain only that vendor's own order customers.
3. As a customer, compare 1–4 active products; verify a fifth is blocked, and removal works.
4. Attempt comparison as a vendor/admin and confirm customer-only access is denied.
5. Submit a complaint on an order belonging to another customer by changing the URL; access must be denied.
6. Submit one complaint, verify Admin and vendor notifications, update status as Admin, and verify customer notification/history.
7. Register a new customer with a valid referral link; verify attribution appears on the referrer's dashboard.
8. Try an invalid referral code and a vendor account code; ensure no referral attribution is recorded.
9. Verify existing customer/vendor registration without a referral code remains unchanged.
10. Test all new pages at mobile, tablet and desktop widths.
