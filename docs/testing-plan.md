# Testing and release plan

**Status:** planned checks; no successful test run is claimed here.

## Repository checks
- Confirm project structure and dependency versions.
- Add formatting, static analysis, and unit tests when source code is introduced.
- Keep CI logs attached to the commit/release they validate.

## Android app checks
- Build a debug APK from a clean checkout using documented commands.
- Install and launch on a supported real Android phone.
- Test camera permission granted/denied, camera unavailable, rotation, background/resume, low storage, and process restart.
- Verify repeated camera use does not cause blinking, crashes, or uncontrolled camera sessions.
- Test airplane mode and no-network behavior.

## Recognition checks
- Evaluate on held-out data not used for training or tuning.
- Include varied lighting, angles, backgrounds, occlusion, damage, and visually similar parts.
- Measure per-class precision/recall/F1, confusion matrix, abstention rate, and latency on named devices.
- Test unknown parts and unsupported machine variants.
- Do not claim field reliability from a small curated sample.

## Safety checks
- Verify high-risk tasks are blocked from unsafe step-by-step guidance.
- Safety decisions must not depend only on a generative model.
- Ensure machine-specific instructions link to applicable sources.
- Obtain qualified expert review before release.

## Release evidence
Include app version, source commit, build instructions, tested device/Android version, model version/checksum, dataset provenance, evaluation report, limitations, and rollback instructions.

No APK build, device test, trained model, or measured recognition result is asserted by this plan.
