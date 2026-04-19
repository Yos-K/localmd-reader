# Release Signing

This document defines the production signing policy for MdLite Reader.

## Policy

- Production signing keys must never be committed to the repository.
- Production signing keys must live outside the project directory.
- Signing passwords must not be written to tracked files, shell history, logs, or documentation.
- Release builds must pass `./test.sh` before signing.
- Release APK artifacts must pass `scripts/check-release-basics.sh`.
- Release AAB artifacts must pass `bundletool validate`.
- The release key must be backed up before the first public release.

## Create The Release Keystore

Create the keystore outside the repository:

```sh
scripts/create-release-keystore.sh
```

By default this creates:

```text
~/AndroidDev/keys/mdlite-reader-release.jks
```

The default key alias is:

```text
mdlite-release
```

To choose another path or alias:

```sh
export MDLITE_RELEASE_KEYSTORE="$HOME/AndroidDev/keys/mdlite-reader-release.jks"
export MDLITE_RELEASE_KEY_ALIAS="mdlite-release"
scripts/create-release-keystore.sh
```

## Build A Signed Release APK

Run the automated checks first:

```sh
./test.sh
```

Then sign with the production key:

```sh
scripts/build-signed-release.sh apk
```

The default output is:

```text
build/release/mdlite-reader-0.1.0-release.apk
```

## Build A Signed Release AAB

Google Play release builds should use Android App Bundle output.

MdLite Reader keeps `bundletool` outside the repository. Download it to a local
tool directory and point `BUNDLETOOL_JAR` to that file.

Run the automated checks first:

```sh
./test.sh
```

Then build the signed AAB:

```sh
scripts/build-signed-release.sh aab
```

Build both signed artifacts:

```sh
scripts/build-signed-release.sh all
```

The wrapper reads the keystore password and key password without echoing them,
exports them only for the child build process, and unsets them on exit.

`jarsigner` may warn that the certificate is self-signed or has no timestamp.
That is acceptable for Android release signing. Treat `bundletool validate`
success as the bundle structure check.

The default output is:

```text
build/release/mdlite-reader-0.1.0-release.aab
```

Release staging location:

```text
~/AndroidDev/releases/mdlite-reader/v0.1.0/mdlite-reader-0.1.0-release.aab
```

Checksum file:

```text
~/AndroidDev/releases/mdlite-reader/v0.1.0/SHA256SUMS
```

## Play Store Note

The APK script remains useful for local release-style installation checks.
Use the AAB script for Play Store upload builds when the Play Console requires
Android App Bundle output.
