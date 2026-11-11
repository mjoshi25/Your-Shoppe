# Phase 10.1 — Navigation and action design refinement

This update is a visual refinement of Phase 10. It does not change business workflows, controllers, repositories, database models, registration, vendor approval, payment, commission, or chat behavior.

## Navigation
- Replaced the crowded flat link row with a structured responsive navigation bar.
- Added a compact ASMJ brand mark and wordmark.
- Grouped account links under the signed-in user's profile menu.
- Grouped vendor links under Vendor workspace.
- Grouped administrator links under Administration.
- Kept Home, Explore products, and Cart as primary navigation links.
- Kept guest Join, Sign in, and My enquiries links.
- Added dropdown hover/focus/expanded styling and mobile wrapping.

## Buttons
- Standardized primary, secondary, danger, and success action styles.
- Added consistent sizing, rounded corners, shadows, hover/active/focus-visible states.
- Styled native buttons used by older forms where they do not already have a `.btn` class.
- Preserved existing `.btn` classes and form actions.

## Compatibility
- No new dependencies or environment variables.
- No backend or MongoDB changes.
- Existing Thymeleaf security role conditions remain on the relevant navigation links.
- Responsive CSS targets desktop, tablet, and mobile layouts.
