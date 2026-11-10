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

## Additional candidate: VisA (commercially usable subject to CC BY 4.0 compliance)

The [AWS Open Data Registry entry for VisA](https://registry.opendata.aws/visa/) states CC BY 4.0 and lists a public S3 archive, `s3://amazon-visual-anomaly/VisA_20220922.tar`, in `us-west-2`. The registry describes 10,821 images across 12 object classes (9,621 normal and 1,200 anomalous), with image- and pixel-level annotations. This is the strongest currently identified candidate for a commercial research baseline, provided Vidya preserves attribution, license notice, and source citation and reviews the upstream documentation before redistribution.

**Task boundary:** VisA is an industrial visual anomaly dataset. It can support anomaly/defect detection research, but it does not provide a general machine-part identity catalogue, OEM fitment, error-code meaning, repair manuals, or verified repair procedures. Keep these labels and evaluation tasks separate.

### Repeatable archive acquisition and verification

The archive is not included in this repository. On a computer or cloud shell with AWS CLI installed, the source registry documents anonymous access without an AWS account:

```bash
aws s3 cp s3://amazon-visual-anomaly/VisA_20220922.tar ./VisA_20220922.tar --no-sign-request --region us-west-2
sha256sum VisA_20220922.tar
python tools/verify_dataset_archive.py VisA_20220922.tar
```

Record the actual byte size, SHA-256, date, and verification output in the dataset register only after the commands succeed. Do not commit the archive to Git. The verifier checks TAR readability and rejects unsafe member paths and links; it does not establish label correctness, image quality, or license compliance.

### Newer MVTec AD 2 candidate

[MVTec AD 2](https://www.mvtec.com/research-teaching/datasets/mvtec-ad-2) contains more than 8,000 images across eight challenging anomaly scenarios, but its official page states CC BY-NC-SA 4.0 and explicitly restricts commercial use. It remains in the non-commercial research track unless separate written permission is obtained.
