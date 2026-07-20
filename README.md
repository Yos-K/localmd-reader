# LocalMD Reader

<p align="center">
  <img src="docs/assets/localmd-reader-icon-512.png" alt="LocalMD Reader logo" width="128" height="128">
</p>

<p align="center">
  Lightweight Android Markdown reading without ads, tracking, or network access.
</p>

LocalMD Reader is a lightweight Android Markdown viewer.

LocalMD Reader is currently in Google Play testing and release preparation.

Closed testing links:

- Web: https://play.google.com/apps/testing/io.github.yosk.mdlite
- Android: https://play.google.com/store/apps/details?id=io.github.yosk.mdlite

It is built for reading local Markdown files quickly without ads, tracking,
login, sync, or network access.

## Features

- Open `.md` and `.markdown` files
- Open Markdown files from Android file managers
- Open Markdown files shared from other apps
- Open Markdown files from Termux with `mdlite-reader`
- Create a temporary Markdown document from clipboard text
- Save temporary Markdown documents with `Save as...`
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

LocalMD Reader intentionally implements a small Markdown subset for v0.1.1.

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

Not supported in v0.1.1:

- Markdown editing
- Full CommonMark compatibility
- Math
- Footnotes
- Remote image loading
- Cloud sync

The Free build is a complete local Markdown reader: code highlighting, basic
Mermaid rendering, tabs, clipboard-created temporary documents, recent files,
and double-tap shortcuts are available without ads or network permission.

Pro is intended to make the same reading workflow more comfortable. Pro-only
features focus on convenience, such as additional themes, advanced gestures,
table of contents, and extended reading history.

## Usage

See [docs/guides/usage.md](docs/guides/usage.md).

Termux command details are in [docs/guides/termux-command.md](docs/guides/termux-command.md).

## Privacy

LocalMD Reader does not collect personal information.

The app reads only files selected by the user. Markdown content is rendered on
the device and is not uploaded by the app.

See [PRIVACY.md](PRIVACY.md).

## Security

The initial version requests no network permission and disables JavaScript in
the WebView used for rendering.

See [SECURITY.md](SECURITY.md).

## Third-Party Notices

Bundled third-party software and required notices are listed in
[THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

## Build

The project is currently built with a lightweight Android SDK setup in Termux.

```sh
cd ~/AndroidDev
. ./env.sh
cd projects/localmd-reader
./build.sh
```

This repository also loads `env.project.sh` during local and release builds to
keep the Android platform and build-tools versions stable for this app.

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

See [docs/release/release-signing.md](docs/release/release-signing.md).

## Repository Status

This repository is public for closed testing and release preparation.

Closed testing notes are in [docs/release/closed-testing-guide.md](docs/release/closed-testing-guide.md).
Tester invitation text is in
[play-store/testing/closed-test-invitation.txt](play-store/testing/closed-test-invitation.txt).

## License

Apache License 2.0. See [LICENSE](LICENSE).

## Japanese

Japanese documentation: [README.ja.md](README.ja.md)
