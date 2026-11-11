# ASMJ Marketplace — Phase 9

Standalone multi-vendor marketplace foundation using Java 17, Spring Boot, Thymeleaf, Spring Security and MongoDB.

## Included in Phase 1
- Public home, login and registration pages
- Customer registration
- Vendor application registration (starts in `PENDING`)
- BCrypt password hashing
- Role-based URL security for ADMIN, VENDOR and CUSTOMER
- Role-aware dashboard routing
- Pending vendor landing page
- Initial admin bootstrap through environment variables
- MongoDB-backed users collection with unique email index
- Responsive starter UI

## Prerequisites
- JDK 17
- Maven 3.9+
- MongoDB local instance or MongoDB Atlas connection string

## Configure environment variables (Windows PowerShell)
```powershell
$env:MONGODB_URI="mongodb+srv://<username>:<password>@<cluster>/<database>?retryWrites=true&w=majority"
$env:APP_ADMIN_EMAIL="admin@yourdomain.com"
$env:APP_ADMIN_PASSWORD="Use-A-Strong-Unique-Password"
$env:APP_ADMIN_NAME="ASMJ Marketplace Admin"
```

Do not commit real credentials or place them in `application.properties`.

## Run
```powershell
mvn clean spring-boot:run
```
Open http://localhost:8080

## Initial admin
On first startup, an admin account is created only if both `APP_ADMIN_EMAIL` and `APP_ADMIN_PASSWORD` are supplied and the email does not already exist. The password is BCrypt-hashed. Keep the bootstrap password secret and rotate it after first login when password-management functionality is added.

## Registration behavior
- Customer registration creates an active customer account.
- Vendor registration creates a vendor account with `PENDING` approval status.
- Pending vendors cannot access vendor dashboard endpoints or place products for sale.
- Admin/vendor/customer URL authorization is separated by Spring Security roles.

## Phase 1 scope notes
Vendor approval screens, vendor profile completion, password reset, email verification, rate limiting, audit logging, product catalogue, Cloudinary upload, order processing, payment handling, weekly commission, and support chat are planned for later phases. Do not use this starter as a production launch until the remaining phases and security testing are completed.


## Phase 1 V2 fixes
- Public registration POST endpoints `/register/customer` and `/register/vendor` are explicitly permitted by the security matcher.
- Registration form uses Thymeleaf-generated context-aware action URLs and CSRF token support.
- Vendor registration can sign in to view the pending approval screen; all other `/vendor/**` URLs require an approved vendor record.
- Admin, vendor and customer URL spaces remain role restricted.
- Login and registration pages use a mobile-first responsive layout.


## Phase 2 — Admin dashboard and vendor approval
- Admin vendor overview with counts by status.
- Vendor list with status filter and search by owner name, email or mobile.
- Vendor registration captures a shop/business profile and stores it separately in `vendor_profiles`.
- Admin can approve pending/rejected applications, reject pending applications with a required reason, suspend approved vendors with a required reason, and reactivate suspended vendors.
- Each review action is recorded in `vendor_review_events` with previous/new status, admin email, reason and timestamp.
- Existing vendor accounts created before Phase 2 remain visible; if they have no profile, the admin detail screen indicates that.
- Selling URLs continue to be restricted by the live vendor status. A vendor must be APPROVED to enter `/vendor/**` except the pending-status page.

### New MongoDB collections
- `vendor_profiles`
- `vendor_review_events`

### Phase 2 test
```powershell
mvn clean test
```

## Phase 2 navigation and UI corrections
- Shared navigation is role-aware: guests see Join and Sign in; authenticated users see their dashboard and role-appropriate shortcuts plus Sign out.
- Authenticated users who manually open `/login` or `/register` are redirected to `/dashboard`.
- Landing page redesigned with responsive hero, local-marketplace positioning, feature cards and role-aware calls to action.
- Admin, vendor, customer, catalogue and form screens share refreshed responsive styling.

## Phase 3 — Product catalogue and Cloudinary images
- Approved vendors can create product listings with name, description, INR price, stock quantity and an image.
- Product images are uploaded to Cloudinary; only JPG, PNG and WEBP up to 5 MB are accepted.
- New products default to `PENDING` approval. Admins can review listings at `/admin/products` and approve or reject them.
- Public catalogue at `/products` displays only `ACTIVE` products.
- Vendor product management is available at `/vendor/products`.
- Set `APP_PRODUCTS_REQUIRE_APPROVAL=false` only if you intentionally want new products published immediately.

