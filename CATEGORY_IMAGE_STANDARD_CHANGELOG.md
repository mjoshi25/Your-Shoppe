# ASMJ Shoppe – Category Dropdown and Image Upload Standard

## Scope
Applied to marketplace extension listings (Events, Courses, Wholesale, Local Delivery, Freelance) and community listings (Business Directory and Classifieds).

## Changes
- Added MongoDB-backed `marketplace_module_categories` collection scoped by module type.
- Added admin configuration at `/admin/marketplace-categories` to create module categories, add/remove subcategories, and activate/deactivate categories.
- Added dependent category/subcategory dropdowns to listing forms. Subcategories refresh when the parent category changes.
- Added server-side validation so submitted category and subcategory values must belong to an active category for that module.
- Replaced listing image URL text fields with JPG/PNG/WEBP file upload controls, client-side preview, 5 MB validation and upload progress.
- Added Cloudinary upload folders scoped by module and persisted secure image URLs to MongoDB.
- Kept existing listing image URL data readable for older listings.
- Added subcategory labels to the relevant listing cards.

## Admin setup
1. Sign in with an administrator account.
2. Open **Admin → Marketplace categories** at `/admin/marketplace-categories`.
3. Select a module (Wholesale, Business Directory, Classifieds, etc.).
4. Add categories and their subcategories.
5. Activate categories that should appear on public forms.

## Configuration
Cloudinary credentials remain the existing application settings:
- `CLOUDINARY_CLOUD_NAME`
- `CLOUDINARY_API_KEY`
- `CLOUDINARY_API_SECRET`

Uploads accept JPG, PNG and WEBP up to 5 MB.

## Validation status
Static source checks and ZIP integrity are performed for this package. Maven compilation, Spring startup, MongoDB/Cloudinary integration and browser end-to-end tests must still be run in the user's configured environment.
