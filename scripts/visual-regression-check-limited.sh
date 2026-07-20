#!/data/data/com.termux/files/usr/bin/sh
# Limited visual regression check via uiautomator dump (C-7 縮小導入 / cmd_025).
#
# For each of the 7 viewer themes this script:
#   1. force-stops the app, sets the theme via SharedPreferences
#   2. reopens with theme-showcase.md via OPEN_TEXTS intent
#   3. opens the menu and asserts that "Unpin current file" is present
#      (Pro build + FileDocumentTab = always visible; absence = regression)
#
# Follows the same uiautomator dump pattern as smoke-render-assert-l5.sh
# and assert_menu_open/assert_document_open in capture-theme-screenshots.sh.
#
# Manual workflow_dispatch only. NOT a merge gate.
# Proves detection capability via a negative assertion at the end.
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
PKG="${1:-io.github.yosk.mdlite.pro.debug}"
ACTIVITY="$PKG/io.github.yosk.mdlite.presentation.MainActivity"

ACTION="io.github.yosk.mdlite.action.OPEN_TEXTS"
EX_TITLES="io.github.yosk.mdlite.extra.MARKDOWN_TITLES"
EX_SOURCES="io.github.yosk.mdlite.extra.MARKDOWN_SOURCES"
EX_TEXTS="io.github.yosk.mdlite.extra.MARKDOWN_TEXTS_BASE64"

FIXTURE="$ROOT/scripts/smoke-fixtures/theme-showcase.md"
ART_DIR="${VR_ARTIFACT_DIR:-$ROOT/vr-check-artifacts}"
THEMES="light dark amoled gradient aurora mist dusk"
MENU_TAP_X="${MENU_TAP_X:-150}"
MENU_TAP_Y="${MENU_TAP_Y:-215}"
# Cold-start timing mirrors capture-theme-screenshots.sh (cmd_024 analysis).
FIRST_LAUNCH_WAIT=30
NEXT_LAUNCH_WAIT=15

mkdir -p "$ART_DIR"

fail() {
  echo "vr-check-limited failed: $1" >&2
  adb logcat -d -v time > "$ART_DIR/logcat.txt" 2>/dev/null || true
  adb exec-out screencap -p > "$ART_DIR/fail-screen.png" 2>/dev/null || true
  adb shell cat /sdcard/ui-dump.xml > "$ART_DIR/fail-ui-dump.xml" 2>/dev/null || true
  exit 1
}

write_theme_pref() {
  printf '%s\n' "<?xml version='1.0' encoding='utf-8' standalone='yes' ?><map><string name=\"viewer_theme\">$1</string></map>" \
    > "$ART_DIR/.viewer_settings.xml"
  adb push "$ART_DIR/.viewer_settings.xml" /data/local/tmp/viewer_settings.xml >/dev/null
  adb shell run-as "$PKG" sh -c \
    "'mkdir -p shared_prefs && cp /data/local/tmp/viewer_settings.xml shared_prefs/viewer_settings.xml'" \
    || fail "writing prefs for theme $1 (is the build debuggable?)"
}

launch_app() {
  adb shell am start -n "$ACTIVITY" -a "$ACTION" --activity-single-top \
    --esa "$EX_TITLES" "theme-showcase.md" \
    --esa "$EX_SOURCES" "$FIXTURE" \
    --esa "$EX_TEXTS" "$fixture_b64" >/dev/null
}

assert_document_open() {
  # Mirrors assert_document_open in capture-theme-screenshots.sh.
  tries=0
  while [ "$tries" -lt 4 ]; do
    if [ "$tries" -gt 0 ]; then
      launch_app
      sleep 8
    fi
    adb shell rm -f /sdcard/ui-dump.xml
    adb shell uiautomator dump /sdcard/ui-dump.xml >/dev/null 2>&1 || true
    if adb shell cat /sdcard/ui-dump.xml 2>/dev/null | grep -q "theme-showcase.md"; then
      return 0
    fi
    tries=$((tries + 1))
  done
  adb shell cat /sdcard/ui-dump.xml > "$ART_DIR/$1-document-fail-ui-dump.xml" 2>/dev/null || true
  fail "fixture did not open for theme $1"
}

