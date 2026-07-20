# LocalMD Reader v0.2.10 Release Notes

## Summary

LocalMD Reader v0.2.10 improves free-reader file navigation, dialog usability,
and gesture reliability.

## Changes

- Added explicit close actions to recent-file and pinned-file dialogs.
- Added a way to choose another folder after opening the Markdown library.
- Hid clipboard diagnostics from the normal user menu.
- Stabilized double-tap shortcut recognition by keeping the gesture stream
  active from the initial touch event.
- Improved GitHub Actions diagnostics for gesture smoke failures and Play
  upload runs.

## Verification

- Unit tests.
- Test smell checks.
- Documentation sync checks.
- Release preflight checks.
