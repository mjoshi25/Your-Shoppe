# Phases 33–37 – Advertising Admin Edit and Action Visibility Fix

## Reported issues
- Admin campaign edit details were not populated / edit flow was unavailable.
- Action button text was white on a light background and hard to distinguish.
- An administrator clicked the action control intending to edit and the campaign was suspended.

## Changes
- Added a dedicated, populated admin campaign edit page at `/admin/advertising/{id}/edit`.
- Added campaign creative/targeting update handling. Existing payment verification, paid amount, placement and purchased campaign dates are preserved. Any creative change resets creative approval to Pending so the revised creative must be approved again.
- Added visible Edit actions for existing placements and fixed-price packages. Their edit forms load existing values and open automatically.
- Added high-contrast action styles for Edit, Suspend, Restore and all secondary advertising admin buttons.
- Renamed the destructive action to **Suspend campaign**, added a confirmation prompt and provided a safe Restore action for previously approved, paid, non-refunded campaigns whose dates have not expired.
- Restore checks placement enablement and concurrent campaign capacity.
- Added a regression assertion for the new admin edit template and explicit actions.

## Data safety
- No MongoDB fields or collection names changed.
- No customer checkout, vendor order payment, vendor commission, coupon or refund workflow changed.
- Campaign payment evidence and payment verification remain intact when editing creative details.
- Editing a creative requires a fresh admin creative approval; it does not require paying a second time.
- Refund and expired campaigns cannot be edited or restored.

## Validation
- HTML parser: all 48 templates parsed without parser errors.
- Inline JavaScript: 7 blocks passed `node --check`.
- CSS parser: 975 rules, zero top-level parse errors.
- Maven tests/build not run because Maven is not installed in the build environment.
- Live Thymeleaf rendering, MongoDB integration and browser verification remain to be performed locally/staging.


## Campaign status visibility follow-up
- Added a dedicated Campaign status column beside Creative status. It shows the actual campaign lifecycle value (APPROVED, SUSPENDED, EXPIRED, REFUNDED, or pending/rejected state) with a high-contrast badge.
- Removed the duplicate low-visibility status text from the campaign/vendor cell.
- This is a display-only admin change; it does not modify campaign records or payment/approval state.
