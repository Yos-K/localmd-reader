# Manual Device Check v0.1.0

Use this checklist for the final human pass before publishing MdLite Reader
v0.1.0.

Test build:

```text
/sdcard/Download/mdlite-reader-debug.apk
```

## Install And Launch

- [x] Install the debug APK from Downloads.
- [x] Launch the app from the Android launcher.
- [x] Confirm the launcher name is `MdLite Reader`.
- [x] Confirm the launcher icon matches the finalized dark Markdown icon.
- [ ] Confirm the first screen is readable and has no clipped text.

## File Opening

- [x] Open a `.md` file from the in-app picker.
- [x] Open a `.markdown` file from the in-app picker.
- [x] Open a Markdown file from an Android file manager.
- [x] Open one Markdown file from Termux with `mdlite-reader FILE.md`.
- [x] Open multiple Markdown files from Termux with `mdlite-reader FILE1.md FILE2.md`
  and confirm they open as tabs.
- [x] Open an unsupported file and confirm a dialog explains why it cannot be opened.
- [x] Open a file above 10 MB and confirm a dialog explains the size limit.

## Reading

- [x] Confirm headings, paragraphs, lists, checklists, blockquotes, code blocks,
  inline code, links, tables, and horizontal rules render correctly.
- [x] Open a table wider than the screen and confirm horizontal scrolling is
  discoverable and usable.
- [x] Open a table narrower than the screen and confirm it does not look
  scrollable.
- [x] Tap an HTTP or HTTPS link and confirm it opens through an external app.

## Navigation

- [x] Open two or more Markdown files as tabs.
- [x] Switch tabs by tapping.
- [x] Close a tab.
- [x] Close the final document tab and confirm the app returns to Welcome.
- [x] Restart the app and confirm restorable tabs come back.
- [ ] Confirm unreadable restored tabs are skipped without crashing.

## Menu And Settings

- [x] Open the menu from the hamburger button.
- [x] Open the menu with a left-edge swipe.
- [x] Close the menu by tapping outside it.
- [x] Close the menu by swiping outward from the drawer area.
- [x] Switch light and dark theme.
- [x] Switch UI language between English and Japanese.
- [x] Move controls to the bottom, restart, and confirm placement is restored.
- [x] Open the Privacy dialog and confirm it says the app does not collect
  personal information.

## Recent Files

- [x] Open Recent files when empty and confirm the empty-state dialog.
- [x] Open a file and confirm it appears in Recent files.
- [x] Reopen a file from Recent files and confirm it moves to the top.
- [x] Clear recent file history and confirm a completion dialog appears.

## Gesture And Layout

- [x] Pinch to increase and decrease font size.
- [ ] Confirm heading sizes change with the body font size.
- [ ] Rotate the device if enabled and confirm text remains readable.
- [x] Confirm dark mode colors are applied to the toolbar, tabs, drawer, and
  dialogs.

## Privacy And Safety

- [ ] Confirm no Android network permission prompt appears.
- [ ] Confirm no login, account, ad, analytics, sync, or remote upload flow
  appears.
- [ ] Confirm screenshots used for Play Store do not contain personal file names,
  paths, account data, notifications, or tokens.
