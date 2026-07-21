# Android 16 Target Migration

## Policy

LocalMD Reader targets Android 16 (API 36) before the Google Play deadline of
August 31, 2026. Gradle CI is the release authority and compiles and packages
the Play bundle with API 36. The target API fitness check prevents Gradle,
manifest, CI, and Play release configuration from drifting apart.

Termux verification APKs are a documented exception: Termux `aapt2` cannot load
API 35 or API 36 platform resources, so local scripts compile against API 33
while preserving `targetSdkVersion=36` in the manifest. They verify application
behavior but are not release artifacts.

## Android 16 Behavior Review

- Edge-to-edge: supported. `MainActivity` consumes top and bottom
  `WindowInsets`; no edge-to-edge opt-out is declared.
- Predictive back: supported by default. The app does not override
  `onBackPressed` or consume `KEYCODE_BACK`.
- Adaptive layouts: no orientation, resizability, or aspect-ratio restriction is
  declared. API 36 device smoke covers launch and primary interaction flow.
- Fixed-rate scheduling: not applicable; the app does not use
  `scheduleAtFixedRate`.
- Health, Bluetooth, MediaStore, and local-network changes: not applicable to
  the app's permissions or APIs. The reader intentionally has no `INTERNET`
  permission.
- Safer Intents: strict matching remains opt-in on Android 16. Existing incoming
  VIEW, SEND, SEND_MULTIPLE, and PROCESS_TEXT flows remain covered by tests.

## Verification

- `scripts/test-target-api-policy.sh`
- `sh test.sh`
- Gradle Free and Pro Preview unit tests
- Gradle lint, debug APKs, and Free release AAB
- Android 16 device smoke

References:

- [Google Play target API requirements](https://support.google.com/googleplay/android-developer/answer/11926878)
- [Android 16 behavior changes for target API 36](https://developer.android.com/about/versions/16/behavior-changes-16)
- [Android 16 SDK setup](https://developer.android.com/about/versions/16/setup-sdk)
