# JSON asset loader

The Android asset repository reads `machine_parts.json` from the APK assets directory. The parser maps the documented snake_case fields into the Kotlin domain model, enforces JSON field types (including integer model years and boolean attribution flags), rejects unknown verification statuses or missing required structures, and the repository rejects duplicate record IDs.

## Add data safely

1. Obtain a machine/part record from an identifiable source.
2. Record the exact source identifier and license/permission terms.
3. Have the record checked by a qualified technician or manufacturer before marking it reviewed.
4. Validate the JSON against `data/schemas/machine-part-record.schema.json`.
5. Add it to the asset catalogue and run the unit tests.

The starter asset is an empty JSON array by design. No invented example has been presented as real machine data.

## Limitations

The loader performs mapping and basic structural validation only. It does not implement full JSON Schema validation, verify a URL, authenticate a manual, prove license rights, or verify machine fitment. The app must not treat parsed data as safe repair instructions.
