# ASMJ Shoppe — Combined Phases 29–32 UI/UX Redesign

## Scope
This release continues from the Phase 28 UI foundation and completes the customer shopping experience, cart/checkout presentation, role dashboards and shared operational-page styling.

## Phase 29 — Customer marketplace
- Reworked homepage hero, discovery tiles, marketplace value proposition and vendor call-to-action.
- Reworked product catalogue search/filter panel, responsive product cards, pricing, fulfilment badges and compact enquiry disclosure.
- Reworked product detail page, seller information, rating/review presentation, purchase actions, wishlist and stock-alert placement.
- Existing controller attributes, product enquiry, category/subcategory fetch, wishlist, stock alert and cart routes retained.

## Phase 30 — Cart and checkout
- Reworked cart item layout, quantity controls, subtotal, coupon area, checkout summary and mobile stacking.
- Reworked checkout headings, progress indicator, fulfilment/payment sections and sticky desktop summary.
- Existing checkout form names, Google Places/Maps map picker, delivery PIN serviceability, Routes API quote, date/slot logic, coupon calculation, UPI-after-acceptance messaging and order POST retained.
- No payment or pricing business logic changed.

## Phase 31 — Role dashboards
- Admin dashboard: vendor status summary, marketplace metrics, moderation, commissions, rewards, coupons, reporting and audit shortcuts.
- Vendor dashboard: catalogue, orders, inventory, fulfilment settings, coupons, rewards, commissions and review shortcuts, plus recent orders.
- Customer dashboard: order count, marketplace discovery, wishlist, recently viewed, offers, alerts and enquiries.
- Existing `DashboardController` routes, `pageTitle` conditionals and model attributes retained.

## Phase 32 — Operational-page system
- Unified page headings, metric cards, filters, forms, notices, tables, status pills, order panels, chat bubbles, reviews, reports, settings and responsive overflow behavior.
- Added responsive refinements for 320px–680px phones, tablets and wide desktop layouts.
- Added reduced-motion support and stronger focus-visible styles.
- Existing Thymeleaf forms and action routes are not removed.

## Compatibility / safety
- No Java source files changed.
- No controller, service, repository, MongoDB document, Spring Security, environment variable, Cloudinary, Google Maps, payment or commission code changed.
- No Render configuration or production deployment was changed.
- The complete ZIP is intended for a separate local/staging test first. It must not replace the production deployment until the owner approves.

## Validation
- ZIP archive integrity: checked after packaging.
- HTML parser: all 42 templates parsed without parser exceptions.
- Inline JavaScript: all 6 inline script blocks passed `node --check`.
- CSS: braces balanced; no browser rendering engine test was available.
- Maven tests/compile: not run because Maven is not installed in the build environment.
- Thymeleaf runtime rendering, Spring Security role rendering, real-device visual testing, Google API calls and complete end-to-end order tests remain to be run by the owner in local/staging.
