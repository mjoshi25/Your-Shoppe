# Phase 27 – Vendor Coupons Navigation Fix

## Change
- Added **Vendor Coupons** to the Vendor workspace dropdown.
- Link target: `/vendor/coupons`.
- The menu is rendered only for users with the `VENDOR` role through the existing `sec:authorize="hasRole('VENDOR')"` workspace menu.
- No changes were made to coupon participation, product exclusions, customer offers, or coupon calculations.

## Verification
- Confirmed the existing `vendor/coupons.html` template is present in the source ZIP.
- Confirmed the existing vendor workspace menu is in `templates/fragments/layout.html`.
- ZIP integrity checked after packaging.
- Maven tests/build were not run as part of this menu-only change.
