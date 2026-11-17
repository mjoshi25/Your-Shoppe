# Phase 47.1 – Service Category Card Filtering Fix

## Problem
All homepage service category tiles linked to `/services` without a category parameter. The catalogue therefore showed the same unfiltered approved service list regardless of whether the user clicked Home repair, Healthcare, Beauty & salon, Education, or Events.

## Changes
- Homepage category tiles now pass the corresponding top-level category name.
- `/services` resolves that active top-level category and gathers its active subcategory IDs.
- Catalogue results are filtered to listings assigned to the selected parent or one of its active subcategories.
- Existing exact category-ID filtering from the catalogue dropdown remains supported.
- Provider approval and PIN-code filtering remain in place.

## Files changed
- `src/main/resources/templates/home.html`
- `src/main/java/com/asmj/marketplace/services/ServiceMarketplaceController.java`
- `src/main/java/com/asmj/marketplace/services/ServiceCategoryRepository.java`
- `src/main/java/com/asmj/marketplace/services/ServiceListingRepository.java`

## Validation
- Source-level checks and ZIP integrity performed.
- Maven build not run in this environment; run `mvn clean package -DskipTests` before deployment.
