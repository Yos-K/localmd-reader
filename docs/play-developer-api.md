# Google Play Developer API

This document defines the API-based release workflow for MdLite Reader.

## Scope

Use the Google Play Developer Publishing API for repeatable release operations:

- Upload a signed AAB.
- Assign the uploaded AAB to a testing track.
- Add release notes.
- Commit or validate the edit.

Some first-time setup still requires Play Console UI:

- Legal agreements and declarations.
- Developer account verification.
- App creation.
- Package ownership verification.
- Service account permission setup.
- Production access application.

Google's API uses the `edits` workflow. Changes are staged in an edit and do
not take effect until the edit is committed.

## Service Account Setup

1. Open Google Cloud Console.
2. Create or choose a project for Play Console automation.
3. Enable `Google Play Android Developer API`.
4. Create a service account.
5. Create a JSON key for that service account.
6. Save the JSON key outside the repository:

```text
~/AndroidDev/secrets/google-play-service-account.json
```

7. Open Play Console.
8. Go to `Users and permissions`.
9. Invite the service account email as a user.
10. Grant only the permissions needed for release management on MdLite Reader.

Do not commit the JSON key. The repository ignores `service-account*.json`, but
the preferred location is outside the repository.

## Python Dependencies

Install API dependencies:

```sh
python3 -m pip install --user -r requirements-play-api.txt
```

## Upload AAB To Closed Testing

The testing track identifier depends on the track configured in Play Console.
Use the Play Console track id, for example `alpha`, `beta`, `internal`, or a
custom closed testing track id.

List available track identifiers:

```sh
scripts/play-list-tracks.py \
  --service-account "$HOME/AndroidDev/secrets/google-play-service-account.json"
```

Create a draft release:

```sh
scripts/play-upload-closed-test.py \
  --service-account "$HOME/AndroidDev/secrets/google-play-service-account.json" \
  --track TRACK_ID \
  --status draft
```

Create a completed closed test release:

```sh
scripts/play-upload-closed-test.py \
  --service-account "$HOME/AndroidDev/secrets/google-play-service-account.json" \
  --track TRACK_ID \
  --status completed
```

Default AAB:

```text
~/AndroidDev/releases/mdlite-reader/v0.1.0/mdlite-reader-0.1.0-release.aab
```

Package name:

```text
io.github.yosk.mdlite
```

## Notes

- If Play Console changes are made while an API edit is open, the edit may be
  invalidated.
- If the API rejects the app because no initial APK/AAB has been uploaded
  through Play Console, perform the first upload in Play Console and use the API
  for subsequent releases.
- Use `--validate-only` when checking API permissions without committing a
  release.
