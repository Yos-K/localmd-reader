# v0.1.0 Release Checklist

This checklist defines the minimum checks before publishing MdLite Reader v0.1.0.

## Repository

- [ ] The repository is private before the first release is ready.
- [ ] `main` is clean.
- [ ] All release changes are committed with Conventional Commits style.
- [ ] No production signing keys are committed.
- [ ] No secrets, tokens, account data, or private file paths are committed.
- [x] Production signing procedure is documented.

## Version And Identity

- [x] App name is `MdLite Reader`.
- [x] Package name is `io.github.yosk.mdlite`.
- [x] `versionName` is `0.1.0`.
- [x] `versionCode` is `1`.
- [x] Launcher icon is configured.
- [x] Round launcher icon is configured.

## Automated Checks

- [x] `./test.sh` runs JVM unit tests.
- [x] `./test.sh` builds the debug APK.
- [x] `./test.sh` verifies APK signing.
- [x] `./test.sh` verifies the APK does not request `android.permission.INTERNET`.
- [x] `./test.sh` verifies app name, version, and launcher icon declarations.

## Core File Opening

- [x] Open `.md` files from the in-app picker.
- [x] Open `.markdown` files from the in-app picker.
- [x] Open Markdown files from Android file managers.
- [x] Reject unsupported file types with a clear dialog.
- [x] Reject files above 2 MB with a clear dialog.
- [x] Explain unreadable documents with likely causes.
- [x] Do not request broad storage permission.

## Markdown Rendering

- [x] Render headings.
- [x] Render paragraphs.
- [x] Render bullet lists.
- [x] Render numbered lists.
- [x] Render checklists.
- [x] Render blockquotes.
- [x] Render fenced code blocks.
- [x] Render inline code.
- [x] Render HTTP and HTTPS links.
- [x] Render tables.
- [x] Render horizontal rules.
- [x] Escape raw HTML.
- [x] Reject unsafe link schemes such as `javascript:`.
- [x] Do not crash on empty input.
- [x] Do not crash on generated mixed Markdown text.

## Viewer Experience

- [x] First-run welcome screen explains the primary actions.
- [x] Light theme works.
- [x] Dark theme works.
- [x] Pinch gesture changes font size.
- [x] Heading sizes follow viewer font size.
- [x] Horizontal table scrolling works when table width exceeds screen width.
- [x] Links open through an external app.

## Tabs And Navigation

- [x] Open multiple Markdown files as tabs.
- [x] Switch tabs by tapping.
- [x] Close tabs.
- [x] Restore open tabs after app restart.
- [x] Skip unreadable restored tabs.
- [x] Use the hamburger menu for secondary actions.
- [x] Drawer actions are grouped by purpose.
- [x] App UI language can switch between English and Japanese.
- [x] Open the menu from the hamburger button.
- [x] Open the menu with left-edge swipe.
- [x] Close the menu by tapping outside it.
- [x] Move controls to the top or bottom.
- [x] Restore the saved controls position on app start.

## Recent Files

- [x] Store recent files locally in app-private preferences.
- [x] Limit recent files to 5 entries.
- [x] Reopen a recent file from the recent files dialog.
- [x] Move a reopened recent file to the top of the list.
- [x] Show an empty-state dialog when there are no recent files.
- [x] Clear recent file history from the recent files dialog.

## Privacy

- [x] No ads.
- [x] No analytics SDK.
- [x] No login or account.
- [x] No automatic crash reporting.
- [x] No `INTERNET` permission.
- [x] Markdown content is not uploaded by the app.
- [x] Logs must not contain Markdown content, file names, file paths, full Android URIs, account data, or tokens.
- [x] `PRIVACY.md` exists.
- [x] `PRIVACY.ja.md` exists.

## Security

- [x] WebView JavaScript is disabled.
- [x] WebView DOM storage is disabled.
- [x] WebView database storage is disabled.
- [x] WebView file access is disabled.
- [x] WebView content access is disabled.
- [x] Raw HTML is escaped before display.
- [x] Only HTTP and HTTPS links are clickable.
- [x] `SECURITY.md` exists.
- [x] `SECURITY.ja.md` exists.

## Documentation

- [x] `README.md` exists and is the primary English document.
- [x] `README.ja.md` exists.
- [x] `LICENSE` exists and is Apache-2.0.
- [x] `CONTRIBUTING.md` exists.
- [x] `CONTRIBUTING.ja.md` exists.
- [x] `docs/release-signing.md` exists.
- [x] `docs/release-signing.ja.md` exists.
- [x] AAB build script exists for Play Store upload builds.
- [x] Product spec matches the implemented v0.1.0 scope.

## Manual Device Checks

- [ ] Install `/sdcard/Download/mdlite-reader-debug.apk`.
- [ ] Launch the app from the Android launcher and confirm the icon/name.
- [ ] Open a Markdown file from the in-app picker.
- [ ] Open a Markdown file from a file manager.
- [ ] Open an unsupported file and confirm the reason dialog.
- [ ] Open Recent files when empty and confirm the empty-state dialog.
- [ ] Open two or more tabs, close one, restart the app, and confirm tab restoration.
- [ ] Switch light/dark theme.
- [ ] Pinch to change font size.
- [ ] Move controls to bottom, restart, and confirm placement is restored.
- [ ] Open a table wider than the screen and confirm horizontal scrolling.

## Release Actions

- [ ] Run `./test.sh` immediately before release.
- [ ] Create or confirm the production keystore outside the repository.
- [ ] Create a signed release build with the production key outside the repository.
- [ ] Run `scripts/check-release-basics.sh` against the signed release APK.
- [ ] Create a signed release AAB with `scripts/build-release-aab.sh` for Play Store upload.
- [ ] Validate the signed release AAB with `bundletool validate`.
- [ ] Copy the signed release APK to the release staging location.
- [ ] Tag the release as `v0.1.0`.
- [ ] Keep the repository private until the public release decision is made.
