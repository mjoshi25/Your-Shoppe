# Phase 73 — Master Data CSV Import / Export

## Included
- Admin-only CSV export for all master-data groups or one selected group.
- Downloadable CSV template with the required six columns: `groupKey,code,label,parentCode,active,sortOrder`.
- CSV upload preview with row-level validation and insert/update counts.
- Explicit confirmation before writes; confirmation revalidates the staged CSV against current master data.
- Upsert by the existing unique `groupKey + code` identity. Existing Mongo IDs are preserved on update.
- Parent-child validation and parent-first save ordering.
- Options omitted from the CSV are left untouched; no automatic deletion/deactivation.
- Import history stores filename, admin, timestamp, total rows, inserts and updates.
- Upload limit: 2 MB and 5,000 data rows.

## Admin workflow
1. Open Admin → Master Data.
2. Use Download template or export an existing environment's data.
3. Prepare the CSV for the target environment.
4. Upload and review the preview; correct all reported errors and upload again if needed.
5. Select Confirm and import only after reviewing the changes.

## CSV rules
- UTF-8; exact header order is required.
- `active` accepts `true/false`, `1/0`, or `yes/no`.
- `sortOrder` must be an integer.
- Group keys must already be registered and active in the target environment.
- Parent group options must exist and be active either in the target database or in the same CSV.
- The file may contain parent and child options in any order; import saves parents first.
- Duplicate groupKey/code pairs in one file are rejected.

## Notes
- This feature transfers option records, not dropdown group definitions. Register required groups in the target environment first.
- Export files can contain operational configuration. Store and transfer them securely.
- No Maven build, automated test suite, browser test, or Render deployment was performed for this package.
