# Proposed system architecture

**Status:** design proposal only; not implemented or validated.

## Data flow
1. Context capture: machine category, manufacturer, model, variant, and year where known.
2. Image capture: check image quality and request a useful angle; do not imply certainty from a poor image.
3. Candidate recognition: a future on-device vision model returns ranked candidate parts and uncertainty.
4. Context validation: check candidates against machine metadata, compatible part records, and exclusions.
5. Knowledge retrieval: retrieve references matching the machine/variant; clearly label generic references.
6. Safety decision: block high-risk DIY procedures and escalate to official manuals or qualified technicians.
7. User response: show source, uncertainty, applicability, and safe next action.

## Six-dimensional context proposal
- T — Temporal/task stage.
- R — Risk and hazards.
- C — Context: machine identity and operating conditions.
- P — Part/spatial state and visible condition.
- A — User action state.
- L — Logic and validation constraints.

This is a proposed organizing framework, not a validated algorithm.

## Component boundaries
- Android UI: consent, machine selection, camera, results, offline status.
- Inference layer: preprocessing and model execution; expose model/version diagnostics.
- Knowledge layer: structured parts, symptoms, error codes, and references.
- Safety layer: deterministic rules that can block unsafe instructions independently of model output.
- Update layer: signed/versioned content and model updates when online.

## Failure handling
- Unknown machine/variant: ask for clarification; do not guess compatibility.
- Poor image or ambiguous candidates: request another image or manual identification.
- No verified source: label as unverified; avoid procedural repair steps.
- Model/runtime failure: show an error; do not fabricate a result.
- Offline with missing reference: explain that the source is unavailable offline.

## Privacy and security
Collect only necessary information. Do not upload camera images by default. Disclose cloud processing and obtain consent if introduced. Never commit credentials or API keys.
