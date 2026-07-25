# ADR-0023: Promote Existing Play Bundles Between Tracks

Status: Accepted

## Decision

Promote an already uploaded and verified Google Play bundle to a wider testing
track by its `versionCode`. The promotion workflow runs only in the private
release repository, uses Workload Identity Federation, and does not rebuild,
resign, or upload the AAB again.

## Alternatives Considered

- Build and upload a new AAB for every testing track.
- Promote releases manually in Play Console only.
- Upload directly to production after closed testing.

## Why This Decision

Using the same bundle keeps the tested artifact identical across closed and open
testing. It avoids duplicate-version upload failures and removes signing material
from an operation that does not require signing. An explicit workflow still
provides an auditable review boundary before Play review submission.

## Why Alternatives Were Rejected

Rebuilding can produce a different artifact and Google Play rejects re-uploading
an existing version code. A GUI-only process is difficult to reproduce and audit.
Skipping open testing would widen distribution without the additional validation
chosen for the first public release.

## Reconsider When

Reconsider if Google Play removes track promotion, requires a new artifact per
track, provides a safer first-party promotion mechanism, or the release policy
requires different binaries for closed and open testing.
