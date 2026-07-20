# Production Release Runbook

This runbook is for the first LocalMD Reader production release after Google
Play production access is approved.

## Preconditions

- Production access is approved in Play Console.
- `./test.sh` passes on `main`.
- `VERSION` is correct.
- No unresolved release blockers remain except the production rollout steps.
- Production signing key exists outside the repository.
- `BUNDLETOOL_JAR` points to the local bundletool jar.
- Google Play service account JSON remains outside the repository.

## 1. Decide Whether To Bump Version

If the production release will upload a new AAB, bump both `VERSION_NAME` and
`VERSION_CODE`. Do not ship a version-code-only release except for an explicitly
documented emergency rebuild.

For a bug-fix style update:

```sh
scripts/version-bump.sh patch
```

For a feature-level update:

```sh
scripts/version-bump.sh minor
```

If Play Console can promote the already-reviewed closed testing release without
uploading a new artifact, keep the current version.

Always confirm:

```sh
scripts/version-show.sh
scripts/version-check.sh
```

## 2. Run Release Checks

```sh
./test.sh
```

Do not continue if tests, release checks, third-party notice checks, or version
checks fail.

## 3. Build The Signed AAB

```sh
scripts/build-signed-release.sh aab
```

Default output:

```text
build/release/mdlite-reader-<VERSION_NAME>-release.aab
```

Copy the AAB to the release staging directory:

```sh
mkdir -p "$HOME/AndroidDev/releases/mdlite-reader/v$(. ./VERSION; echo "$VERSION_NAME")"
cp "build/release/mdlite-reader-$(. ./VERSION; echo "$VERSION_NAME")-release.aab" \
  "$HOME/AndroidDev/releases/mdlite-reader/v$(. ./VERSION; echo "$VERSION_NAME")/"
sha256sum "$HOME/AndroidDev/releases/mdlite-reader/v$(. ./VERSION; echo "$VERSION_NAME")/"*.aab \
  > "$HOME/AndroidDev/releases/mdlite-reader/v$(. ./VERSION; echo "$VERSION_NAME")/SHA256SUMS"
```

## 4. Release In Play Console

Use one of these paths:

- Promote the approved closed testing release to production if Play Console
  allows it and no new artifact is needed.
- Upload the newly built AAB to the production track.

Before submitting, confirm:

- Release notes match `docs/release/release-notes-v0.1.0.md`.
- **`play-store/release-notes/<locale>/whatsnew.txt` content is updated for this version (manual check required).**
  `check-release-notes.sh` verifies file presence and the 500-character limit only; it does NOT check
  whether the content refers to the current version. Without this check, stale notes from a previous
  release can ship silently. (Risk accepted: the version-mention check was removed when the file-based
  notes approach was adopted in PR #135.)
- Store listing text, icon, feature graphic, and screenshots are current.
- Data safety and privacy policy are complete.
- Countries/regions are set intentionally.

Submit the production rollout for review.

## 5. Update Repository State

After the production release is submitted or approved:

```sh
git status --short
git log --oneline -5
```

Commit any release metadata updates with Conventional Commits.

Tag the exact release commit:

```sh
git tag v$(. ./VERSION; echo "$VERSION_NAME")
git push origin main --tags
```

If GitHub Releases are used, create a release using
`docs/release/release-notes-v0.1.0.md` as the source text. Do not upload signing keys,
service account JSON, APKs, AABs, or private tester data to the repository.

## 6. Post-Release Checks

- Open the Play Store listing from a browser.
- Install from Google Play on an Android device.
- Open a local Markdown file.
- Open a Markdown file from another app.
- Confirm no Pro-only settings appear in the Free build.
- Confirm Privacy and Pro features screens open.
- Check Play Console for crashes or policy warnings.

## Rollback

If a serious issue is found before rollout completes, halt or pause the rollout
in Play Console. If the release is already live, prepare a patch version with a
higher `VERSION_CODE`, run this runbook again, and submit the fix.
