# Android app shell

The initial Android application module uses Kotlin and the Android Gradle Plugin. It is intentionally a status-only shell.

## Build

Requirements: JDK 17, Android SDK platform 35, Android build tools, and Gradle 8.9.

From the repository root:

    gradle :app:assembleDebug
    gradle :app:testDebugUnitTest

The CI workflow installs Gradle 8.9 and runs these commands. A successful workflow run is required before claiming the build passes.

## Current limitations

- No camera permission or camera capture flow.
- No image classifier or model file.
- No downloaded dataset or trained weights.
- No repair database or machine-specific instructions.
- No release signing configuration.

The first screen deliberately reports these limitations instead of showing fake recognition results. Package ID: org.vidya.ai.
