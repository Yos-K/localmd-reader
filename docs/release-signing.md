# Release Signing

This document defines the production signing policy for MdLite Reader.

## Policy

- Production signing keys must never be committed to the repository.
- Production signing keys must live outside the project directory.
- Signing passwords must not be written to tracked files, shell history, logs, or documentation.
- Release builds must pass `./test.sh` before signing.
- Release artifacts must pass `scripts/check-release-basics.sh`.
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
export MDLITE_RELEASE_KEYSTORE="$HOME/AndroidDev/keys/mdlite-reader-release.jks"
export MDLITE_RELEASE_KEY_ALIAS="mdlite-release"
printf "Keystore password: "
stty -echo
read -r MDLITE_RELEASE_STORE_PASS
stty echo
printf "\nKey password: "
stty -echo
read -r MDLITE_RELEASE_KEY_PASS
stty echo
printf "\n"
export MDLITE_RELEASE_STORE_PASS
export MDLITE_RELEASE_KEY_PASS
scripts/build-release-apk.sh
unset MDLITE_RELEASE_STORE_PASS
unset MDLITE_RELEASE_KEY_PASS
```

The default output is:

```text
build/release/mdlite-reader-0.1.0-release.apk
```

## Play Store Note

The current lightweight build creates APK files. Google Play may require an
Android App Bundle for new apps, so AAB output must be added before Play Store
production submission if the Play Console requires it.
