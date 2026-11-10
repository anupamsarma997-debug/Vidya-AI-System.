# Offline machine/part catalogue

MachinePartCatalog provides dependency-free, in-memory text lookup over machine/part records.

## Search behavior

- Case-insensitive substring matching across record ID, machine category/manufacturer/model/variant/year, part ID/name/function, and OEM part number.
- Blank queries return no results.
- Duplicate record IDs are collapsed.
- Incomplete, unreviewed, source-checked-only, and rejected records are excluded. Only structurally complete records marked TECHNICIAN_REVIEWED or MANUFACTURER_CONFIRMED are eligible.
- Search returns reference records only; it does not diagnose faults or generate repair steps.

## Current limitations

This is not yet a JSON asset loader or persistent database. Callers must supply records already parsed into Kotlin objects. The catalogue does not validate source authenticity, legal reuse rights, machine fitment, or technical correctness. Human review remains mandatory, and hazardous repair work must be referred to qualified technicians.

## Extended machine knowledge fields

The JSON loader now maps optional `symptoms`, `error_codes`, and `hazards` arrays into typed Kotlin records. It also loads machine serial ranges, part location descriptions, and compatible variants. Unknown hazard labels and malformed symptom/error-code entries are rejected rather than silently accepted.

Hazards are reference metadata, not an automatic diagnosis. Any non-`NONE` hazard category must remain behind the qualified-technician safety gate. A record's presence in the asset does not make it reviewed; only technician-reviewed or manufacturer-confirmed records can appear in catalogue search.