### Cloudinary setup
Create or use a Cloudinary account and copy its Cloud Name, API Key and API Secret from the Cloudinary dashboard. Configure these environment variables locally and in Render:
- `CLOUDINARY_CLOUD_NAME`
- `CLOUDINARY_API_KEY`
- `CLOUDINARY_API_SECRET`

Never commit real Cloudinary credentials to Git or put them in `application.properties`. Product images are uploaded to the `asmj-marketplace/products` folder in Cloudinary. The application stores the returned secure image URL and public ID in MongoDB.

### Render environment variables
Keep your existing MongoDB and admin variables, and add all three Cloudinary variables above. Keep `APP_PRODUCTS_REQUIRE_APPROVAL=true` for admin moderation.

### Phase 3 test checklist
1. Sign in as an approved vendor.
2. Open My products and add a product with a valid image.
3. Confirm the image appears in Cloudinary under `asmj-marketplace/products`.
4. Confirm the product appears as PENDING in the vendor list and admin product moderation page.
5. Approve it as admin and confirm it appears in `/products`.
6. Reject another test product and confirm it is not visible in the public catalogue.
7. Sign in as a customer and verify that the catalogue is accessible and vendor/admin URLs remain blocked.

### Build and run
```powershell
mvn clean test
mvn spring-boot:run
```

**Important:** This release has not been verified against your live MongoDB Atlas or Cloudinary account from this environment. Run the Maven tests locally and complete the Cloudinary upload test before deploying. Cloudinary is integrated in the code, but actual uploads require the three environment variables.


## Phase 4 — Product review, categories and fulfilment
- Admin product moderation displays the Cloudinary product image, vendor, category, subcategory, price, stock and fulfilment beside approve/reject actions.
- Category and subcategory are mandatory for new product submissions. Active categories are stored in MongoDB `categories`; choosing a category loads only its subcategories through `/api/categories/{id}/subcategories` and clears any previous selection.
- On first startup, a starter category set is seeded only when the `categories` collection is empty. Existing category data is not overwritten.
- Vendors can enable Home Delivery, Pickup from Shop, or both under Vendor Centre → Pickup & Delivery Settings. At least one option must remain enabled.
- Fulfilment settings apply to existing and new products. Customers see the available fulfilment choices on public product cards.
- Delivery charge calculation, delivery PIN-code/service-area restrictions, checkout and order-level fulfilment selection are reserved for the order/delivery phase; this release configures and displays vendor-supported methods.

### Phase 4 validation
```powershell
mvn clean test
mvn spring-boot:run
```
Test category → subcategory dependency (including changing category after selecting a subcategory), product image visibility during admin review, and both fulfilment options. Products created before this phase may show no category/subcategory until re-created or edited in a later product-edit enhancement.

## Phase 5 — Category administration and product enquiry chat

This release adds:
- Admin category and subcategory create, rename, activate/deactivate, and delete actions.
- Category/subcategory renames cascade to products already assigned to those values.
- Deletion is blocked when existing products reference the category/subcategory; deactivate instead, or reassign products before deleting.
- Inactive categories/subcategories are hidden from the public catalogue filters and vendor product-entry dropdowns. Existing product listings remain intact.
- Public product search by name/description, category and subcategory.
- Buyer-to-vendor product conversations for both registered customers and guest buyers. Guests provide a name and mobile number; a private session key protects their enquiry history in that browser session.
- Vendor enquiry inbox, registered buyer enquiry inbox, guest enquiry inbox, and admin-wide enquiry view. Admins can join a conversation for support.
- Conversation messages are stored in MongoDB with sender type/name and timestamps. No external chat service or WebSocket infrastructure is required for this first version; messages appear on refresh/navigation.

