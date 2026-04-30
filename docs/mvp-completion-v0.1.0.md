# v0.1.0 MVP Completion

This document defines the completion criteria for LocalMD Reader v0.1.0.

## MVP Goal

LocalMD Reader v0.1.0 is complete when a user can reliably open local Markdown
files on Android and read them without ads, tracking, login, sync, or network
access.

The release should feel small, fast, and trustworthy. It does not need to be a
full Markdown platform.

## Required User Outcomes

- Open `.md` files from the in-app picker.
- Open `.markdown` files from the in-app picker.
- Open Markdown files from Android file managers.
- Open one or more Markdown files from Termux with `mdlite-reader`.
- Read common Markdown documents with headings, paragraphs, lists, checklists,
  blockquotes, code blocks, inline code, links, tables, and horizontal rules.
- Switch between light and dark theme.
- Adjust reading font size with pinch gestures.
- Open multiple files as tabs.
- Restore open tabs after app restart.
- Reopen recent files from local history.
- Clear recent file history.
- Understand why unsupported, oversized, or unreadable files cannot be opened.
- Confirm from the app UI that the app does not collect personal information.

## Required Engineering Outcomes

- The app requests no `android.permission.INTERNET` permission.
- WebView JavaScript is disabled.
- WebView file access is disabled.
- Raw HTML is escaped before display.
- Only HTTP and HTTPS links are opened externally.
- Recent file and tab metadata stays in app-private storage.
- Tests pass with `./test.sh`.
- Release checks pass for app identity, signing, permissions, and launcher icon.
- No production signing keys, secrets, tokens, account data, or private paths are
  committed.

## Store-Ready Outcomes

- App icon is finalized.
- Feature graphic is exported.
- At least four phone screenshots are captured.
- English Play Store listing text is ready.
- Japanese Play Store listing text is ready.
- Privacy policy documents are ready.
- Data safety declaration plan is ready.
- Release signing procedure is documented.

## Manual Confirmation Required Before Release

- Install `/sdcard/Download/mdlite-reader-debug.apk`.
- Confirm launcher name and icon.
- Confirm file opening from the picker.
- Confirm file opening from an Android file manager.
- Confirm file opening from Termux with one file.
- Confirm file opening from Termux with multiple files.
- Confirm unsupported, oversized, and unreadable file dialogs.
- Confirm tab close and tab restore behavior.
- Confirm light and dark theme.
- Confirm pinch font size adjustment.
- Confirm horizontal table scrolling.
- Confirm language switching.
- Confirm controls placement restore.
- Confirm Recent files and clear history behavior.
- Confirm Privacy dialog wording.

Detailed manual steps are in `docs/manual-device-check-v0.1.0.md`.

## Explicitly Excluded From v0.1.0

- Markdown editing.
- Full CommonMark compatibility.
- Mermaid.
- Math.
- Footnotes.
- Remote image loading.
- Relative image rendering.
- Cloud sync.
- PDF export.
- HTML export.
- Folder browsing.
- Git integration.
- In-app purchase or Pro unlock.

## Image Rendering Decision

Image rendering is excluded from v0.1.0.

Reasons:

- Remote image loading would require network access, which conflicts with the
  initial no-network privacy promise.
- Relative image rendering requires careful document URI and permission handling
  across file providers.
- Broken image handling can make a reader feel unreliable.
- v0.1.0 should keep the app focused on trustworthy local Markdown text reading.

Image support can be reconsidered in a later version after the file permission
model is designed explicitly.

## Completion Judgment

v0.1.0 is MVP-complete when:

- All required user outcomes are implemented.
- All required engineering outcomes are satisfied.
- Store-ready assets and texts exist.
- `./test.sh` passes immediately before release.
- Manual confirmation items are either checked or explicitly deferred.
- Deferred items are documented as not blocking v0.1.0.

## Current Status

Implementation status:

```text
MVP implementation: mostly complete
Manual device confirmation: pending
Play Console entry: pending
Public privacy policy URL: pending until release publication
Release notes: pending
Final signed release build: pending
```
