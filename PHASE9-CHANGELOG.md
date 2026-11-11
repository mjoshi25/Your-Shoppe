# ASMJ Marketplace — Phase 9 changes

## Chat
- Added authorization-checked GET `/chat/{id}/messages` endpoint returning the current conversation messages.
- Conversation page polls every four seconds while visible; pauses while the browser tab is hidden and refreshes on tab focus.
- New messages are rendered using safe DOM text nodes, including attachment links.
- Added buyer/vendor last-read timestamps and unread counts in the corresponding enquiry inboxes.
- Existing form-based message posting and Cloudinary chat attachment support remain unchanged.

## Accounts and settings
- Added profile image URL, customer address, and email notification preference fields to UserAccount. Existing MongoDB user documents remain compatible with these optional fields.
- Added `/settings` for customer, vendor and admin users.
- Profile photo upload/removal, name/mobile update, password change with current-password validation.
- Customer delivery address can be saved in settings and is prefilled at checkout.
- Vendor business details can be updated; delivery and pickup settings remain in the existing vendor fulfilment settings screen.
- Shared navigation displays a profile photo or initial avatar, account name and Settings link.
- Dashboard displays a personalized welcome message.
- Profile images accept JPG/PNG/WEBP up to 5 MB and use Cloudinary folder `asmj-marketplace/profile-photos`.

## Configuration
- No new environment variables. Existing MongoDB Atlas, admin bootstrap and Cloudinary variables are still required.
- Render Dockerfile and Blueprint configuration are retained from Phase 8.

## Verification status
- ZIP integrity and expected template/view references checked.
- Maven compilation and JUnit tests were not executed in the generation environment because Maven is not installed there. Run `mvn clean test` before Render deployment.
