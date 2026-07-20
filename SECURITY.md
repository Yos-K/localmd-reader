# Security Policy

LocalMD Reader minimizes permissions and keeps Markdown rendering local.

## Supported Versions

LocalMD Reader is in early private development. Security fixes are applied to the
current `main` branch until the first public release is prepared.

## Reporting A Vulnerability

During private development, report security issues directly to the maintainer.

When the repository becomes public, this section should be updated with a
dedicated reporting contact or GitHub Security Advisory instructions.

## Security Principles

- Request only the permissions needed to open user-selected files
- Do not request `INTERNET` permission in v0.1.0
- Do not include ads, analytics, or automatic crash reporting
- Do not log personal information
- Do not store production signing keys in the repository
- Keep Markdown parsing separate from Android UI code

## File Access

LocalMD Reader uses Android file access flows for user-selected documents.

The app must not request broad storage permissions such as
`MANAGE_EXTERNAL_STORAGE`.

## WebView Rendering

The WebView is used only as a local display surface.

Required settings:

- JavaScript disabled
- DOM storage disabled
- Database disabled
- File access disabled
- Content access disabled

Markdown raw HTML is escaped before rendering.

## Links

Only HTTP and HTTPS Markdown links are rendered as clickable links.

Unsupported link schemes, including `javascript:`, must not become clickable
anchors.

## Release Checks

Before release:

- Run `./test.sh`
- Verify the APK does not request `android.permission.INTERNET`
- Verify WebView safety tests still pass
- Verify no production signing keys are committed
- Verify privacy documents match actual app behavior

## Japanese

Japanese security policy: [SECURITY.ja.md](SECURITY.ja.md)
