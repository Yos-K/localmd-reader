# LocalMD Reader v0.1.3 Release Notes

## Summary

LocalMD Reader v0.1.3 improves the free reader's file selection flow.

## Changes

- Added a free "Choose from folder" action.
- Lets users choose a folder and then pick Markdown files from that folder.
- Keeps the action wording focused on choosing files, avoiding the impression
  that the app opens an entire folder workspace.
- Added a folder Markdown document model that keeps only `.md` / `.markdown`
  files, drops unsupported entries, removes duplicate URIs, and sorts entries
  by display name.

## Verification

- Unit tests.
- Test smell check.
- Documentation currency check.
- D8 debug APK build through `test.sh`.
