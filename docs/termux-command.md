# Termux Command

MdLite Reader can be opened from Termux with one or more Markdown files.

Command from the repository:

```sh
scripts/mdlite-open.sh FILE.md [FILE2.md ...]
```

Example:

```sh
scripts/mdlite-open.sh README.md docs/termux-command.md
```

Each readable Markdown file is opened as a tab. If a file is already open, its
tab is refreshed and activated instead of duplicated.

The command starts MdLite Reader directly. Multiple files open one after another
as tabs.

## Global Command

For daily use, install the wrapper into `~/bin`:

```sh
mkdir -p ~/bin
cp scripts/mdlite-open.sh ~/bin/mdlite-reader
chmod +x ~/bin/mdlite-reader
```

Then run:

```sh
mdlite-reader FILE.md [FILE2.md ...]
```

`~/bin` must be included in `PATH`.

## Access Model

The command reads Markdown text in Termux and passes it directly to MdLite
Reader. This allows files in Termux directories to be opened without requesting
broad storage permission.

Android Intent extras have practical size limits. Use the in-app picker or an
Android file manager for large files near the app's 2 MB file size limit.
