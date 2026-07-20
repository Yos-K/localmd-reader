# ADR-0001: Keep the Reader Offline by Architecture

Status: Accepted

## Decision

Do not request `INTERNET` permission. Keep JavaScript disabled in the document
reader WebView. Features must operate on local data without ads, tracking,
accounts, or remote configuration.

## Alternatives Considered

- Allow networking for analytics, crash reporting, or remote assets.
- Enable JavaScript in the reader WebView for richer Markdown extensions.
- Keep networking and JavaScript out of the reader and isolate exceptional
  rendering work in constrained local components.

## Why This Decision

The absence of permission and reader scripting makes the privacy promise
auditable at build time, reduces the attack surface of untrusted Markdown, and
keeps the app useful without connectivity.

## Why Alternatives Were Rejected

Policy text alone cannot prevent accidental requests. A scripted reader mixes
untrusted document content with executable behavior. Remote services also add
identity, availability, and dependency costs that conflict with the product.

## Reconsider When

Reconsider only with explicit owner approval if the product identity changes,
or if Android offers a verifiably isolated capability that preserves the same
privacy and untrusted-content boundary.

