# MdLite Reader

MdLite Reader is a lightweight Android Markdown viewer.

It is designed to be fast, local-first, and privacy-respecting.

## Principles

- No ads
- No tracking
- No login
- No network permission
- No personal information in logs
- Local Markdown viewing

## Status

Early development. The repository should remain private until the first release readiness checklist is complete.

## Build

```sh
cd ~/AndroidDev
. ./env.sh
cd projects/mdlite-reader
./build.sh
```

The generated APK is:

```text
app-debug.apk
```

## Test

```sh
./test.sh
```

The current test script builds the APK, verifies signing, and checks that the APK does not request the `INTERNET` permission.

## License

Apache License 2.0. See [LICENSE](LICENSE).

## Japanese

日本語版: [README.ja.md](README.ja.md)
