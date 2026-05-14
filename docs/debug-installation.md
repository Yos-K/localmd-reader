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
