# Phase 71 — Edit and Delete Unapproved Marketplace Listings

## Scope
Adds owner-controlled edit and delete actions to marketplace extension submissions (Events, Courses & Training, Wholesale, Delivery and Freelance) while their status is not APPROVED.

## Changes
- Vendor's My Marketplace Activity cards now show Edit and Delete actions for non-approved listings.
- Listing detail pages expose the same actions.
- Edit form prepopulates existing listing fields and category/subcategory selections; current Cloudinary image is retained unless a replacement is uploaded.
- Saving an edit preserves the listing owner, ID and original creation timestamp, updates the modified timestamp, and returns the listing to PENDING for Admin review.
- Approved listings cannot be edited or deleted. The restriction is enforced server-side as well as in the UI.
- Delete is a POST action with CSRF protection and verifies that the signed-in user owns the listing.
- Security rules require authentication for edit/delete and vendor listing detail routes.

## Validation
- ZIP integrity test passed.
- Static source checks performed for route definitions, owner checks, approval lock, form binding and CSRF forms.
- Maven compilation, automated tests, MongoDB integration and browser/Render testing were not performed in this environment.
