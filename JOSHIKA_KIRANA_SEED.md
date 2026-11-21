# Joshika Kirana Store – opt-in seed

This source update adds an opt-in, idempotent seed for `joshikiranastore@asmj.co.in`.

## What it creates
- Vendor account with role `ROLE_VENDOR` and `VendorStatus.PENDING`.
- Vendor profile for **Joshika Kirana Store**. Address/city/state/PIN fields are placeholders and must be completed after approval.
- 15 kirana products across Staples, Snacks, Beverages and Packaged Food.
- Every seeded product is saved with `ProductStatus.PENDING`; products are not published for customers.
- Product image uses the existing product placeholder asset. Replace images after vendor approval if required.

## Safe activation
The seed is disabled by default. It does not change any existing account's role, password or approval state, and does not overwrite existing products.

Set these Render environment variables for one deployment:
- `APP_SEED_KIRANA_CREATE=true`
- `APP_SEED_KIRANA_PASSWORD=12345678`

Deploy once to create the account/profile/products. Then immediately set:
- `APP_SEED_KIRANA_CREATE=false`
- Remove `APP_SEED_KIRANA_PASSWORD`

The password is BCrypt-hashed using the application's configured `PasswordEncoder`; it is not stored in plaintext in MongoDB or hardcoded in the source.

If the email already exists as a non-vendor account, or as a vendor whose status is not `PENDING`, the seeder stops without modifying that account. If the `Grocery & Food` category or required active subcategories are not configured, corresponding products are skipped and a warning is logged. The seeder never modifies category master data.

## Products seeded
1. Sona Masoori Rice 5 kg
2. Whole Wheat Atta 5 kg
3. Toor Dal 1 kg
4. Moong Dal 1 kg
5. Refined Sugar 1 kg
6. Iodised Salt 1 kg
7. Sunflower Oil 1 L
8. Mustard Oil 1 L
9. CTC Tea 250 g
10. Instant Coffee 100 g
11. Glucose Biscuits Family Pack
12. Mixture Namkeen 200 g
13. Instant Noodles 4-Pack
14. Bath Soap 4-Pack
15. Detergent Powder 1 kg

## Build and verification
Run `mvn clean package -DskipTests` before deployment. After deployment, check Admin → Vendors for the pending vendor and Admin → Products for the 15 pending products. Complete the shop address, city, state, PIN code, mobile number and product images through the normal workflow before approval.
