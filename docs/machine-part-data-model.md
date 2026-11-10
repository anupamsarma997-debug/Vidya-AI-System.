# Machine and part knowledge records

Vidya records must identify the exact machine scope and preserve source provenance. The JSON Schema at [machine-part-record.schema.json](schemas/machine-part-record.schema.json) is the initial data contract.

## Required before a record can be used
- Manufacturer, model, and any known variant/year/serial range.
- Part identifier, name, and function, with fitment kept separate from visual similarity.
- At least one source with title, identifier/URL, and license.
- Verification state and known limitations.
- Applicable hazards and exact scope for error-code meanings.

## Verification states
- `unreviewed`: imported or drafted; never use as repair advice.
- `source_checked`: source was inspected, but not independently technician-reviewed.
- `technician_reviewed`: reviewed by a qualified technician for the stated machine scope.
- `manufacturer_confirmed`: confirmed against manufacturer documentation or support.
- `rejected`: known incorrect, incompatible, unsafe, or unsupported.

## Data safety rules
1. Never infer compatibility from appearance alone.
2. Never generalize an error code across manufacturers or models.
3. Keep unknown values null or explicitly unknown; do not fill gaps with guesses.
4. Do not ingest copyrighted manuals or datasets unless their license/permission allows the intended use.
5. A source citation does not itself prove a repair procedure is safe.
6. High-voltage, gas/fuel, pressure, suspended-load, moving-machine, chemical, and unknown hazards must stop automated repair instructions and direct the user to qualified help.

No real machine records are included yet. This schema is a template, not a populated or verified parts database.
