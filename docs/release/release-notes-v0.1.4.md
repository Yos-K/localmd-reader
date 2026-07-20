# LocalMD Reader v0.1.4 Release Notes

## Summary

LocalMD Reader v0.1.4 improves file-list dialogs, folder reselection, and
gesture reliability for the free reader.

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
- GitHub Actions CI including Gradle build, mutation, drift check, and gesture
  smoke.
