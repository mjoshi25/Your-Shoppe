# ASMJ Shoppe UI/UX redesign roadmap

The redesign is being delivered as independent milestones on a copy of the latest Phase 27 project ZIP. The production repository and Render deployment are not modified by this work.

| Phase | Scope | Status |
|---|---|---|
| 28 | Shared design foundation, navigation, global search, responsive shell, accessibility basics | Implemented; static validation pending final build |
| 29 | Customer homepage, product discovery, product cards and product detail | Pending |
| 30 | Cart, checkout, delivery address/map, payment and refund UI | Pending |
| 31 | Customer, vendor and admin dashboards | Pending |
| 32 | Orders, chat, inventory, coupons, rewards, commissions, reports, settings and final regression | Pending |

Business rules and existing Spring Boot/Thymeleaf/MongoDB/Cloudinary integrations must remain unchanged unless a UI cannot be completed without a separately approved backend change.


## Combined Phases 29–32 implementation notes
- Rebuilt the public homepage into a marketplace landing experience while preserving the existing public routes and role-aware calls to action.
- Redesigned catalogue filters and product cards. Existing `/products`, category/subcategory API, product enquiry, cart add and product detail routes remain in use.
- Redesigned product details and reviews while preserving wishlist, stock-alert, enquiry and review bindings.
- Redesigned cart and checkout presentation. Existing cart, coupon, serviceability, map selection, distance quote, slot, payment and order submission forms/scripts remain in place.
- Redesigned the shared dashboard view for admin, vendor and customer using the existing `pageTitle` and model attributes. No controller model changes were made.
- Added a common responsive presentation layer for operational pages (orders, reports, commissions, coupons, inventory, rewards, chat, settings, forms and tables).
- No Java source, controllers, services, repositories, MongoDB entities, security rules, application properties, environment configuration or deployment files were changed.
- This is a UI-only combined milestone. Do not deploy to production until the complete role-by-role workflow regression has passed in a separate staging environment.

## Phases 33–37 — Paid Advertising (implemented in this release)
- Phase 33: MongoDB advertising foundation, placement inventory, fixed-duration packages, UPI settings and separate payment/creative states.
- Phase 34: Vendor campaign creation, banner uploads, internal destination/product/category targeting, PIN targeting and UPI proof submission.
- Phase 35: Admin placement/package/UPI configuration, payment verification, creative approval/rejection, campaign suspension and capacity checks.
- Phase 36: Sponsored placements on homepage, category/search, product detail and cart pages; sponsored disclosure; no checkout ads.
- Phase 37: Deduplicated session-level impressions/clicks, vendor/admin analytics, daily rotation and automatic campaign expiry.
- Phase 38 remains: user-side responsive, browser, MongoDB, Cloudinary and complete end-to-end acceptance testing.