### Phase 5 test checklist
1. Sign in as admin. Create a category and multiple subcategories; rename both and verify linked product labels update.
2. Deactivate a category/subcategory and verify it no longer appears in public filters or vendor product creation. Reactivate it and verify it returns.
3. Attempt to delete a category/subcategory used by a product. Confirm deletion is blocked and existing products remain visible. Delete an unused value and confirm it is removed.
4. As a guest, search by category/subcategory, open a product card, enter name/mobile and send an enquiry. Confirm the conversation appears in the vendor inbox and in **My enquiries** in the same browser session.
5. As the vendor, reply. Confirm the guest can see the reply and send another message.
6. Repeat as a registered customer. Confirm **My enquiries** lists the conversation.
7. Sign in as admin and verify **All product enquiries** shows conversations and admin can reply.
8. Verify users cannot open another buyer's conversation by changing the conversation ID in the URL. Guest conversations should only open in the browser session that created them.
9. Test all screens at desktop and mobile widths.

### Notes
- Guest enquiry history is protected by a server-side HTTP session. Clearing browser cookies, using another browser/device, or session expiry will end guest access to that history; the vendor and admin retain their stored conversation record.
- Guest mobile numbers are contact details supplied by the guest and are not OTP-verified in this phase.
- This phase uses request/response chat (send then refresh), not live WebSocket messaging. Real-time delivery, unread counts and attachment support can be considered in a later phase.


## Phase 6 — Cart, checkout and order management

This phase introduces the first transactional shopping flow:
- Customer cart stored in the HTTP session; quantity can be updated or removed.
- Checkout requires a signed-in customer. Guest buyers can continue product enquiries, but must register/sign in before placing an order.
- A cart is restricted to one vendor at a time. This keeps vendor fulfilment, delivery charges and order ownership unambiguous.
- Home delivery or shop pickup is selected at checkout according to vendor settings. Vendors configure a flat delivery charge per order and optional UPI ID in Pickup & Delivery Settings. Pickup has no delivery charge.
- Payment methods: COD / pay at pickup, or UPI with transaction reference. UPI orders remain pending until an admin verifies or rejects the reference. This phase does not integrate an automatic payment gateway or upload UPI screenshots.
- Customer order history and cancellation while order is still PLACED.
- Vendor order inbox with status progression; admin-wide order view and manual UPI verification.
- Product stock is checked at checkout and reduced when the order is saved.

### Phase 6 test checklist
1. Sign in as a customer and add one or more products from the same vendor to the cart. Update quantities and clear the cart.
2. Try adding a product from a different vendor; confirm the one-vendor cart guard is shown.
3. Configure a vendor delivery charge and UPI ID under Vendor Centre → Pickup & Delivery Settings.
4. Checkout with home delivery and verify address validation and delivery charge.
5. Checkout with pickup and verify no delivery charge is applied.
6. Place a COD order and verify it appears in customer orders and vendor orders.
7. Place a UPI order with a transaction reference; verify admin can mark it VERIFIED or REJECTED.
8. Update order status as vendor and verify customer sees the updated status. Try to access another vendor's order URL and confirm access is denied.
9. Cancel an order while PLACED; confirm later-stage orders cannot be cancelled by the customer.
10. Confirm stock decreases after a successful order and insufficient stock blocks checkout.
11. Test all new screens on mobile and desktop.

### Phase 6 limitations / next enhancements
- Cart is session-based; it is not synchronized across devices and clears when the session expires.
- One vendor per cart/order; multi-vendor checkout and split orders are not included.
- UPI payment is manually verified from the transaction reference; payment screenshot upload and automated payment gateway integration are not included.
- Vendor delivery charge is a flat per-order amount. PIN-code serviceability, distance-based pricing, refunds, commission ledger and Monday–Sunday commission settlement are future phases.
- Inventory is validated and decremented in application code; high-concurrency atomic stock reservation and payment/refund reconciliation need additional hardening before high-volume production.

## Phase 6 corrective release — templates and product editing

This corrective release explicitly packages `src/main/resources` and sets the Thymeleaf template prefix/suffix. A regression test checks that every affected view template is available on the test classpath. The source ZIP includes all templates referenced by the affected controllers, including `chat/inbox.html`, `vendor/orders.html`, `order/cart.html`, and `admin/categories.html`.

If an IDE still reports `Error resolving template`, open the extracted project folder that directly contains `pom.xml`, stop the old running process, run `mvn clean test`, and then `mvn spring-boot:run`. Do not run the app from a parent folder or copy only `src/main/java`; `src/main/resources/templates` must be part of the project and copied into `target/classes/templates`. To inspect the packaged JAR, use `jar tf target/asmj-marketplace-0.0.1-SNAPSHOT.jar | findstr templates` in Windows PowerShell/CMD.

