# ASMJ Shoppe – Financial Integrity Fixes

This patch is based on `Your-Shoppe-main (5).zip` and is supplied as a separate review copy.

## Changes

1. **Commission eligibility:** `CommissionService.recordCompletedOrder` now independently requires `COMPLETED`, `VERIFIED`, and recorded payments greater than or equal to the order total. The unique `orderId` index remains the final duplicate guard; duplicate-key races are handled without creating a second ledger row.
2. **Merchandise refunds:** commission basis is reduced by merchandise refunds when the commission is first created. If an existing commission is still `UNPAID`, it is recalculated after a merchandise refund. A commission already marked `PAID` is intentionally not silently rewritten; a separate credit/debit adjustment workflow is still required for that case.
3. **Coupon usage limits:** a new `CouponUsageService` reserves usage with a single atomic MongoDB `findAndModify` operation that checks activity, date window and usage limit. The order stores a reservation flag. Customer/vendor cancellation releases the reservation exactly once. A failed order save releases the reservation in the request error path.
4. **Delivery quotes:** checkout now rejects a distance quote if the vendor's current free-distance allowance or per-kilometre rate differs from the values captured in the quote.
5. **Tests:** commission tests now set completed/fully-paid order state and include checks for incomplete orders and merchandise-refund recalculation.

## Coupon reservation policy

A coupon is reserved at successful order placement, not at payment or vendor acceptance. This prevents simultaneous checkouts from exceeding the usage limit. A customer/vendor cancellation releases the reservation. Orders that remain pending/awaiting acceptance continue to occupy a reservation until cancelled; automatic expiry/release of abandoned orders is not included in this patch.

## Validation limitations

Maven is not installed in the available build environment and the project has no Maven wrapper, so Maven compilation and automated tests could not be executed here. Review and run `mvn test` locally (Java 17, Maven 3.9.x) and validate the MongoDB atomic reservation query against a staging database before deployment. Do not deploy directly to production from this ZIP.
