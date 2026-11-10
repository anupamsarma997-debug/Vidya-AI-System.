# Offline machine-part record validation

The Android domain layer now includes `MachinePartRecord`, `MachineIdentity`, `PartIdentity`, `KnowledgeSource`, and `MachinePartRecordValidator`.

## What the validator checks
- Non-empty record, machine, and part identifiers/descriptions.
- Plausible model-year range when a year is supplied.
- At least one traceable source with a declared license field.
- A record is eligible to be displayed as a *reviewed reference* only when it is structurally complete and marked `TECHNICIAN_REVIEWED` or `MANUFACTURER_CONFIRMED`.

## Important limits
This is structural validation only. It cannot prove that a part fits a machine, that a manual is authentic, that a license permits redistribution, or that a repair procedure is safe. It does not parse JSON or load records from device storage yet. Unreviewed records must not be presented as verified facts or repair instructions.
