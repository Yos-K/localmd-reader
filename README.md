# MdLite Reader

<p align="center">
  <img src="play-store/icon-512.png" alt="MdLite Reader logo" width="128" height="128">
</p>

<p align="center">
  Lightweight Android Markdown reading without ads, tracking, or network access.
</p>

MdLite Reader is a lightweight Android Markdown viewer.

It is built for reading local Markdown files quickly without ads, tracking,
login, sync, or network access.

## Features

- Open `.md` and `.markdown` files
- Open Markdown files from Android file managers
- Open Markdown files from Termux with `mdlite-reader`
- Render Markdown locally in a locked-down WebView
- Light and dark themes
- Pinch-to-change font size
- Recently opened files, limited to 5 entries
- Clear recent file history
- Multiple tabs
- Restore previously opened tabs
- Move controls to the top or bottom
- No `INTERNET` permission

## Supported Markdown

MdLite Reader intentionally implements a small Markdown subset for v0.1.0.

Supported:

- Headings
- Paragraphs
- Bullet lists
- Numbered lists
- Checklists
- Blockquotes
- Fenced code blocks
- Inline code
- HTTP and HTTPS links
- Tables
- Horizontal rules
- Raw HTML escaping

Not supported in v0.1.0:

- Markdown editing
- Full CommonMark compatibility
- Mermaid
- Math
- Footnotes
- Remote image loading
- Relative image rendering
- Cloud sync

## Usage

See [docs/usage.md](docs/usage.md).

Termux command details are in [docs/termux-command.md](docs/termux-command.md).

## Privacy

MdLite Reader does not collect personal information.

The app reads only files selected by the user. Markdown content is rendered on
the device and is not uploaded by the app.

See [PRIVACY.md](PRIVACY.md).

## Security

The initial version requests no network permission and disables JavaScript in
the WebView used for rendering.

See [SECURITY.md](SECURITY.md).

## Build

The project is currently built with a lightweight Android SDK setup in Termux.

```sh
cd ~/AndroidDev
. ./env.sh
cd projects/mdlite-reader
./build.sh
```

The generated debug APK is:

```text
app-debug.apk
```

## Test

```sh
./test.sh
```

The test script runs JVM unit tests, builds the debug APK, verifies signing,
and checks that the APK does not request the `INTERNET` permission.

## Release Signing

Production signing uses a keystore outside the repository.
Play Store upload builds use signed Android App Bundle output.

See [docs/release-signing.md](docs/release-signing.md).

## Repository Status

This repository is public for closed testing and release preparation.

Closed testing notes are in [docs/closed-testing-guide.md](docs/closed-testing-guide.md).
Tester invitation text is in
[play-store/testing/closed-test-invitation.txt](play-store/testing/closed-test-invitation.txt).

## License

Apache License 2.0. See [LICENSE](LICENSE).

## Japanese

Japanese documentation: [README.ja.md](README.ja.md)
