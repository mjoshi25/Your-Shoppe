# Phase 78.4 – Order Complaint Actions and Replies

## Implemented in source
- Vendors can see complaints associated with their own vendorUserId.
- Vendor complaint cards provide actions: In progress (`UNDER_REVIEW`), Waiting for customer, and Resolved.
- Vendors can send a customer-facing response (maximum 2,000 characters).
- Vendor update endpoint checks the logged-in account is ROLE_VENDOR and owns the complaint.
- A notification is sent to the customer when the vendor updates a complaint.
- Customer complaint page displays status, vendor reply, and Admin/Support response.
- Admin complaint page displays the vendor response and retains Admin status/resolution-note controls.
- Existing status values and complaint records remain compatible; the new vendorResponse field is additive.
- Status changes and replies do not trigger refunds or modify order/payment records.

## Required staging tests
1. Vendor A must not access or update Vendor B's complaint by changing the URL ID.
2. Vendor can set In progress and send a response; customer sees it and receives a notification.
3. Vendor can set Waiting for customer; a response is required.
4. Vendor can set Resolved.
5. Admin can still update status and admin resolution note.
6. Customer can see both vendor response and Admin response.
7. Verify CSRF-protected POST forms on desktop and mobile.

## Build status
Maven compilation and runtime tests have not been run in this environment. Run `mvn clean package -DskipTests` before deployment.
