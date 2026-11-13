# ASMJ Shoppe – Phase 24

## Vendor order workflow
- Replaced the generic status dropdown with guided, state-specific actions.
- Vendor cards explain what the current order stage means and what to do next.
- Actions are labelled in plain language: Accept order, Start processing, Mark ready for pickup, Dispatch for delivery, and Confirm handover & complete.
- Only the valid next action for the current status and fulfilment method is displayed.
- UPI orders cannot expose the Start processing action until the payment is fully verified; the card explains the required payment step.
- Cancellation and completion actions include confirmation prompts.
- Vendor status badges use shorter, human-readable labels.
- Existing server-side transition validation and stock/payment controls remain in place.

## Controlled sample products for shop1@asmj.co.in
- Added five sample products across existing active categories/subcategories.
- Every sample listing is created with `PENDING` status so an admin must approve it before customers can see or buy it.
- Sample products use a local neutral placeholder image and realistic test prices/stock values.
- Each seeded record carries a dedicated test marker, batch ID and stable seed key.
- Seeding is idempotent: restarting the application will not create duplicate copies.
- Cleanup targets only this seed batch for the named vendor. Products referenced by existing orders are archived rather than hard-deleted to preserve order history.

## Application properties
Defaults are disabled:

```properties
app.seed.products.create=${APP_SEED_PRODUCTS_CREATE:false}
app.seed.products.delete=${APP_SEED_PRODUCTS_DELETE:false}
```

### Create the sample products
Set the following Render environment variables and redeploy/restart:

```text
APP_SEED_PRODUCTS_CREATE=true
APP_SEED_PRODUCTS_DELETE=false
```

After startup, verify the five listings under the vendor account and the admin Pending Products queue. Then set both variables to `false` and restart/redeploy to prevent further seeding.

### Delete the sample products later
When finished testing, set:

```text
APP_SEED_PRODUCTS_CREATE=false
APP_SEED_PRODUCTS_DELETE=true
```

Restart/redeploy once. The cleanup removes only the explicitly marked sample products belonging to `shop1@asmj.co.in`. Any seeded product already referenced by an order is archived instead of deleted.

**Safety:** If both flags are accidentally set to `true`, deletion takes precedence and creation is skipped. Both flags default to `false`; do not leave either enabled after the one-time operation.

## Validation note
The source and ZIP were statically inspected, but Maven is not installed in the build container and this archive does not include a Maven wrapper. A Maven compile/test run and live MongoDB/Render test have therefore not been claimed. Run `mvn test` (or `mvn -DskipTests package`) in the project environment before deployment.
