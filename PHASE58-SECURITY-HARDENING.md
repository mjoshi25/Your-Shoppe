# Phase 58 — Security Hardening

## Changes applied in this source update
- Removed the broad public `/chat/**` authorization exception.
- Added only the public chat endpoints needed for guest product enquiries and existing guest conversation flows. ProductChatController performs participant/session-key checks before conversation reads and writes.
- Added the enabled-account check to the vendor orders route, in addition to the existing vendor role and approval checks.
- Changed the session cookie Secure default to `true`; local HTTP development can explicitly set `SESSION_COOKIE_SECURE=false`.
- Added a restrictive Permissions-Policy response header.
- Added Content-Security-Policy in **Report-Only** mode. This does not block content yet; it allows policy validation before enforcement so existing inline scripts and third-party integrations are not unexpectedly broken.
- Retained Spring Security CSRF protection, BCrypt password encoding, session-fixation migration, HSTS, frame denial, content-type protection and referrer policy.

## Important limitations — not a security certification
This phase is a targeted hardening pass, not a complete audit or penetration test. The following still require implementation and/or verification:
1. Distributed login and registration rate limiting / brute-force protection.
2. Full controller/service-level IDOR and ownership audit across every marketplace module.
3. Cloudinary upload validation, file-size/type/content checks and ownership validation across all upload endpoints.
4. Order, coupon, payment-evidence, refund and commission tampering tests.
5. MongoDB Atlas least-privilege users, network allow-list, backup/restore test and secret rotation review.
6. Render environment review, HTTPS/custom-domain verification and browser-key referrer restrictions.
7. Dependency vulnerability scanning and automated security regression tests.
8. Review CSP reports and integrations, then move from Report-Only to enforced CSP with a tested allow-list.

## Deployment notes
- Set `SESSION_COOKIE_SECURE=true` in Render (now also the application default).
- Keep MongoDB, Cloudinary API secret and Google Routes API key in Render environment variables only.
- Do not commit production secrets.
- Test guest product chat, authenticated buyer/vendor chat, vendor pending/approved/suspended states, login/logout and all role dashboards before deployment.
- This update is a separate source ZIP. No production deployment was performed.
