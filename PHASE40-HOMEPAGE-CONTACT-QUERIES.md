# Phase 40 – Homepage Contact Queries

## Included
- Added a responsive "Connect with ASMJ Shoppe" section to the homepage.
- Public one-way query submission collects name, email, phone, subject and message.
- Optional attachment upload supports JPG, PNG, WEBP, PDF, TXT, DOC and DOCX through the existing Cloudinary document uploader (8 MB maximum).
- Added basic server-side validation and a honeypot field for simple bot filtering.
- Stores submissions separately in MongoDB collection `contact_queries`.
- Added an admin-only read view at `/admin/contact-queries`, with submitted contact details, message and attachment link.
- Added the admin navigation link. No reply/chat workflow is included; staff contact the submitter outside the application.
- Existing marketplace, advertising, rental, payment and commission flows are unchanged.

## Configuration
- Attachment uploads require the existing Cloudinary environment variables: `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`.
- Without an attachment, contact queries can be submitted without Cloudinary.

## Validation
- 57 Thymeleaf HTML templates parsed with Python HTMLParser without parser exceptions.
- CSS parsed with tinycss2: 1,044 top-level rules, zero parse errors.
- Maven build and live MongoDB/Cloudinary tests were not run in this environment.