Vendor product management now includes Edit and Delete actions. Edits preserve the current image unless a replacement is uploaded, validate active category/subcategory selection, and return approved products to PENDING for review when approval is enabled. Products referenced by order history are archived instead of permanently deleted; products without order history can be deleted.

## Phase 7 — Weekly vendor commission and settlement

- Admin can configure a marketplace commission percentage (default 5.00%). The setting is stored in MongoDB and survives application restarts.
- Commission is recorded once per completed order only after payment is verified. In this Phase 8 release, COD requires the vendor to upload a cash receipt after completion; UPI requires buyer UTR/screenshot evidence and admin verification, including if verification happens after completion.
- Commission is calculated on the product subtotal (delivery charge excluded) using the rate captured for that order.
- Commission period follows Monday through Sunday in India Standard Time. The ledger uses the order's completion date, not a later UPI verification date.
- Admin can view commission ledger entries and mark individual entries as paid, with payment timestamp and admin identity recorded.
- Vendors can view their own commission statement. Unpaid entries become overdue after their Sunday week closes; a vendor with an overdue unpaid entry cannot place/receive new orders through checkout. Existing order management and enquiry access remain available.
- This is a manual settlement workflow; no bank transfer or automatic deduction is performed.

### Phase 7 test checklist
1. Set commission rate to 5% in Admin → Commissions and restart the app; confirm the rate remains saved.
2. Complete a COD order, then upload the vendor cash receipt. Confirm one commission entry is created after receipt upload.
3. Place a UPI order with UTR and screenshot. Confirm no ledger entry is created until the order is completed and admin verifies payment.
4. Verify the UPI payment after order completion. Confirm the commission entry is recorded once, using the order completion week.
5. Verify commission amount equals product subtotal × captured commission rate; delivery charge is excluded.
6. Confirm vendor can see only their own commission entries.
7. Mark a commission entry paid as admin and confirm the paid timestamp/status appears for vendor.
8. For overdue testing, use a test ledger entry with a prior week end date and UNPAID status; confirm checkout for that vendor is blocked, while existing vendor orders and enquiries remain accessible. Mark all overdue entries paid and confirm checkout is available again.
9. Confirm two calls or repeated status updates do not create duplicate commission entries for the same order.
10. Test product edit, image preservation/replacement, deletion without order history, and archive behavior for products referenced by orders.
11. Run `mvn clean test` and `mvn spring-boot:run`; verify the Thymeleaf template resource regression test passes.

### Important build and deployment note
Run commands from the extracted `asmj-marketplace-phase8` folder (the folder containing `pom.xml`). This environment does not have Maven installed, so the full Maven build and integration tests still need to be run locally before Render deployment. Do not deploy until `mvn clean test` passes on your machine.

## Phase 8 — Payment evidence, chat attachments and Render deployment

### Customer order payments
- COD: vendor uploads a cash receipt image/PDF after marking the order completed. The order payment is then recorded as verified and eligible commission is created.
- UPI: buyer must provide the UTR/reference and upload a screenshot/PDF during checkout. Admin reviews the evidence from Marketplace Orders. Commission is created only after the order is completed and UPI is verified.

### Vendor commission settlement
- Vendor submits either UPI (UTR + screenshot/PDF) or declares cash payment to admin.
- UPI evidence is reviewed by admin. For cash, admin uploads the receipt issued/recorded on receiving cash before marking the commission paid.
- An unpaid commission remains outstanding and continues to restrict new orders until admin verification.

### Chat attachments
- Buyer enquiries and every chat reply support one attachment per message.
- Allowed formats: JPG, PNG, WEBP, PDF, TXT, DOC and DOCX; maximum 8 MB per attachment.
- Attachments are stored in Cloudinary. Configure Cloudinary environment variables before using uploads.

### Render deployment
This release includes a Dockerfile and Render Blueprint (`render.yaml`). Push the project to a Git repository and create a new Render Blueprint from that repository, or create a Docker web service using the included Dockerfile. Set these environment variables in Render:

- `MONGODB_URI` — MongoDB Atlas connection string; allow Render outbound access in Atlas Network Access (prefer the narrowest supported access policy).
- `APP_ADMIN_EMAIL`, `APP_ADMIN_PASSWORD` — bootstrap admin credentials. Use a strong unique password and do not commit it.
- `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` — Cloudinary credentials.
- `APP_PRODUCTS_REQUIRE_APPROVAL=true` — recommended for production.

