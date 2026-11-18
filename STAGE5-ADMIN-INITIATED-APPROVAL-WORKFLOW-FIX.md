# ASMJ Shoppe – Admin-initiated approval workflow correction

## Business rules
- Admin accounts are normalized at startup to `VendorStatus.NOT_APPLICABLE` and enabled; they are not vendor profiles and are excluded from the vendor manager query (which selects `ROLE_VENDOR`).
- Admin users cannot create or respond to submitter-side approval cases.
- Non-admin users initiate approval cases for records they own.
- The Approval Centre lists incoming cases from non-admin owners. Admin may request clarification and then decide after the submitter responds.
- Admin has no create-information-request action. The old `/admin/approval-center/new` view and form were removed.

## User workflow
1. User opens Account > My approval submissions.
2. User chooses module, record type and their own record ID, adds a message and optional documents.
3. Server resolves the source record, verifies ownership against the signed-in account, and derives the item title from MongoDB.
4. A case is created with `SUBMITTED` status and an initial `SUBMITTED_FOR_APPROVAL` history entry.
5. Admin reviews the incoming request, may request clarification, and can approve/reject after the user responds.

## Deployment notes
- No MongoDB collection migration is required; existing `approval_cases` documents remain readable.
- Existing historical cases owned by Admin are hidden from the incoming queue and blocked from detail/action routes.
- Existing module-specific approval mechanisms remain in place.
- Admin account status normalization runs on application startup.
- The user submission page currently accepts an existing record ID; later UX work can add direct “Submit for Admin approval” actions beside each module's save/edit form.
- Uploaded documents continue to use the current Cloudinary upload service; private authenticated delivery remains a separate production security task for sensitive files.
