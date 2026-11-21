# Phase 79 – Paid Shop Advertising Directory

## Business rule
A normal vendor profile is not automatically listed. A shop appears in `/shops` only when its shop advertisement has verified payment, Admin approval, APPROVED status, and the current India-local date is within the submitted campaign dates. The vendor account must also remain enabled and Admin-approved.

## Vendor workflow
- Approved vendors can submit a paid shop advertisement from Vendor workspace → Advertise my shop.
- Default package is ₹499 for 30 days. Both values are configurable using `APP_SHOP_ADVERTISING_PRICE` and `APP_SHOP_ADVERTISING_DURATION_DAYS`.
- Vendor selects an Admin-managed shop category, enters shop name/description/start date, uploads a banner and optional logo, supplies UPI UTR and payment screenshot.
- Payment details use the existing Admin-configured advertising UPI ID, payee and QR settings.
- Pharmaceutical Store category requires a pharmacy licence number and licence document for manual Admin review.
- The submission is not publicly visible while payment or approval is pending.

## Admin workflow
- Admin → Shop advertisements.
- Admin can create/deactivate shop categories.
- Admin reviews the UTR/payment proof and verifies payment.
- Admin separately approves or rejects the advertisement; rejection reason is recorded and vendor notified.
- Admin can suspend an approved advertisement.
- Submission and approval notifications are sent to Admin/vendor; audit events are recorded.

## Public experience
- `/shops` is public and linked in the Shop navigation menu.
- Directory supports shop-name search and category filtering.
- Approved active shops rotate in a featured carousel every five seconds, with manual previous/next controls and pause-on-hover.
- The same page lists all matching active advertised shops in a grid.
- Clicking a shop opens `/shops/{advertisementId}`, which displays its banner, shop details, location, and only ACTIVE/in-stock products belonging to that vendor.
- Product cards open the existing `/products/{productId}` detail page; customers continue using the existing cart, checkout and vendor-direct payment process.
- Impression and click counts are recorded.

## Separation from product advertising
This is a separate shop-level advertisement collection and workflow. Existing product/banner campaigns are unchanged.

## Configuration
- `APP_SHOP_ADVERTISING_PRICE` (default 499)
- `APP_SHOP_ADVERTISING_DURATION_DAYS` (default 30)
- Admin advertising UPI settings must be configured in the existing advertising settings before vendor submissions are accepted.
- Cloudinary must be configured for banner/logo/payment/licence uploads.

## Important limitations / review items
- Pharmacy licence documents are collected for manual Admin review; the application does not independently validate licence authenticity or implement prescription validation for individual medicines. Do not approve pharmaceutical shop advertising or enable regulated medicine sales without the required compliance review and product-level prescription safeguards.
- The implementation was not Maven-compiled or runtime-tested in this environment because Maven is not installed here.

## Required verification before production
1. Maven build and Render staging deployment.
2. Vendor cannot submit as non-vendor, pending, rejected or suspended vendor.
3. Pending/unverified/unapproved/expired/suspended ads never appear on `/shops` or `/shops/{id}`.
4. Admin verifies payment and approves separately; duplicate UTR is rejected.
5. Vendor cannot approve or edit another vendor's ad (vendor has submission/list only in this phase).
6. Directory name/category filters work on desktop and mobile.
7. Carousel rotates every five seconds, manual controls work, and impressions/clicks update.
8. Storefront shows only products belonging to the advertised vendor with ACTIVE status and available stock.
9. Product detail/cart/checkout/payment flow remains unchanged.
10. Pharmacy licence proof is visible to Admin and reviewed before approval.
