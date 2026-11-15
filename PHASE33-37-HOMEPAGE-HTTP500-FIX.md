# Phases 33–37 — Homepage HTTP 500 fix

## Root cause
`CurrentUserAdvice` previously populated `adsByPlacement` only for placements with at least one live campaign. Thymeleaf templates accessed map entries using property syntax (for example, `adsByPlacement.HOME_HERO`). When a placement had no live campaigns, the key was absent and SpEL raised `EL1008E` while rendering the homepage.

## Fix
- Initialize all six supported advertising placement keys with empty lists on every request.
- Change ad-slot template expressions to explicit map-key lookup syntax.
- Add a user-friendly `templates/error.html` so a future application error is not obscured by a missing error template.

No advertising records, payment state, campaign approval state, or production data are modified by this fix.
