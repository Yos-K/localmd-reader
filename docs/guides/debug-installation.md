# Debug Installation

Google Play installs are signed with the Play app signing certificate.
Local debug APKs are signed with the repository debug keystore. Android does not
allow an app to be updated by another APK with a different signing certificate
when both APKs use the same package name.

To avoid that conflict, `build.sh` builds the local debug APK with a development
package name:

```text
io.github.yosk.mdlite.debug
```

The debug app label is:

```text
LocalMD Reader Dev
```

This lets the Play-installed app and the local debug app coexist on the same
device.

## Build

```sh
./build.sh
```

Output:

```text
app-debug.apk
```

## Test the Free Build Locally

Build a local APK with Pro features disabled:

```sh
scripts/build-free-debug-apk.sh
```

Output:

```text
/sdcard/Download/localmd-reader-free-debug.apk
```

Package name:

```text
io.github.yosk.mdlite.free.debug
```

App label:

```text
LocalMD Reader Free Dev
```

This APK keeps Pro features disabled and can coexist with the Google Play app
and the Pro debug APK on the same device.

## Test the Pro Build Locally

Build a local APK with Pro features enabled:

```sh
scripts/build-pro-debug-apk.sh
```

Output:

```text
/sdcard/Download/localmd-reader-pro-debug.apk
```

Package name:

```text
io.github.yosk.mdlite.pro.debug
```

App label:

```text
LocalMD Reader Pro Dev
```

## Optional Overrides

The debug package and label can be changed for local experiments:

```sh
MDLITE_DEBUG_PACKAGE=io.github.yosk.mdlite.local \
MDLITE_DEBUG_APP_NAME="LocalMD Reader Local" \
./build.sh
```

Do not use these debug package names for Google Play releases.

Release scripts continue to use the production package name:

```text
io.github.yosk.mdlite
```
