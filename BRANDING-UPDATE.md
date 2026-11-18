# ASMJ Shoppe Branding Update

This update refreshes the existing ASMJ Shoppe brand assets while retaining the brand name and purple/orange palette. The new tagline is **One Platform. Many Possibilities.**

## Assets

- `src/main/resources/static/images/asmj-shoppe-logo.svg` — horizontal website/authentication/invoice logo.
- `src/main/resources/static/images/asmj-shoppe-mark.svg` — standalone square brand mark.
- `src/main/resources/static/favicon.svg` — vector browser favicon.
- `favicon.ico`, `favicon-16.png`, `favicon-32.png` — browser favicon sizes.
- `favicon-192.png`, `favicon-512.png` — PWA launcher icons.
- `apple-touch-icon.png` — iOS home-screen icon.
- `manifest.webmanifest` — installable web app metadata and icon declarations.

## Integration

- Updated the shared site header and authentication pages to use the new horizontal logo.
- Updated invoice branding.
- Added consistent favicon, Apple touch icon, theme colour and web manifest references across HTML pages.
- Added `?v=2` cache-busting to logo and icon references so browsers are prompted to fetch the new assets.
- Preserved application routes, authentication, business logic and marketplace functionality.

## Deployment / cache refresh

Deploy the updated application. After deployment, hard-refresh the browser (`Ctrl+F5`). If an Android PWA was already installed, remove the old home-screen shortcut and install it again after opening the updated website.

## Validation

SVG XML, PNG dimensions, ICO sizes and web manifest JSON were validated. ZIP integrity is checked after packaging. Maven compilation and live browser/PWA installation testing still need to be run in the project's deployment environment.
