#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"

assert_contains() {
  label="$1"
  pattern="$2"
  file="$3"
  grep -F "$pattern" "$file" >/dev/null || {
    echo "FAIL: $label is missing required target API policy configuration in ${file#"$ROOT/"}" >&2
    return 1
  }
}

assert_contains "Gradle compile SDK" 'compileSdk 36' "$ROOT/app/build.gradle"
assert_contains "Gradle target SDK" 'targetSdk 36' "$ROOT/app/build.gradle"
assert_contains "API 36 Robolectric support" 'org.robolectric:robolectric:4.16' "$ROOT/app/build.gradle"
assert_contains "source manifest target SDK" 'android:targetSdkVersion="36"' "$ROOT/src/main/AndroidManifest.xml"
assert_contains "Termux-compatible project platform" 'android-33' "$ROOT/env.project.sh"
assert_contains "Termux-compatible debug build platform" 'ANDROID_PLATFORM="${ANDROID_PLATFORM:-android-33}"' "$ROOT/build.sh"
assert_contains "Termux-compatible release APK platform" 'ANDROID_PLATFORM="${ANDROID_PLATFORM:-android-33}"' "$ROOT/scripts/build-release-apk.sh"
assert_contains "Termux-compatible release AAB platform" 'ANDROID_PLATFORM="${ANDROID_PLATFORM:-android-33}"' "$ROOT/scripts/build-release-aab.sh"
assert_contains "CI SDK installation" '"platforms;android-36"' "$ROOT/.github/workflows/ci.yml"
assert_contains "API 36 Robolectric runtime" "java-version: '21'" "$ROOT/.github/workflows/ci.yml"
assert_contains "CI manual build platform" 'ANDROID_PLATFORM: android-36' "$ROOT/.github/workflows/ci.yml"
assert_contains "Play SDK installation" '"platforms;android-36"' "$ROOT/.github/workflows/play-release.yml"
assert_contains "Play manual build platform" 'ANDROID_PLATFORM: android-36' "$ROOT/.github/workflows/play-release.yml"
assert_contains "Android 16 device smoke" 'api-level: 36' "$ROOT/.github/workflows/device-smoke.yml"

echo "target-api-policy: release builds target API 36; Termux keeps its compatible compile platform."
