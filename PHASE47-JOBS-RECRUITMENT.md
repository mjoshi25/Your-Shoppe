# Phase 47 — Job Openings & Interview Management

## Included
- Public job search and job detail pages.
- Existing signed-in customer accounts can apply; candidates upload PDF/DOC/DOCX resumes to the existing Cloudinary document uploader (8 MB maximum).
- Employer/company/recruitment consultancy profile application; admin approval required before job posting.
- Employer job posting with mandatory admin moderation before public publication.
- Employer application workspace with candidate contact details and resume link.
- Employer can upload an offer letter (PDF/DOC/DOCX, up to 8 MB) to Cloudinary; candidate can open the uploaded offer letter from application tracking.
- Candidate pipeline statuses: APPLIED, UNDER_REVIEW, SHORTLISTED, INTERVIEW_SCHEDULED, REJECTED, SELECTED, OFFERED, OFFER_ACCEPTED.
- Multiple interview rounds, date/time, mode, interviewer, meeting link/location, feedback and pass/fail result.
- Candidate application tracking page.
- Admin employer verification and job moderation page at `/admin/jobs`.
- Homepage Find Jobs and Hire Candidates cards are active links; navigation includes Find Jobs, Employer workspace and Jobs administration.

## Workflow and initial policy
- Existing customer account is used as the candidate identity; no new role is added.
- Employer profile is linked to the signed-in account and must be approved by an administrator.
- Employer-created jobs start in PENDING and are not visible publicly until admin changes status to PUBLISHED.
- Job posting is free in this initial phase; paid featured vacancies / per-post fees are not implemented.
- Admin approval is mandatory for employer profiles and each vacancy.

## Important limitations / follow-on work
- Resume is stored as a Cloudinary document URL; private authenticated proxy delivery and expiry controls are not implemented. Configure Cloudinary access policy appropriately before production use.
- Offer letter file upload is included. Automated offer-letter document generation and email/SMS notifications are not included; employers upload their prepared offer document and can mark OFFER_ACCEPTED manually.
- Interview reminders, calendar integrations, candidate profile/resume builder, recruiter multi-user seats, payments, paid job boosts and hiring analytics are follow-on work.
- Candidate applications require a customer account. Public browsing is allowed.
- The module uses the existing login accounts; it does not introduce a separate employer role.

## Routes
- Public: `GET /jobs`, `GET /jobs/{id}`
- Customer: `POST /jobs/{id}/apply`, `GET /candidate/job-applications`
- Authenticated employer account: `/employer/register`, `/employer/dashboard`, `/employer/jobs/**`
- Admin: `/admin/jobs`

## Validation
Source-level checks and ZIP integrity are performed for this release. Maven compilation and live Render/Cloudinary integration must be verified by the project owner before deployment.