Render supplies `PORT`; the Docker entrypoint binds Spring Boot to it. Do not add secrets to `render.yaml` or commit `.env` files. After first deployment, sign in with the bootstrap admin account, change the password if your account workflow supports it, and test uploads, payment review and chat from the deployed HTTPS URL.

### Phase 8 testing checklist
1. Place a COD order, complete it as vendor, upload cash receipt and confirm the order shows verified payment.
2. Place a UPI order with UTR and screenshot; confirm admin can open the evidence and verify/reject it.
3. Confirm a completed UPI order creates commission only after admin verification.
4. Submit commission by UPI and verify from admin; submit another by cash and ensure admin must attach a cash receipt.
5. Confirm unpaid commissions still block new orders until verified.
6. Send an enquiry with an attachment and send attachments in buyer/vendor/admin chat replies.
7. Test uploads using mobile browser and confirm invalid file types / files over 8 MB are rejected.

## Phase 9 — Live chat refresh, personalized accounts and profile settings

- Product conversations now expose an access-controlled JSON message endpoint. The conversation page polls it every four seconds while visible, so new buyer/vendor/admin replies appear without manually refreshing the page. Polling pauses when the browser tab is hidden and resumes when visible. The existing POST message workflow and Cloudinary attachment handling are retained.
- Dashboard greetings use the signed-in account's actual name. The shared navigation displays the account name and profile photo (or an initial avatar) and provides a Settings link for every authenticated role.
- `/settings` provides profile photo upload/removal, name and mobile updates, notification preference, and password changes for customers, vendors and admins. Password changes require the current password and enforce an 8–72 character new password.
- Customer settings include a saved delivery address. Vendor settings include business name, description and address; fulfilment and delivery-charge controls remain in the existing Vendor Centre settings page. Admins receive personal profile and security settings.
- Profile photos accept JPG, PNG and WEBP up to 5 MB and are stored in Cloudinary under `asmj-marketplace/profile-photos`. Existing accounts remain compatible; missing profile images use an initial avatar.
- Chat polling is lightweight request/response polling, not WebSocket/STOMP. It requires no Redis or additional Render service. A four-second polling interval is used while the conversation tab is active.

### Phase 9 local verification
```powershell
mvn clean test
mvn spring-boot:run
```

Test as customer, approved vendor and admin: personalized dashboard greeting, profile photo upload and removal, profile fields, password update (including wrong-current-password rejection), and access control. Open the same conversation in two browser sessions (buyer and vendor), send a reply in one, and verify it appears in the other within approximately four seconds. Repeat with an attachment. After local tests pass, deploy to Render and complete the mobile checks on actual devices.

### Render configuration
No new infrastructure or environment variables are required beyond the existing MongoDB Atlas, admin bootstrap and Cloudinary settings. Ensure `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY` and `CLOUDINARY_API_SECRET` are configured in the Render service. Keep secrets in Render environment settings, not in source control.


## Registration reliability hotfix (Phase 9.1)
- Registration form now includes an explicit Spring Security CSRF token. This is important because customer and vendor submit buttons post to separate `formaction` endpoints and the form itself has no fixed `th:action`.
- Validation messages are shown beside full name, email and password fields instead of silently returning the registration page.
- MongoDB duplicate-key and database failures are handled with clear user-facing messages and server-side error logging; the application no longer redirects to login unless the account save returns a persisted ID.
- Vendor registration removes the newly-created user record if the required vendor profile cannot be persisted, avoiding an orphaned vendor login.
- Added regression tests for CSRF form markup, confirmed persistence IDs, database write failure and vendor-profile cleanup.

### Registration troubleshooting
Set `MONGODB_URI` in the runtime environment to an Atlas connection string that includes the intended database name in its path, for example `/asmj_marketplace`. Ensure the Atlas database user has read/write access to that database and the deployment host is allowed by Atlas Network Access. After registration, inspect the `users` collection in that exact database. Vendor registrations also create a matching document in `vendor_profiles`.


## Phase 10 — Vendor onboarding chat and button refresh
See `PHASE10-CHANGELOG.md` for the vendor-admin pre-approval conversation, document attachment flow, security rules and test checklist. No new environment variables are required.
