# ADR-0025: Send Termux Documents with One Scalar Base64 Batch Intent

Status: Accepted

## Decision

The Termux command opens all requested Markdown files with one explicit
`OPEN_TEXTS_BASE64` intent. One scalar string extra carries an ordered batch of
Base64-encoded title, source identifier, and Markdown content records. The
records become tabs through the existing document-opening session. Keep
`OPEN_TEXTS` string-array support for ADB and existing automation.

## Alternatives Considered

- Continue sending all documents in one intent with `--esa` string arrays.
- Send raw Markdown through a regular string extra.
- Expose file paths or `file://` URIs for LocalMD Reader to read directly.
- Send one Base64 document per intent while retaining the existing array action.
- Send all Base64 documents as records in one scalar string extra.

## Why This Decision

The Termux `am` implementation version 0.8.0 accepts `--esa` syntactically but
does not include those string-array extras in the delivered intent. A scalar
`--es` extra is delivered. Sending one intent per document is also invalid:
after the first intent, LocalMD Reader is foreground and Android can suppress
later starts from the now-background Termux process. One Base64 batch preserves
newlines, shell-sensitive Markdown characters, and argument order in one start.
The app's existing session already owns tab creation and duplicate handling.

## Why Alternatives Were Rejected

Keeping `--esa` leaves the documented command silently opening no files. Raw
Markdown is vulnerable to shell quoting and argument-boundary loss. Direct file
paths are private to Termux and cannot be read by another Android application;
`file://` sharing is also incompatible with modern Android storage boundaries.
One intent per document loses later documents under Android background-start
restrictions. Removing `OPEN_TEXTS` would break working ADB and CI harnesses
without improving the Termux path.

## Reconsider When

Reconsider if Termux provides a verified `am` version that transports string
arrays, if Android introduces a secure streaming contract for app-private
content, or if Binder transaction limits require a chunked or provider-based
transport for supported document sizes.
