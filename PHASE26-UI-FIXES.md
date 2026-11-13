# Phase 26 – Registration and Cart UI Fixes

## Changes
- Cart: the “Browse available offers” link is now rendered only for authenticated users with the CUSTOMER role using the Spring Security Thymeleaf dialect. Vendor and admin sessions do not see this customer-only link. The customer offers route and feature are unchanged.
- Registration: Terms & Conditions checkbox and label are aligned in one horizontal flex row. The checkbox remains mandatory and unchecked by default, and the complete label remains clickable. A larger 20px checkbox improves touch usability.

## Verification
- Source-level checks: passed (role condition present; label wraps checkbox and text; required attribute retained).
- Maven compilation, Thymeleaf runtime rendering, and browser/device tests: not run in this environment. Verify customer and vendor sessions separately after deployment.
