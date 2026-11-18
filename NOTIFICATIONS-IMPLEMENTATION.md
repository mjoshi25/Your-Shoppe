# ASMJ Shoppe – In-app Notifications

## Included
- Authenticated Admin, Vendor and Customer accounts see the notification bell in the shared site header.
- The bell displays the current unread count and opens the notification centre.
- Notification centre lists recent notifications, supports marking all as read, and opens each notification.
- Notification ownership is checked against the logged-in user's account before opening or marking as read.
- Action destinations are resolved on the server from the notification type, actual record and authenticated role. A client-supplied URL is never trusted.
- Approval notifications route Admin to the Approval Centre record and the submitter to their own approval request.
- Order notifications route Admin to Admin Orders, the owning Vendor to Vendor Orders and the owning Customer to Customer Orders.
- Initial notification producers cover approval submission/clarification/decision/response, order placement/status changes and customer UPI proof submission.

## Database
MongoDB collection: `marketplace_notifications`. No manual collection creation is required; MongoDB creates it on first save.

## Important scope note
This is the first in-app notification release. It does not send email, SMS, WhatsApp or browser push notifications. Additional event producers for rentals, services, jobs, chat and other marketplace modules can be added in subsequent increments using `NotificationService.notifyUser(...)` or `notifyAdmins(...)`.

## Verification
ZIP integrity and source-level checks should be performed before deployment. Maven packaging could not be run in this environment because Maven is not installed. Do not deploy until `mvn clean package` succeeds locally or in CI and the listed role-based workflows are tested.
