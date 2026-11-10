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

## Initial source-backed dataset shortlist (not acquired)

These entries are leads for evaluation, not downloaded assets or approved training data:

| Dataset | Potential use in Vidya | Known limitation / license gate |
|---|---|---|
| [Fraunhofer MVIP](https://fordatis.fraunhofer.de/handle/fordatis/358) | Industrial component recognition; multi-view RGB-D and object context | Repository says Creative Commons but the exact license variant and commercial permission must be confirmed. Its split archive is extremely large (25 parts, mostly about 4.88 GB each); not a phone-first download. |
| [MVTec AD](https://www.mvtec.com/research-teaching/datasets/mvtec-ad) | Detect/localize visual anomalies on industrial objects and textures | CC BY-NC-SA 4.0; non-commercial only. Do not bundle or use for commercial Vidya without separate permission. |
| [KolektorSDD2](https://www.vicos.si/resources/kolektorsdd2/) | Surface-defect detection experiments | CC BY-NC-SA 4.0; non-commercial only; does not teach machine identity or repair procedures. |
| [KolektorSDD](https://www.vicos.si/resources/kolektorsdd/) | Surface-defect segmentation experiments | CC BY-NC-SA 4.0; non-commercial only; small, narrow industrial inspection dataset. |

### Acquisition decision

1. Prioritize MVIP for a future part-recognition research baseline, but first confirm the exact license, dataset terms, storage requirement, and redistribution rules with Fraunhofer.
2. Keep MVTec and Kolektor datasets in a separate non-commercial research track unless written commercial permission is obtained.
3. Do not merge surface-defect labels into component-identity labels. A scratch mask is not a part name, fault diagnosis, or repair instruction.
4. No archive has been downloaded or extracted as part of this shortlist. CSV status remains `identified` or `license_review` until acquisition evidence is recorded.
