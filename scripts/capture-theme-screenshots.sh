#!/data/data/com.termux/files/usr/bin/sh
# Capture per-theme screenshots from a connected emulator/device via adb, as
# review evidence for visual PRs (issue #77 family).
#
# For each of the 7 viewer themes this script:
#   1. force-stops the app and writes the theme into the app's
#      SharedPreferences (run-as works because the build is debuggable),
#   2. relaunches the app with the theme-showcase fixture via the same
#      OPEN_TEXTS intent as scripts/emulator-smoke.sh,
#   3. captures the document view, then opens the menu with an edge swipe
#      and captures it again.
#
# Use the proPreview debug package (default) so Pro-gated themes render;
# the free build would fall back to light for amoled/gradient/aurora/mist/dusk.
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
PKG="${1:-io.github.yosk.mdlite.pro.debug}"
ACTIVITY="$PKG/io.github.yosk.mdlite.presentation.MainActivity"

ACTION="io.github.yosk.mdlite.action.OPEN_TEXTS"
EX_TITLES="io.github.yosk.mdlite.extra.MARKDOWN_TITLES"
EX_SOURCES="io.github.yosk.mdlite.extra.MARKDOWN_SOURCES"
EX_TEXTS="io.github.yosk.mdlite.extra.MARKDOWN_TEXTS_BASE64"

FIXTURE="$ROOT/scripts/smoke-fixtures/theme-showcase.md"
ART_DIR="${THEME_SHOT_DIR:-$ROOT/theme-screenshots}"
THEMES="light dark amoled gradient aurora mist dusk"

mkdir -p "$ART_DIR"

fail() {
  echo "Theme screenshot capture failed: $1" >&2
  adb logcat -d -v time > "$ART_DIR/logcat.txt" 2>/dev/null || true
  exit 1
}

write_theme_pref() {
  # write_theme_pref <theme>: stage the prefs file via /data/local/tmp because
  # quoting XML through `adb shell run-as` directly is fragile.
  printf '%s\n' "<?xml version='1.0' encoding='utf-8' standalone='yes' ?><map><string name=\"viewer_theme\">$1</string></map>" \
    > "$ART_DIR/.viewer_settings.xml"
  adb push "$ART_DIR/.viewer_settings.xml" /data/local/tmp/viewer_settings.xml >/dev/null
  adb shell run-as "$PKG" sh -c \
    "'mkdir -p shared_prefs && cp /data/local/tmp/viewer_settings.xml shared_prefs/viewer_settings.xml'" \
    || fail "writing prefs for theme $1 (is the build debuggable?)"
}

# Toolbar Menu button center on the pixel_6 profile (1080x2400). The capture
# environment pins this profile in theme-screenshots.yml, so fixed coordinates
# are deterministic there.
MENU_TAP_X="${MENU_TAP_X:-150}"
MENU_TAP_Y="${MENU_TAP_Y:-215}"

# Proven-by-run-1 timing: dumpsys-based foreground polls failed twice on the
# CI emulator (logcat showed the app displayed in ~7s while both the
# mResumedActivity and mCurrentFocus greps never matched), whereas plain
# sleeps captured all 7 themes. Cold start on the software renderer needs the
# longer first wait; later launches reuse warmed caches.
# 30s (was 25): cmd_024 flaky analysis showed "app did not reach foreground"
# failures on cold start; the extra 5s covers CI runner variance.
FIRST_LAUNCH_WAIT=30
NEXT_LAUNCH_WAIT=15
launch_wait="$FIRST_LAUNCH_WAIT"

wait_for_foreground() {
  sleep "$launch_wait"
  launch_wait="$NEXT_LAUNCH_WAIT"
  adb shell pidof "$PKG" >/dev/null 2>&1 || fail "app not running for theme $1"
}

dump_focus_diagnostics() {
  # Evidence for fixing the foreground poll later: record what dumpsys
  # actually prints about focus on this emulator image.
  {
    echo "== dumpsys window (focus lines) =="
    adb shell dumpsys window 2>/dev/null | grep -inE "focus" | head -20
    echo "== dumpsys activity activities (resumed lines) =="
    adb shell dumpsys activity activities 2>/dev/null | grep -inE "resumed" | head -20
  } > "$ART_DIR/focus-diagnostics.txt" || true
}

