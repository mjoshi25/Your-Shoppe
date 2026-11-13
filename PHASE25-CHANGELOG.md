# ASMJ Shoppe Phase 25 – Registration, Vendor Actions and API-Key Security

## Included
- Customer and vendor registration now require an unchecked-by-default Terms & Conditions checkbox. Server-side validation rejects missing consent.
- Accepted terms version (`1.0`) and UTC acceptance timestamp are stored on the user account.
- Public `/terms` page is available before registration. The current text is an initial operational draft and requires legal review before public launch.
- Vendor order action panel uses “Your next action” language and clearer fulfilment instructions while preserving the existing guarded transition buttons and UPI verification requirement.
- Phase 24 controlled sample-product creation/deletion flags and marked-batch cleanup are retained.
- Customer offers link remains in the customer cart; it must not be copied into vendor-only screens. `/customer/**` remains customer-role protected.
- Google Maps keys remain split: `GOOGLE_MAPS_BROWSER_API_KEY` is browser-only; `GOOGLE_MAPS_API_KEY` is server-side Routes API only. No actual key is included in this release.

## Required operator action – key rotation
1. In Google Cloud Console, disable/delete the previously exposed key and create replacement keys.
2. Browser key: HTTP-referrer restrict to the exact production host (and localhost only for local development); allow only Maps JavaScript API and Places API (New).
3. Server key: keep only in Render `GOOGLE_MAPS_API_KEY`; restrict to Routes API and server/network constraints where feasible. Never inject it into templates or JavaScript.
4. Set Render `GOOGLE_MAPS_BROWSER_API_KEY` to the new browser key and `GOOGLE_MAPS_API_KEY` to the new server key. Redeploy.
5. Search repository, Git history, old ZIPs, logs and screenshots for the old key. If it was committed, remove it from current files and consider history rewrite after coordinating collaborators.
6. Review Google Cloud API usage and billing alerts. Never send either key in chat.

## Environment variables
- `APP_SEED_PRODUCTS_CREATE=false`
- `APP_SEED_PRODUCTS_DELETE=false`
- `GOOGLE_MAPS_BROWSER_API_KEY=<new restricted browser key>`
- `GOOGLE_MAPS_API_KEY=<new restricted server key>`

## Validation status
ZIP structure can be checked locally. Maven compilation and live Google Maps/Render tests were not run in this environment because Maven is unavailable here. Run `mvn clean test` on the Windows development machine before deployment.
