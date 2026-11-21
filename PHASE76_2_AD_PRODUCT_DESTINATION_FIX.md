# Phase 76.2 – Advertisement product destination consistency

## Fixes
- A selected promoted product is now authoritative in vendor campaign edits, rejected-creative resubmissions, and admin campaign edits. The destination URL is rebuilt from that exact product ID rather than allowing a stale URL field to override it.
- The rejected-creative resubmission product dropdown now preselects the campaign's currently associated product. This prevents the browser from silently selecting the first product in the list (which could be an unrelated product).
- Advertisement click handling resolves the destination from the campaign's stored product ID and verifies that the product belongs to the campaign vendor. It no longer trusts a stale destination URL when a product is associated.
- Banner image changes remain independent of the promoted product association.

## Verification
- Source-level checks and ZIP integrity check performed.
- Maven packaging was attempted but Maven is not installed in this execution environment; compile/runtime/browser tests remain outstanding.
