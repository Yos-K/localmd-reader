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

The command uses `termux-open`, so Android may ask which app should open the
file the first time. Choose MdLite Reader. After MdLite Reader is the default
Markdown viewer, multiple files open one after another as tabs.

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

The command uses Termux's file sharing bridge instead of passing raw file paths
to the Android app. This allows files in Termux directories to be opened without
requesting broad storage permission.

If Android does not offer MdLite Reader for a file, open the file through the
in-app picker or an Android file manager so the system grants a document URI.
