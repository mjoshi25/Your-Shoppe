# ASMJ Shoppe — Phase 28: UI/UX Redesign Foundation

## Baseline inspected
- Source baseline: `ASMJ_Shoppe_Phase_27_Vendor_Coupons_Menu_Fix.zip`.
- Archive integrity: passed before changes (175 entries; no corrupt members).
- Stack and application structure retained: Java 17, Spring Boot, Thymeleaf, Spring Security, MongoDB and Cloudinary.
- No Java controllers, services, repositories, entities, security rules, application properties, or business logic were changed in this phase.

## Existing UI work found in the baseline
- ASMJ Shoppe purple/gold visual identity, logo and favicon assets (Phase 20).
- Public landing page with hero, feature cards and vendor/customer calls to action.
- Responsive starter product grid and two-column product detail layout.
- Responsive cart/checkout grid and form layouts.
- Shared role-aware account, vendor workspace and administration navigation.
- Dashboard metric cards, vendor/admin management screens, responsive form grids and horizontally scrollable data tables.
- Existing mobile breakpoints for landing, product cards, product details, chat, order workflows, registration and checkout.
- Phase 26 registration terms-checkbox alignment and customer-only offers link correction.
- Phase 27 vendor coupon navigation entry.

## Phase 28 changes
- Rebuilt the shared header into a marketplace-style layout with brand, global product search, cart, account actions and role-specific workspace menus.
- Search submits to the existing `/products?q=...` endpoint; no new backend route is introduced.
- Added a keyboard-accessible skip-to-content link and `main-content` target to pages with a main element.
- Consolidated the active UI tokens around the existing ASMJ purple and gold brand palette.
- Improved shared content widths, card surfaces, button/input focus states, table overflow behavior and responsive spacing.
- Added tablet/mobile header layouts and a narrow 320–380px treatment.
- Added reduced-motion handling.
- Preserved all existing navigation destinations and role conditions, including Vendor Coupons and Seller Excellence Rewards.

## Remaining redesign milestones
- Phase 29: customer shopping experience — homepage, search/category discovery, product cards, product detail and review presentation.
- Phase 30: cart, checkout, address/map selection, delivery quote, payment and refund presentation.
- Phase 31: customer, vendor and admin dashboard information architecture and responsive dashboard widgets.
- Phase 32: operational screens — orders, chat, inventory, coupons, rewards, commissions, reports, settings, forms, tables and final accessibility/responsive regression.

## Validation performed
- Archive integrity checked after packaging.
- HTML templates parsed with an HTML parser.
- Verified all pages using the shared header retain the expected role-specific route targets.
- Verified global search uses the existing GET `/products` route and `q` parameter.
- Verified every template main element receives the skip-link target where applicable.
- Maven build, Thymeleaf runtime rendering and real-device/browser testing are not claimed as passed; run the Maven test suite and test on a staging deployment before merging or deploying.
