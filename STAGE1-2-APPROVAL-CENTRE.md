# Stage 1 & 2 — Common Approval Support Centre

## Included
- MongoDB `approval_cases` collection with owner, module, subject reference, status, decision metadata, uploaded-file metadata and chronological history.
- Admin Approval Centre: list cases, create an information request linked to an existing record, request further information with optional reference attachments, review responses and record an approval-support decision.
- Submitter Approval Requests: view own requests, review admin instructions/history and respond with text and multiple supporting files.
- Ownership checks ensure a submitter can only read/respond to their own cases. Admin endpoints require `ROLE_ADMIN`.
- Dashboard and account navigation links for admins and submitters.
- Uploads reuse the existing Cloudinary document uploader and its 8 MB per-file validation and allowed MIME types.

## Routes
- `GET /admin/approval-center`
- `GET, POST /admin/approval-center/new`
- `GET /admin/approval-center/{id}`
- `POST /admin/approval-center/{id}/request`
- `POST /admin/approval-center/{id}/decision`
- `GET /my-approval-requests`
- `GET /my-approval-requests/{id}`
- `POST /my-approval-requests/{id}/respond`

## Important scope boundary
Stage 1 & 2 establish a reusable request, response, attachment and history workflow. They do not yet automatically create approval cases from every existing pending vendor/product/rental/service/employer/job record, nor does an approval-support decision automatically update those module records. That integration is Stage 3 and must be implemented per module to preserve each module's existing approval rules.

## Production security note
This implementation reuses the project's existing `CloudinaryImageService.uploadDocument`, which returns secure HTTPS URLs but currently uses the default Cloudinary delivery access mode. Do not use it for highly sensitive identity, licence or financial documents in production until private/authenticated Cloudinary delivery and server-authorized document retrieval are implemented and tested. The app restricts access to case records, but a publicly delivered Cloudinary URL can be shared outside the app.

## Testing
- ZIP archive integrity verified after packaging.
- Maven build not run in this environment because `mvn` is not installed and the project does not include Maven Wrapper.
- MongoDB/Cloudinary integration and browser workflows require local or Render environment testing.
