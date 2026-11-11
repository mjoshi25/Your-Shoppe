# ASMJ Shoppe – Phase 20 checkout and daily slot fix

## Fixes

1. Removed `#strings.substringBeforeLast(...)` from the checkout template. Thymeleaf options now use server-prepared `key`, `day` and `label` values from `OrderController`.
2. Added default daily delivery and pickup schedules for profiles that have no custom schedule, including older vendor records:
   - 09:00–12:00, capacity 10
   - 12:00–15:00, capacity 10
   - 15:00–18:00, capacity 10
   - Repeated Monday through Sunday.
3. Delivery and pickup defaults are independent. Vendors can edit either schedule in Vendor Settings; custom saved schedules replace the defaults.
4. Added `VendorProfileDefaultSlotsTest` to cover all-seven-day defaults and custom override behavior.

## Deployment notes

- No new environment variables or MongoDB migration are required.
- Vendors still need to configure their serviceable delivery PIN codes before delivery orders can be placed.
- Existing profiles with empty slot arrays use the default schedule automatically. When a vendor saves the settings form, the displayed schedule is saved to MongoDB.

## Validation

- Static assertions for the default slots and controller/template bindings: passed.
- HTML parser checks for checkout and vendor fulfilment templates: passed.
- Maven compilation and JUnit execution: not run in this environment (`mvn` is unavailable). Render build and tests must confirm the Java build.
