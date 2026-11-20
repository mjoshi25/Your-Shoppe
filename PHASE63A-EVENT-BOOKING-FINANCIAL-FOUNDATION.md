# Phase 63A — Event booking financial foundation

## Included
- Admin-configurable event booking charge: disabled/enabled, fixed, percentage, or both.
- Event vendor opt-in checkbox for ASMJ Shoppe online booking. An event that is not opted in rejects platform booking requests and remains a free listing/contact path.
- Booking-time snapshot of ticket unit price, quantity, customer amount payable to vendor, fixed/percentage settings and calculated platform fee.
- When an event request is confirmed, the vendor-to-marketplace fee is recorded as PAYABLE/UNSETTLED in `marketplace_financial_entries`.
- Admin pages: `/admin/event-booking-settings` and `/admin/financial-ledger` (both protected by existing `/admin/**` security rule).
- Existing booking payment is not marked PAID; no payment gateway or vendor remittance verification is invented.

## Financial direction
- Ticket amount: CUSTOMER -> VENDOR (direct; payable to vendor).
- Platform booking fee: VENDOR -> ASMJ_MARKETPLACE (payable after vendor confirms the booking).
- The platform does not collect customer ticket funds.

## Scope still to implement in subsequent Phase 63B–63E
- Payment/settlement update workflows with verified UTR/proof and audit trail.
- Universal invoice/receipt issuance and PDF generation/access for all modules.
- Integration of service, rental, course, wholesale, delivery and product order transaction records into the common financial ledger.
- Credit notes/refund accounting and reconciliation dashboards.

## Important
This phase is a foundation, not a completed universal invoice system. Existing records are not retroactively repriced. Compile and test on the project build environment before deployment.
