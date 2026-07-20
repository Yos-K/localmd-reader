# ADR-0006: Make Gradle CI the Android Build Authority

Status: Accepted

## Decision

Gradle Wrapper builds on GitHub Actions are authoritative for Android variants,
lint, tests, and Play artifacts. Termux shell builds remain a fast local fallback
for development, but their success does not replace the Gradle CI gate.

## Alternatives Considered

- Make the custom Termux D8/AAPT pipeline authoritative.
- Depend on manual Android Studio builds.
- Build only in CI with no local fallback.
- Use reproducible Gradle CI and retain lightweight Termux feedback.

## Why This Decision

Gradle matches Android's supported dependency and variant model and provides a
reproducible release path. The Termux path still gives rapid on-device feedback
when its older tools can compile the selected dependency set.

## Why Alternatives Were Rejected

The Termux D8 path fails on some modern AndroidX bytecode and can drift from
release behavior. Manual IDE builds are difficult to audit. CI-only builds slow
the local feedback loop and make device development unnecessarily dependent on
network availability.

## Reconsider When

Reconsider if Termux gains a reliable, supported modern Android toolchain with
variant parity, or if another reproducible build system demonstrably replaces
Gradle for CI, signing, lint, and Play delivery.

