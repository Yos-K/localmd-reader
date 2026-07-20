# ADR-0004: Use Android's Storage Access Framework

Status: Accepted

## Decision

Open files and project roots through Android's Storage Access Framework. Keep
persistable read permissions when granted, identify documents by content URI,
and recover through the picker when a remembered permission becomes invalid.

## Alternatives Considered

- Request broad or all-files storage access.
- Navigate raw filesystem paths directly.
- Build a custom storage provider or file manager.
- Use system document providers and scoped URI grants.

## Why This Decision

SAF gives the user control over accessible data, works across local and provider
storage, and avoids broad permissions while allowing durable project access.

## Why Alternatives Were Rejected

Broad storage access violates least privilege and Play expectations. Raw paths
are not stable across providers or modern Android storage. A custom manager
duplicates platform behavior and still cannot bypass provider boundaries safely.

## Reconsider When

Reconsider if Android replaces SAF, persisted grants become unavailable, or a
new scoped API offers equal provider coverage with a simpler user journey.

