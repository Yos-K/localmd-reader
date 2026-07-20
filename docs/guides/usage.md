# Usage

This guide explains how to use LocalMD Reader v0.1.1.

## Open A File

Open Markdown from inside the app:

1. Tap `Menu`.
2. Tap `Open file`.
3. Choose a `.md` or `.markdown` file.

Open Markdown from Android:

1. Open a file manager.
2. Select a `.md` or `.markdown` file.
3. Choose LocalMD Reader when Android asks which app to use.

Open Markdown shared from another app:

1. Open a `.md` or `.markdown` attachment in the other app.
2. Choose LocalMD Reader.
3. The document opens as a tab. Previously opened tabs remain available when
   they can still be read.

When Pro is active, use `Menu` -> `Markdown library` to browse a selected
project folder. Enter a file or folder name in the filter field to narrow the
current directory. Clear the field to restore every item in that directory.
The library remains as a tree below its menu action. Use the chevron on that
action to collapse or reopen the tree without losing its current location or
filter. Opening a document closes the menu, and reopening the menu returns to
the same tree location.
Android 11 and later do not allow an app to select the Download folder itself
as a library root. Choose or create a subfolder inside Download instead.

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

## Create From Clipboard

Use `Menu` -> `Create from clipboard` to open clipboard text as a temporary
Markdown document.

- If there is one available item, it opens immediately.
- If there are multiple available items, choose one or more items from the
  dialog and tap `Open selected`.
- Previously opened clipboard items are kept in a local in-app history, limited
  to 10 entries, so they can be opened again later.
- Temporary Markdown documents are not saved automatically. Use `Save as...` to
  choose a file name and save them.

Android does not allow LocalMD Reader to read the full system or keyboard
clipboard history. The history shown by the app contains only clipboard content
that was previously opened inside LocalMD Reader.

## Tabs

- Opening another file adds a tab.
- Tapping a tab switches the active document.
- Tapping the close control closes that tab.
- Closing the final document tab returns to the welcome screen.
- Open tabs are restored after app restart when the documents can still be read.

## Reading Controls

- Pinch in the document to change font size.
- Use `Menu` -> `Find in document` to search text in the current document.
  The search bar stays above the document so you can move to the previous or
  next match without covering the reading area.
- Use `Menu` -> `Settings` -> `Theme` to switch between light and dark themes.
- Use `Menu` -> `Settings` -> `Move controls to bottom` or
  `Move controls to top` to change toolbar placement.
- Use `Menu` -> `Settings` -> `Switch to English` or `日本語に切り替え` to switch
  app language.

## Recent Files

- Use `Menu` -> `Recent files` to reopen recent documents.
- Recent files are limited to 5 entries.
- Use `Clear history` in the Recent files dialog to remove the local history.
- When Pro is active, use `Menu` -> `Pin current file` to keep the active file
  in the pinned list.
- When Pro is active, use `Menu` -> `Unpin current file` to remove the active
  file from the pinned list.
- Use `Menu` -> `Pinned files` to reopen pinned documents.

## Free And Pro Features

The Free build includes local Markdown viewing, code highlighting, basic
Mermaid rendering, tabs, tab restoration, recent files, clipboard-created
temporary Markdown documents, light and dark themes, font size changes, double
tap shortcuts, language switching, and local offline use without ads or network
permission.

Use `Menu` -> `Settings` -> `Pro features` to see which advanced features are
available or locked in the current build.

Pro features make frequent reading faster and more comfortable. They focus on
additional themes, advanced gestures beyond double tap, table of contents, table
reading enhancements, and extended reading history. Free keeps recent files to 5
entries; Pro extends the history to 20 entries and adds pinned documents. Pro
also keeps table headers and first columns visible while scrolling wide tables.
Pro also allows gesture shortcuts to jump to the next or previous heading.
Pro preserves safe relative Markdown links as anchors for linked local document
sets. Pro also renders safe relative Markdown image references for local
document sets. Unsafe schemes such as `file:` or `javascript:` remain plain
text.

When Pro is active, open `Menu` -> `Table of contents` to expand the current
document headings inside the menu. Select a heading to jump to that section.

## Privacy

Use `Menu` -> `Settings` -> `Privacy` to read the in-app privacy statement.

LocalMD Reader v0.1.1 has no ads, analytics SDK, login, sync, crash reporting, or
network permission.

## Limitations

LocalMD Reader v0.1.1 is a viewer, not an editor.

Not supported in v0.1.1:

- Markdown editing.
- Full CommonMark compatibility.
- Math.
- Footnotes.
- Remote image loading.
- Cloud sync.

## File Size Limit

Files larger than 10 MB cannot be opened. This keeps the app responsive on
phones and avoids unexpectedly loading very large documents into memory.
