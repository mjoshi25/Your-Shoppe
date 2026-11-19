# Hybrid commission update

- Replaces the single percentage setting on the product-order commission screen with a fixed amount plus order-subtotal percentage slabs.
- Admin can add/remove slab rows. Slabs must start at ₹0, be continuous in paise, and the last slab must have a blank maximum.
- Commission formula: fixed amount per eligible completed order + (eligible order subtotal × selected slab percentage).
- Commission ledger snapshots fixed amount, slab minimum/maximum, percentage component, and final commission amount. Existing ledger entries are not recalculated.
- Legacy `ratePercent` settings remain readable and are migrated in memory to a single open-ended slab until Admin saves the new configuration.
- This update changes the existing product-order commission path only. Rental commission and service commission settings remain separate and are not changed.
- Maven tests could not be run in this environment because Maven is unavailable. Run `mvn clean test` locally before deployment.