assert_menu_open() {
  # MainActivity flips the toolbar button's contentDescription to "Close menu"
  # while the menu is open, so the UI dump proves the tap actually worked
  # instead of silently screenshotting the document again. Retry a few times
  # (animations may delay idle), and keep the dump + a screenshot as evidence
  # when it still fails.
  # cmd_024: re-tap on retry — the original tap may land while the UI is still
  # rendering (assert_document_open returning does not guarantee the layout
  # pass is complete). Three retries × re-tap covers transient layout jank.
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
  adb shell cat /sdcard/ui-dump.xml > "$ART_DIR/menu-fail-ui-dump.xml" 2>/dev/null || true
  adb exec-out screencap -p > "$ART_DIR/menu-fail-screen.png" 2>/dev/null || true
  fail "menu did not open for theme $1 (evidence: menu-fail-ui-dump.xml / menu-fail-screen.png)"
}

launch_app() {
  adb shell am start -n "$ACTIVITY" -a "$ACTION" --activity-single-top \
    --esa "$EX_TITLES" "theme-showcase.md" \
    --esa "$EX_SOURCES" "$FIXTURE" \
    --esa "$EX_TEXTS" "$fixture_b64" >/dev/null
}

assert_document_open() {
  # The OPEN_TEXTS intent is not honored on the very first launch after the
  # APK install (issue #97: two consecutive CI runs captured the welcome
  # screen for the first theme while themes 2-7 all opened the fixture).
  # Assert the fixture tab is actually on screen; if not, re-send the intent
  # now that the app is warm (--activity-single-top delivers it through
  # onNewIntent, which is the path every later theme already exercises).
  # Re-send at the top of every retry (not the bottom) so each re-sent intent
  # is always followed by a dump+check before the function can fail: a
  # trailing un-checked re-send would make the job fail flaky-late and save
  # evidence from after the document opened (Codex review on #98).
  # cmd_024: tries 3→4 and retry sleep 5→8 — run 27090846408 showed "fixture
  # did not open" on cold-start because the layout was still settling after
  # wait_for_foreground returned; longer retry wait covers that gap.
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
  adb shell cat /sdcard/ui-dump.xml > "$ART_DIR/document-fail-ui-dump.xml" 2>/dev/null || true
  adb exec-out screencap -p > "$ART_DIR/document-fail-screen.png" 2>/dev/null || true
  fail "fixture did not open for theme $1 (evidence: document-fail-ui-dump.xml / document-fail-screen.png)"
}

fixture_b64="$(base64 < "$FIXTURE" | tr -d '\n')"

for theme in $THEMES; do
  adb shell am force-stop "$PKG"
  write_theme_pref "$theme"
  launch_app
  wait_for_foreground "$theme"
  [ -f "$ART_DIR/focus-diagnostics.txt" ] || dump_focus_diagnostics
  assert_document_open "$theme"
  # cmd_024: brief settle wait after the fixture is confirmed on screen.
  # assert_document_open polls uiautomator dump which is available before
  # the final layout pass completes; without this pause the menu tap can
  # land during an ongoing transition and be swallowed.
  sleep 2
  adb exec-out screencap -p > "$ART_DIR/$theme-document.png" || fail "screencap for theme $theme"

  # Open the menu by tapping the toolbar Menu button. An edge swipe would be
  # eaten by the system Back gesture on gesture-nav devices (verified on the
  # API 35 emulator: it backgrounds the app and we screenshot the launcher).
  adb shell input tap "$MENU_TAP_X" "$MENU_TAP_Y"
  sleep 2
  assert_menu_open "$theme"
  adb exec-out screencap -p > "$ART_DIR/$theme-menu.png" || fail "menu screencap for theme $theme"
  echo "captured: $theme"
done

# Menu animation evidence (#76): record an open -> close cycle on the light
# theme so motion (slide + scrim) can be reviewed, not just end states.
adb shell am force-stop "$PKG"
write_theme_pref light
launch_app
wait_for_foreground "menu-animation"
assert_document_open "menu-animation"
adb shell screenrecord --time-limit 8 /data/local/tmp/menu-animation.mp4 &
record_pid=$!
sleep 1
adb shell input tap "$MENU_TAP_X" "$MENU_TAP_Y"
sleep 2
adb shell input tap 900 1200
sleep 2
wait "$record_pid" || true
adb pull /data/local/tmp/menu-animation.mp4 "$ART_DIR/menu-animation.mp4" >/dev/null \
  || echo "warning: menu animation recording could not be pulled" >&2

rm -f "$ART_DIR/.viewer_settings.xml"
echo "Theme screenshots written to $ART_DIR"
