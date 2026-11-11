# ASMJ Shoppe — Phase 10

## Included

### Consistent action buttons
- Refined primary, secondary, danger and success button styles across the application.
- Improved hover, focus-visible, pressed and disabled states.
- Better touch sizing and full-width action buttons on small screens.

### Vendor onboarding communication
- Added one persistent MongoDB conversation per vendor application (`vendor_application_conversations`).
- Admin can message a vendor from the admin vendor detail page, request documents and attach files.
- Vendor can reply from the vendor application status page before approval, including while status is PENDING or REJECTED (and while suspended, if the account remains enabled).
- Messages display sender, role and timestamp. Attachments are stored in Cloudinary using the existing document upload validation (maximum 8 MB; JPG, PNG, WEBP, PDF, TXT, DOC, DOCX).
- Existing vendor approval, rejection and review-history actions remain available.

## Security
- Admin send endpoint is under `/admin/**` and checks ROLE_ADMIN again in the controller.
- Vendor reply endpoint is restricted to ROLE_VENDOR and uses the signed-in account's own user ID; no vendor ID is accepted from the browser.
- `/vendor/application-chat` is explicitly permitted for authenticated vendor role before the general approved-vendor-only `/vendor/**` rule, so pending vendors can communicate but do not gain access to product/order routes.
- CSRF protection remains enabled for message forms.

## MongoDB
- New collection: `vendor_application_conversations`.
- Each conversation stores vendor user ID, vendor display name/email, messages, timestamps and attachments metadata/Cloudinary URLs.
- No existing collection or field is renamed. Existing product chat collection is unchanged.

## Test checklist
1. Register a vendor and sign in while PENDING.
2. Open Vendor application status; send a text message to admin.
3. Attach a supporting PDF or image and send it.
4. Sign in as admin; open Manage vendors → vendor detail.
5. Confirm the vendor message and attachment are visible.
6. Admin requests another document and attaches a file.
7. Sign back in as the pending vendor; confirm admin reply is visible and reply.
8. Confirm the full history remains after logout/login and page refresh.
9. Approve vendor; verify approval controls and normal vendor routes still work.
10. Verify a customer or another vendor cannot access the onboarding conversation endpoints.
11. Check button styles on desktop and mobile after Render deployment.

## Configuration
- Uses existing MongoDB Atlas and Cloudinary environment variables; no new environment variables.
- Cloudinary folder: `asmj-marketplace/vendor-onboarding`.
