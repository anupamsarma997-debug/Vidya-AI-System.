# Vidya — AI-Driven Multimodal Guidance and Monitoring System

> **Status: foundation documentation / early development.** This repository does not currently contain a verified Android application, trained recognition model, downloaded dataset, or validated repair workflow.

Vidya is intended to become an offline-first assistant that helps users identify machine components, understand symptoms, and find safe, model-specific maintenance guidance. The long-term direction is multimodal: camera images, user-entered symptoms, machine context, and structured safety checks.

## Current verified repository contents
- Project overview and development documentation.
- Proposed architecture, dataset governance, and validation plan.
- Versioned machine/part JSON Schema with source and verification requirements.
- A conservative safety-gate domain model with unit tests (not yet connected to a repair workflow).
- Kotlin machine/part record model, structural validator, reviewed-record offline search catalogue, and JSON asset loader with unit tests.
- A minimal Kotlin Android application shell is now present under `app/`.
- GitHub Actions CI is configured to build a debug APK and run unit tests.
- The bundled machine-parts catalogue is intentionally empty until traceable, licensed records are acquired and reviewed.
- Camera recognition, model weights, external datasets, and repair workflows are still not implemented.

Do not interpret proposed targets or diagrams as measured performance. No accuracy, latency, or safety claim is made until tested and documented.

## Intended capabilities
1. Machine context: manufacturer, model, year/variant, and machine category.
2. Visual assistance: identify candidate components from a camera image, showing uncertainty clearly.
3. Symptom guidance: collect observable symptoms and relevant error codes.
4. Knowledge retrieval: retrieve applicable manuals, service bulletins, and verified maintenance references.
5. Safety gate: stop and direct users to qualified technicians for high-voltage, fuel/gas, pressure, lifting, moving-machinery, or other serious hazards.
6. Offline-first operation: prioritize on-device inference and locally available reference content.

## Proposed development sequence
| Stage | Deliverable | Completion evidence |
|---|---|---|
| 0 | Repository foundation | Documentation and conventions |
| 1 | Android app shell | Kotlin launcher screen added; build and installation still need CI/device verification |
| 2 | Machine/parts knowledge schema | Versioned records with traceable sources |
| 3 | Dataset acquisition | Download manifest, licenses, hashes, provenance |
| 4 | Baseline vision model | Reproducible training and held-out evaluation |
| 5 | On-device inference | Model packaged and tested on supported phones |
| 6 | Guided workflows | Safety review and user testing |

## Proposed technical direction
- Android client (Kotlin; framework and minimum Android version to be selected).
- On-device model runtime selected after device benchmarking.
- Versioned machine/part knowledge data with source attribution.
- Optional backend for controlled updates; core guidance should degrade safely offline.

## Documentation
- [Android app shell and build instructions](docs/android-app-shell.md)
- [Architecture](docs/architecture.md)
- [Dataset governance](docs/dataset-governance.md)
- [Testing and release plan](docs/testing-plan.md)
- [Dataset register template](data/dataset-register.csv)
- [Machine/part data model](docs/machine-part-data-model.md)
- [Offline record validation](docs/offline-record-validation.md)
- [Offline machine/part catalogue](docs/offline-catalogue.md)
- [JSON asset loader](docs/json-asset-loader.md)
- [Machine/part JSON Schema](data/schemas/machine-part-record.schema.json)

## Evidence rules
- Never label a dataset downloaded until the archive is obtained, extracted, and checked.
- Never label a model trained until training artifacts and reproducible logs exist.
- Never claim recognition accuracy without held-out evaluation and disclosed metrics.
- Never present generic advice as machine-specific repair instructions.
- Record source licenses, attribution, machine variant, and data limitations.
- Do not advise bypassing guards, interlocks, electrical isolation, or manufacturer safety procedures.

## License
No project-wide license has been selected. Until one is added, do not assume this repository grants permission to reuse its contents.
