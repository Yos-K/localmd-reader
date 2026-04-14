# Play Store Screenshots

This document defines the screenshot capture workflow for MdLite Reader v0.1.0.

## Safety Rules

- Use only sample Markdown files from `play-store/samples/`.
- Do not show private file names, private paths, account names, notifications, or tokens.
- Clear notifications before capture.
- Prefer Do Not Disturb mode while capturing.
- Review every screenshot before committing it.
- Crop or retake screenshots if the status bar or navigation area contains personal information.

## Sample Files

Copy the samples to the public Download folder before capture:

```sh
cp play-store/samples/screenshot-document.md /sdcard/Download/mdlite-screenshot-document.md
cp play-store/samples/screenshot-wide-table.md /sdcard/Download/mdlite-screenshot-wide-table.md
```

## Capture Command

Capture the current screen:

```sh
scripts/capture-play-store-screenshot.sh phone-01-welcome
```

The output directory is:

```text
play-store/screenshots/
```

## Required Set

Phone screenshot 1:

```text
Welcome screen with actions to open a Markdown file or view recent files.
```

Capture name:

```text
phone-01-welcome.png
```

Phone screenshot 2:

```text
Rendered Markdown document with headings, lists, code, links, and a table.
```

Capture name:

```text
phone-02-document.png
```

Phone screenshot 3:

```text
Dark theme reading view with a wide table.
```

Capture name:

```text
phone-03-dark-table.png
```

Phone screenshot 4:

```text
Tabs and the menu for theme, language, placement, and recent files.
```

Capture name:

```text
phone-04-tabs-menu.png
```

## Manual Steps

1. Install the latest debug APK from `/sdcard/Download/mdlite-reader-debug.apk`.
2. Open the app and capture the welcome screen.
3. Open `/sdcard/Download/mdlite-screenshot-document.md`.
4. Capture the rendered document screen.
5. Open `/sdcard/Download/mdlite-screenshot-wide-table.md`.
6. Switch to dark theme and capture the wide table screen.
7. Open two tabs, open the menu, and capture the tabs/menu screen.
8. Review the screenshots for personal information.
9. Copy approved screenshots to `play-store/screenshots/`.
