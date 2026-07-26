# LocalMD Reader v0.2.13

Release date: 2026-07-26

## Highlights

- Enabled the Google Play purchase flow in the Free Play build.
- Kept every user on the complete Free entitlement until Google Play verifies a one-time Pro purchase.
- Preserved the same package and reading workflow when Pro convenience features are unlocked.

## Compatibility

- Targets Android 16 (API 36) and continues to support Android 6.0 or later.
- No ads, tracking, login, analytics, or app network permission were added.

## Verification

- The build policy test verifies that Play Billing is included without enabling Pro by default.
- The complete local test suite and required CI checks must pass before Play upload.
