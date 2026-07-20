# Versioning

LocalMD Reader keeps release version metadata in the repository root `VERSION`
file.

```text
VERSION_NAME=0.1.0
VERSION_CODE=13
```

`VERSION_NAME` is the user-facing version. Use semantic-version style numbers:

- `patch`: bug fixes, copy updates, small UX improvements
- `minor`: user-visible feature additions

`VERSION_CODE` is the Google Play version code. It must increase for every
uploaded APK or AAB.

For Play submissions, do not bump only `VERSION_CODE`. Every user-visible
release must also bump `VERSION_NAME`. `scripts/release-preflight.sh` fails when
the current `VERSION_NAME` is still equal to the latest `vX.Y.Z` release tag.

## Commands

Show the current version:

```sh
scripts/version-show.sh
```

Check that `VERSION` and `src/main/AndroidManifest.xml` match:

```sh
scripts/version-check.sh
```

Bump a patch version and increment `VERSION_CODE`:

```sh
scripts/version-bump.sh patch
```

Bump a minor version, reset the patch number, and increment `VERSION_CODE`:

```sh
scripts/version-bump.sh minor
```

Do not use `scripts/version-code-bump.sh` for normal Play submissions. It is
reserved for explicitly documented emergency rebuilds of the same user-facing
version, and requires `MDLITE_ALLOW_VERSION_CODE_ONLY=true`.

Build scripts read `VERSION` and apply it to the generated manifest before
compiling. `./test.sh` also runs `scripts/version-check.sh`.
