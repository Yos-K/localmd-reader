# ADR-0022: Restore Tabs Opened from Direct Markdown Text

Status: Accepted

## Decision

Restore Markdown received directly from Termux or another text entry point after
Back navigation and process restart, just like a saved file tab. Store received
text in app-private internal storage under a directory derived from a SHA-256
hash of its source identifier. Receiving the same source again replaces its
stored content and reuses its tab instead of creating a duplicate.

## Alternatives Considered

- Keep direct-text tabs only for the current process.
- Store the Markdown body together with tab preferences.
- Persist the body as an app-private file and reuse normal file restoration.
- Require the user to save explicitly before a direct-text tab can be restored.

## Why This Decision

Users expect tab restoration regardless of whether a document came from the
file picker, another app, or Termux. Exploratory testing found that saved files
restored while Termux tabs silently disappeared. An internal file avoids putting
up to 10 MB of content in preferences and reuses existing size, read, and restore
behavior. Hashing avoids exposing the original local path in internal file names.

## Why Alternatives Were Rejected

Process-only state loses the reading session after activity recreation or
restart. Preferences are unsuitable for large document bodies and increase
startup memory cost. Requiring an explicit save adds friction to a reading flow
and behaves inconsistently with files opened through Android providers.

## Reconsider When

Reconsider if Android provides a standard durable handoff for temporary text,
if app-private copies need storage quotas or automatic expiration, or if restored
tabs must synchronize with their original source instead of preserving a content
snapshot.
