# ASMJ Shoppe – Phase 20 payment and order chat correction

## Changes
- UPI payment evidence is now verified by the order's own vendor, not by admin.
- Vendor verification/rejection is restricted to the vendor who owns the order. Rejection requires a reason; customer can resubmit evidence.
- UPI orders cannot move to PROCESSING until payment status is VERIFIED by the vendor.
- Admin orders page is read-only for UPI verification and displays vendor verification status.
- Customer and vendor order cards now include an order-specific chat thread with text and optional image/PDF attachment.
- Order chat is embedded in the order document and access is limited to that order's customer and vendor.
- Audit events record vendor payment decisions and order chat messages.

## Test checklist
1. Customer places UPI order; order remains PLACED / PENDING.
2. Customer submits UTR and screenshot; status becomes PROOF_SUBMITTED.
3. Vendor sees proof and verifies; status becomes VERIFIED.
4. Vendor can then move order to ACCEPTED/PROCESSING according to existing workflow.
5. Attempting PROCESSING before verification is rejected.
6. Vendor rejects with a reason; customer sees note and can resubmit.
7. Admin can view but cannot verify UPI evidence.
8. Customer and vendor can send order chat messages and attachments; unrelated accounts are denied.
9. Confirm existing product enquiry chat and order time negotiation still work.

Maven compilation and JUnit execution must be confirmed by the user's local build or Render; they were not run in this environment.
