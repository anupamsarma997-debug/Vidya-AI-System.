# Dataset acquisition and governance

**Status:** this document does not confirm that any dataset has been acquired.

## Before downloading
Verify the original publisher, canonical source, exact version, license, commercial-use permissions, scope/classes, annotation quality, duplicates, file size, and checksum where available. A public download link does not automatically mean public-domain data.

## Register
Use the CSV register in this folder. Record one row per dataset version. Status values: identified, license_review, downloaded, verified, rejected.

A dataset is downloaded only when the archive is actually present. It is verified only after extraction/integrity checks and sample inspection. Record SHA-256 and private storage location in a working log; do not commit large archives.

## Quality checks
- Inspect class counts and labels.
- Inspect samples from every class.
- Detect exact and near duplicates before splitting.
- Split by source, machine, or session where possible to reduce leakage.
- Keep test data separate from training and tuning.
- Document missing classes and out-of-distribution cases.
- Do not augment the test set to inflate metrics.

## Evaluation
Report per-class precision, recall, F1, confusion matrix, failure examples, test-set size, and collection method. Include an unknown/abstain path. Never report accuracy alone without class balance and evaluation context.

Remove personal information where appropriate. Do not include proprietary manuals or copyrighted images unless permission covers the intended use.
