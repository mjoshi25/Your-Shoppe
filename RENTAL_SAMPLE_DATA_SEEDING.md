# ASMJ Shoppe — Rental sample data seeding

This release adds an opt-in, repeatable seeder for two demo vendor accounts and 25 driver-operated rental vehicle listings.

## Seeded accounts

- `shop2@asmj.co.in` — ASMJ Rental Demo Shop 2
- `shop3@asmj.co.in` — ASMJ Rental Demo Shop 3

New accounts are created as enabled vendor accounts with vendor status `APPROVED` so they can access the vendor area during testing. A password is never hardcoded: set `APP_SEED_RENTAL_VENDOR_PASSWORD` to a temporary password before enabling creation. If an email already exists, it is reused only if its role is already `ROLE_VENDOR`; the seeder does not overwrite its password or profile.

## Sample listings

Creates five records for each of the five seeded top-level rental categories (25 total): Two-wheelers, Three-wheelers, Cars, Passenger transport, and Commercial vehicles. Records are distributed between both vendors, assigned to active subcategories, marked as seeded demo data, and saved with status `PENDING`. All descriptions explicitly identify the records as sample test listings. Demo driver/licence/insurance values are placeholders, not real verification documents. Do not approve these records for customer use.

## Enable creation

Set these environment variables for one controlled startup:

```properties
APP_SEED_RENTALS_CREATE=true
APP_SEED_RENTAL_VENDOR_PASSWORD=<temporary-unique-password>
APP_SEED_RENTALS_DELETE=false
```

The seeder is idempotent by seed key. Re-running it does not duplicate existing sample vehicles. It expects the five rental categories to have been seeded and active.

## Disable seeded vehicles

To disable only this seeder's records, set:

```properties
APP_SEED_RENTALS_CREATE=false
APP_SEED_RENTALS_DELETE=true
```

Cleanup changes the seeded vehicle status to `SUSPENDED`, preserving vehicle records, vendor accounts, and any booking history. It never deletes real vendor accounts or unmarked rental vehicles. Turn both flags off after cleanup.

## Important

Keep both seed flags `false` in normal production operation. The sample accounts are demo accounts, not real businesses. Change their temporary password and replace demo contact/profile details before any real-world use. Seeded vehicle listings must remain pending until replaced with authentic vehicle, driver, licence, and insurance information and reviewed by an administrator.