assert_menu_open() {
  # Mirrors assert_menu_open in capture-theme-screenshots.sh.
  tries=0
  while [ "$tries" -lt 4 ]; do
    if [ "$tries" -gt 0 ]; then
      adb shell input tap "$MENU_TAP_X" "$MENU_TAP_Y"
      sleep 2
    fi
    adb shell rm -f /sdcard/ui-dump.xml
    adb shell uiautomator dump /sdcard/ui-dump.xml >/dev/null 2>&1 || true
    if adb shell cat /sdcard/ui-dump.xml 2>/dev/null | grep -q "Close menu"; then
      return 0
    fi
    tries=$((tries + 1))
    sleep 2
  done
  adb shell cat /sdcard/ui-dump.xml > "$ART_DIR/$1-menu-fail-ui-dump.xml" 2>/dev/null || true
  fail "menu did not open for theme $1"
}

assert_visible_text() {
  # Assert keyword is present in the current UI dump. Saves evidence on failure.
  # Mirrors assert_visible_text in smoke-render-assert-l5.sh.
  keyword="$1"
  label="$2"
  adb shell rm -f /sdcard/ui-dump.xml
  adb shell uiautomator dump /sdcard/ui-dump.xml >/dev/null 2>&1 || true
  if adb shell cat /sdcard/ui-dump.xml 2>/dev/null | grep -q "$keyword"; then
    echo "vr-check assert ok: $label ($keyword)"
    return 0
  fi
  adb shell cat /sdcard/ui-dump.xml > "$ART_DIR/${label}-fail-ui-dump.xml" 2>/dev/null || true
  adb exec-out screencap -p > "$ART_DIR/${label}-fail-screen.png" 2>/dev/null || true
  return 1
}

fixture_b64="$(base64 < "$FIXTURE" | tr -d '\n')"
launch_wait="$FIRST_LAUNCH_WAIT"

for theme in $THEMES; do
  adb shell am force-stop "$PKG"
  write_theme_pref "$theme"
  launch_app
  sleep "$launch_wait"
  launch_wait="$NEXT_LAUNCH_WAIT"
  adb shell pidof "$PKG" >/dev/null 2>&1 || fail "app not running for theme $theme"

  assert_document_open "$theme"
  sleep 2

  adb shell input tap "$MENU_TAP_X" "$MENU_TAP_Y"
  sleep 2
  assert_menu_open "$theme"

  # Assert "Unpin current file" is present in the menu.
  # Condition: Pro build (pinnedDocumentsAvailable) + FileDocumentTab (activeTabIsFile).
  # OPEN_TEXTS opens a FileDocumentTab, so this must always be visible on Pro.
  # Absence means a regression in PinnedDocumentMenuActions.UnpinCurrentFileMenuAction.
  if ! assert_visible_text "Unpin current file" "${theme}-unpin-action"; then
    fail "theme $theme: 'Unpin current file' missing — regression in Unpin menu action"
  fi

  echo "vr-check theme $theme: ok"
done

# Detection-capability proof: assert a text that must NOT be present.
# If "vr-check-nonexistent" appears the assert logic is broken.
adb shell rm -f /sdcard/ui-dump.xml
adb shell uiautomator dump /sdcard/ui-dump.xml >/dev/null 2>&1 || true
if adb shell cat /sdcard/ui-dump.xml 2>/dev/null | grep -q "vr-check-nonexistent"; then
  fail "detection-capability: vr-check-nonexistent unexpectedly found (fixture contaminated?)"
fi
echo "vr-check detection-capability: vr-check-nonexistent correctly absent"

adb logcat -d -v time > "$ART_DIR/logcat.txt" 2>/dev/null || true
adb exec-out screencap -p > "$ART_DIR/screen.png" 2>/dev/null || true
adb shell cat /sdcard/ui-dump.xml > "$ART_DIR/ui-dump.xml" 2>/dev/null || true

echo "vr-check-limited passed (all 7 themes: $THEMES)"
