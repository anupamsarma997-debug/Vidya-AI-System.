# Vidya large-scale image target: 654,321 real images

## Hard target

Vidya's minimum scale target is **654,321 unique, usable, source-traceable real images**. This is a target, not an achieved count. The repository currently does not contain these archives, and no large-scale training run has been completed.

Do not count:
- synthetic/rendered/generated images toward the real-image target;
- duplicate or near-duplicate frames as new independent samples;
- validation or held-out test images as training images;
- text records, image pairs, patches, crops, or augmentations as extra original photographs;
- dataset headline counts until archive files and actual image counts are verified.

Every dataset needs a provenance record, license review, archive checksum, extraction report, image count, class map, duplicate report, and split manifest. If two datasets overlap, deduplicate before calculating the unique-image total.

## Task-separated data pools

A single classifier must not be trained by mixing incompatible labels. Vidya needs distinct model heads / training pipelines:

1. **Part identity and retrieval:** exact component/object identity, variant and viewpoint. Highest priority for the intended camera-based parts finder.
2. **Defect / anomaly detection:** normal vs defect, defect class, masks and localization. This can flag visible damage but cannot identify an OEM replacement part.
3. **Machine and tool context:** machine category, make/model/variant and scene context.
4. **Textual technical knowledge:** manuals, parts catalogs, service bulletins and error-code references. These are documents/structured records, not image training samples.
5. **Safety and procedure knowledge:** curated and reviewed rules, separate from visual model labels.

## Candidate scale sources (counts are source-reported, not yet acquired)

| Source | Reported image count | Status and limitations |
|---|---:|---|
| [Real-IAD Variety](https://realiad4ad.github.io/Real-IAD-Variety/) | 198,950 | Hugging Face gated; license must be inspected. Project page says 19,955 train images and 178,995 test images. Never train on its test set. Defect/anomaly data. |
| [Real-IAD](https://realiad4ad.github.io/Real-IAD/) | 150,000 | Hugging Face gated; CC BY-NC-SA 4.0 / research-only. Not suitable for commercial training without separate permission. Defect/anomaly data. |
| [MANTA](https://openaccess.thecvf.com/content/CVPR2025/papers/Fan_MANTA_A_Large-Scale_Multi-View_and_Visual-Text_Anomaly_Detection_Dataset_for_Tiny_Objects_CVPR_2025_paper.pdf) | 137,338 | Multi-view images across agriculture, medicine, electronics, mechanics and groceries. Direct download and license need confirmation; not all are machine components. |
| [IRISP](https://github.com/imyliu1717/industrial_spare_part_dataset) | 141,937 | Fine-grained industrial spare parts, CC BY-SA 4.0 as declared, but project README currently says full dataset download TBA. Do not count as acquired. |
| [M2 screw defect dataset](https://data.mendeley.com/datasets/2y6mcsyrpy/2) | 5,536 | Declared CC BY 4.0; two RAR archives listed. Download and verify first. Narrow screw defect labels only. |
| [VisA](https://registry.opendata.aws/visa/) | 10,821 | CC BY 4.0 as stated by AWS registry. Download and verify archive. Industrial anomaly data, not OEM part identity. |
| [MVTec AD 2](https://www.mvtec.com/research-teaching/datasets/mvtec-ad-2) | 8,000+ | CC BY-NC-SA 4.0; non-commercial only. Separate license-restricted research pool. |
| [MVTec AD](https://www.mvtec.com/research-teaching/datasets/mvtec-ad) | 5,354+ | CC BY-NC-SA 4.0; non-commercial only. Separate license-restricted research pool. |
| [KolektorSDD2](https://www.vicos.si/resources/kolektorsdd2/) | 3,347 | CC BY-NC-SA 4.0; non-commercial only. |
| [NEU surface defect database](https://faculty.neu.edu.cn/songkc/en/zdylm/263265/) | 1,800 | Reuse license not clearly stated; permission needed before commercial use. |
| [MPDD](https://github.com/stepanje/MPDD) | 1,000+ | CC BY-NC-SA reported by dataset index; verify primary repository license and image count. |

**Important arithmetic note:** adding headline counts from these sources is not a valid unique real-image count. Some sources are gated, some have unclear licensing, some contain non-machine categories, and some are not downloadable. There may be overlap. The verified unique training count remains **0** until archives are downloaded, extracted, inspected, deduplicated, licensed, and split.

## Synthetic data: separate optional pool

[SORDI.ai](https://sordi.ai/dataset) reports over 1.2 million synthetic industrial images and 120+ object classes. These are digital-twin / rendered images, not real photographs. They may be useful as a separate synthetic pretraining pool after Kaggle license terms are checked, but they must never be used to claim the 654,321 real-image target has been met. Evaluate real-world transfer on held-out real images.

## Training and evaluation protocol

- Use a separate dataset adapter for each source; never assume every archive has ImageFolder folders.
- Keep original publisher train/validation/test splits whenever provided.
- For datasets with only train/test, create validation only from train.
- Deduplicate exact hashes and near-duplicates before splitting; group by object/serial/session/camera where possible to prevent leakage.
- Report per-class precision, recall, F1, confusion matrix, AUROC / AUPRO or retrieval Recall@K as appropriate to the task.
- Keep test images untouched until final evaluation.
- Record source-level and combined unique counts separately.
- Report license restrictions and commercial eligibility per model artifact.
- Do not deploy a defect model as a part-number recognition model.
- Do not generate repair steps from visual predictions; use separately verified machine-specific documents and a safety gate.

## Storage and compute reality

Real-IAD 1024px is reported at about 53 GB; raw data about 507 GB. MANTA and Real-IAD Variety also require substantial storage. A mobile phone is suitable for opening Colab and monitoring a run, not storing or training on hundreds of thousands of high-resolution images. Use cloud storage/runtime with adequate disk, bandwidth and GPU. Free Colab resources are not guaranteed and may disconnect.

## Current execution status

- Dataset source research: ongoing.
- Archives downloaded and verified: none in this repository.
- Unique real training images verified: 0.
- Model training on these large datasets: not started.
- On-device integration: not started.

The next valid milestone is to obtain access to Real-IAD Variety / Real-IAD and verify the license, then download a manageable source subset and run the task-specific baseline. The 654,321 count is a minimum acceptance gate, not a promise that all datasets can be legally combined or that the model will reach a particular accuracy.
