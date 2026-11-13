# Phase 27 – Vendor-approved coupons

## Included
- Vendor coupon participation page at `/vendor/coupons`.
- Vendors explicitly opt in to active coupon codes per shop.
- Vendors can exclude/include their own products from coupon discounts.
- Customer offers remain visible; the offers page explains that discounts require shop participation and product eligibility.
- Checkout recalculates eligible subtotal and discount on the server.
- Discount is allocated to eligible order lines and saved as a per-line snapshot.
- Invoice shows eligible subtotal, per-line coupon savings, coupon discount, delivery charge and final total.
- Existing orders retain saved discount values if vendor preferences change later.

## Important existing architecture constraint
The current checkout explicitly permits one vendor per order and rejects mixed-vendor carts. This release therefore applies one coupon to eligible products within the single vendor's order. A single mixed-vendor cart/order with independent vendor participation requires a separate multi-vendor checkout/order-splitting design and is not represented as implemented here.

## Verification
ZIP integrity and source-level checks only. Maven is not installed in the build environment; run `mvn clean test` locally before deployment. Test vendor opt-in/out, product exclusions, coupon minimums/caps, checkout tampering, invoice totals and changes after order placement.
