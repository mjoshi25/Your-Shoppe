# ASMJ Shoppe — Phase 63B to 63E

## Included in this delivery
- Vendor remittance submission for recorded marketplace receivables using UTR/reference, payment method and optional proof URL.
- Remittances remain SUBMITTED_FOR_VERIFICATION until an Admin explicitly verifies them.
- Admin verify action marks PAID/SETTLED; reject action returns the amount to PAYABLE/UNSETTLED and records a reason.
- Audit events are recorded for vendor submissions and Admin verification/rejection.
- Vendor sees only financial entries where they are the payer. Admin sees all entries.
- Event booking statement/print-to-PDF route is available to the customer who submitted the request, the owning vendor and Admin. It clearly distinguishes statement/payment advice from proof of payment.
- Existing product order invoice route and ownership checks are preserved.
- Event platform charge ledger creation is idempotent by request/component.

## Payer/payee conventions
- Event ticket amount: CUSTOMER -> VENDOR.
- Event platform booking charge: VENDOR -> ASMJ_MARKETPLACE.
- A submitted UTR is not treated as paid until Admin verifies it.
- The invoice/statement never implies ASMJ collected customer funds.

## Explicit remaining limitations (must be tested/extended before production)
- PDF output uses browser Print / Save as PDF; no server-generated PDF engine was added.
- Existing order invoice is retained, but this delivery does not retrofit all service, rental, course, wholesale and delivery legacy records into financial entries because those models do not share a consistent confirmed amount/payment contract. Integrate each module only after confirming its payer, payee, amount, and payment trigger.
- Refund/credit-note workflow and reconciliation summaries are not fully implemented. Do not mark a refund complete from a request alone.
- Optional proof URL is a reference link, not an uploaded/validated document.
- Compile and test on the project build environment before deployment.
