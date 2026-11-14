# Phases 33–37 hotfix — empty advertisement placements

## Reported issue
The homepage failed to render when no live advertisement existed for a placement:
`Exception evaluating SpringEL expression: "adsByPlacement.HOME_HERO"`.

## Root cause
`CurrentUserAdvice` populated `adsByPlacement` only for placements that had eligible live campaigns. When a placement had no eligible campaign, the map did not contain that key. Thymeleaf/SpringEL dot-property lookup on the absent map entry raised an exception before the fragment could apply its null/empty guard.

## Fix
- Initialize all six known placement keys with empty lists in `CurrentUserAdvice`.
- Change template expressions to explicit map-key access (`adsByPlacement['HOME_HERO']`, etc.).
- The existing `advertising/slots` fragment already suppresses null or empty lists, so no placeholder is rendered when no campaign is active.

## Files changed
- `src/main/java/com/asmj/marketplace/controller/CurrentUserAdvice.java`
- `src/main/resources/templates/home.html`
- `src/main/resources/templates/product/detail.html`
- `src/main/resources/templates/product/catalog.html`
- `src/main/resources/templates/order/cart.html`

## Scope
No advertising approval, payment, pricing, targeting, order, commission, checkout, or database business logic was changed. No production deployment was performed.

## Validation
ZIP integrity and static template checks are performed for this hotfix. A full Maven build and live Thymeleaf runtime test must still be run in the user's environment.
