# Usage

This guide explains how to use LocalMD Reader v0.1.0.

## Open A File

Open Markdown from inside the app:

1. Tap `Menu`.
2. Tap `Open file`.
3. Choose a `.md` or `.markdown` file.

Open Markdown from Android:

1. Open a file manager.
2. Select a `.md` or `.markdown` file.
3. Choose LocalMD Reader when Android asks which app to use.

## Open From Termux

LocalMD Reader can open Markdown files directly from Termux.

```sh
mdlite-reader README.md
```

Open multiple files as tabs:

```sh
mdlite-reader README.md docs/usage.md
```

The Termux command reads Markdown text in Termux and sends it to LocalMD Reader.
This lets files under the Termux home directory open without broad Android
storage permission.

Detailed setup notes are in `docs/termux-command.md`.

## Tabs

- Opening another file adds a tab.
- Tapping a tab switches the active document.
- Tapping the close control closes that tab.
- Closing the final document tab returns to the welcome screen.
- Open tabs are restored after app restart when the documents can still be read.

## Reading Controls

- Pinch in the document to change font size.
- Use `Menu` -> `Dark theme` or `Light theme` to switch theme.
- Use `Menu` -> `Move controls to bottom` or `Move controls to top` to change
  toolbar placement.
- Use `Menu` -> `Switch to English` or `日本語に切り替え` to switch app language.

## Recent Files

- Use `Menu` -> `Recent files` to reopen recent documents.
- Recent files are limited to 5 entries.
- Use `Clear history` in the Recent files dialog to remove the local history.

## Privacy

Use `Menu` -> `Privacy` to read the in-app privacy statement.

LocalMD Reader v0.1.0 has no ads, analytics SDK, login, sync, crash reporting, or
network permission.

## Limitations

LocalMD Reader v0.1.0 is a viewer, not an editor.

Not supported in v0.1.0:

- Markdown editing.
- Full CommonMark compatibility.
- Mermaid.
- Math.
- Footnotes.
- Remote image loading.
- Relative image rendering.
- Cloud sync.
