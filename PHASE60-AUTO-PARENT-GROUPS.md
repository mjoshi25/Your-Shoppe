# Phase 60 – Configurable automatic parent groups

- Added optional `parentGroupKey` metadata to registered dropdown groups.
- State remains a top-level group; Indian Cities automatically use Indian States; Indian PIN Codes automatically use Indian Cities; Emergency Subcategories automatically use Emergency Categories.
- Admin can configure an optional parent group when registering a future group.
- In Add/Edit Dropdown Option, the parent group is derived from the selected group. Admin only selects a parent option; the parent selector is hidden for independent groups.
- Server validates that required parent options exist and are active; parent code is cleared for independent groups.
- Existing option values and history are retained.

## Validation
- Source-level checks and ZIP integrity only. Maven compilation, MongoDB integration, and browser testing have not been run.
