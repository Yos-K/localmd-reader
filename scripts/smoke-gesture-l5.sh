#!/data/data/com.termux/files/usr/bin/sh
# L5 gesture smoke: drives the real touch pipeline on an emulator and asserts
# gesture outcomes via UIAutomator UI dump. Gestures break most often when
# unrelated features land in MainActivity (touch dispatch order is shared
# state), so this runs automatically on PRs that touch gesture source files —
# but stays flaky-tolerant and is NOT a required PR check.
#
# Probes (Pro debug build — custom gesture shortcuts are a Pro feature):
#   G1 edge swipe right (from <=24dp edge, >=72dp) opens the menu
#   G2 swipe left (>=72dp) inside the open menu closes it
#   G3 double tap bound to open_menu opens the menu (binding via prefs)
#   G4 left chevron (< stroke) bound to next_tab switches the active tab
#
# G4 uses `input motionevent` (multi-segment path): directional "swipe"
# shortcuts are chevron strokes (DirectionalGesturePath), which a straight
# `input swipe` can never match. G3 issues two taps in one adb shell call;
# the double-tap timeout (300ms) makes this the flakiest probe — it retries,
# and persistent failure here should be read as harness timing first, app
# regression second (check the screen/ui-dump artifacts).
#
# ANR dialog suppression (hide_error_dialogs 1) AND 3-button navigation
# (cmd overlay enable ...navbar.threebutton) must be set by the caller:
# under gesture navigation the system BACK gesture intercepts left-edge
# swipes and exits the app before it sees the touch (observed on run
# 27286076577 — the fail screenshot showed the launcher, not the app).
# Detection-capability proof: the nonexistent-text check at the bottom.
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
PKG="${1:-io.github.yosk.mdlite.pro.debug}"
ACTIVITY="$PKG/io.github.yosk.mdlite.presentation.MainActivity"

ACTION="io.github.yosk.mdlite.action.OPEN_TEXTS"
EX_TITLES="io.github.yosk.mdlite.extra.MARKDOWN_TITLES"
EX_SOURCES="io.github.yosk.mdlite.extra.MARKDOWN_SOURCES"
EX_TEXTS="io.github.yosk.mdlite.extra.MARKDOWN_TEXTS_BASE64"

FIXTURE_ONE="$ROOT/scripts/smoke-fixtures/smoke-one.md"
FIXTURE_TWO="$ROOT/scripts/smoke-fixtures/smoke-two.md"
ART_DIR="${SMOKE_ARTIFACT_DIR:-$ROOT/smoke-artifacts}"
mkdir -p "$ART_DIR"

# Cold-start timing mirrors visual-regression-check-limited.sh (cmd_024).
FIRST_LAUNCH_WAIT=30
NEXT_LAUNCH_WAIT=15

# pixel_6 (1080x2400, 420dpi): density 2.625 -> dp(24)=63px, dp(72)=189px.
# Menu open: DOWN at x<=63, UP at delta>=+189. Close: UP at delta<=-189.
MENU_OPEN_SWIPE="15 1200 500 1200 150"
MENU_CLOSE_SWIPE="600 1200 200 1200 150"
DOUBLE_TAP_X=540
DOUBLE_TAP_Y=1300

fail() {
  echo "gesture-smoke failed: $1" >&2
  echo "gesture-smoke debug: current UI text dump follows" >&2
  dump_ui | sed -n 's/.*text="\([^"]*\)".*/  text: \1/p' | sed -n '1,80p' >&2 || true
  echo "gesture-smoke debug: viewer_settings.xml follows" >&2
  adb shell run-as "$PKG" cat shared_prefs/viewer_settings.xml >&2 2>/dev/null || true
  adb logcat -d -v time > "$ART_DIR/logcat.txt" 2>/dev/null || true
  adb exec-out screencap -p > "$ART_DIR/fail-screen.png" 2>/dev/null || true
  adb shell cat /sdcard/ui-dump.xml > "$ART_DIR/fail-ui-dump.xml" 2>/dev/null || true
  exit 1
}

b64() {
  base64 < "$1" | tr -d '\n'
}

dump_ui() {
  adb shell rm -f /sdcard/ui-dump.xml
  adb shell uiautomator dump /sdcard/ui-dump.xml >/dev/null 2>&1 || true
  adb shell cat /sdcard/ui-dump.xml 2>/dev/null
}

ui_has() {
  dump_ui | grep -q "$1"
}

dismiss_pixel_launcher_anr_if_present() {
  if dump_ui | grep -q "Pixel Launcher isn't responding"; then
    # GitHub emulator cold boots can surface a launcher ANR over the app.
    # It is outside the app under test; dismiss it before asserting app UI.
    adb shell input tap 540 1328
    sleep 2
  fi
}

save_evidence() {
  adb shell cat /sdcard/ui-dump.xml > "$ART_DIR/$1-ui-dump.xml" 2>/dev/null || true
  adb exec-out screencap -p > "$ART_DIR/$1-screen.png" 2>/dev/null || true
}

# Retry by REDOING the gesture, never by falling back to a tap: a fallback
# tap would mask a broken gesture path and report a false green.
# $1=label  $2=gesture shell function  $3=keyword expected AFTER the gesture
assert_gesture_yields() {
  label="$1"
  gesture="$2"
  keyword="$3"
  tries=0
  while [ "$tries" -lt 3 ]; do
    "$gesture"
    sleep 3
    if ui_has "$keyword"; then
      echo "gesture-smoke ok: $label"
      return 0
    fi
    tries=$((tries + 1))
  done
  save_evidence "$label-fail"
  fail "$label: '$keyword' not visible after 3 gesture attempts"
}

# $1=label  $2=gesture  $3=keyword expected ABSENT after the gesture
assert_gesture_clears() {
  label="$1"
  gesture="$2"
  keyword="$3"
  tries=0
  while [ "$tries" -lt 3 ]; do
    "$gesture"
    sleep 3
    if ! ui_has "$keyword"; then
      echo "gesture-smoke ok: $label"
      return 0
    fi
    tries=$((tries + 1))
  done
  save_evidence "$label-fail"
  fail "$label: '$keyword' still visible after 3 gesture attempts"
}

gesture_edge_swipe_open() {
  adb shell input swipe $MENU_OPEN_SWIPE
}

gesture_swipe_close() {
  adb shell input swipe $MENU_CLOSE_SWIPE
}

gesture_double_tap() {
  # Both taps in ONE adb shell call to stay inside the 300ms double-tap
  # window; explicit DOWN/UP events are more stable than `input tap` on
  # GitHub emulator cold boots.
  adb shell "input motionevent DOWN $DOUBLE_TAP_X $DOUBLE_TAP_Y; input motionevent UP $DOUBLE_TAP_X $DOUBLE_TAP_Y; sleep 0.1; input motionevent DOWN $DOUBLE_TAP_X $DOUBLE_TAP_Y; input motionevent UP $DOUBLE_TAP_X $DOUBLE_TAP_Y"
}

gesture_left_chevron() {
  # "<" stroke: start (850,1000) -> apex (450,1400) -> end (850,1800).
  # Satisfies DirectionalGesturePath: legs 400px >= 28% of height 800,
  # base drift 0 <= 45% of width 400, apex depth 400 >= 55% of width 400.
  adb shell "input motionevent DOWN 850 1000; input motionevent MOVE 650 1200; input motionevent MOVE 450 1400; input motionevent MOVE 650 1600; input motionevent MOVE 850 1800; input motionevent UP 850 1800"
}

launch_both_fixtures() {
  adb shell am start -n "$ACTIVITY" -a "$ACTION" --activity-single-top \
    --esa "$EX_TITLES" "smoke-one.md,smoke-two.md" \
    --esa "$EX_SOURCES" "$FIXTURE_ONE,$FIXTURE_TWO" \
    --esa "$EX_TEXTS" "$fixture_one_b64,$fixture_two_b64" >/dev/null
}

assert_document_open() {
  tries=0
  while [ "$tries" -lt 4 ]; do
    dismiss_pixel_launcher_anr_if_present
    if [ "$tries" -gt 0 ]; then
      launch_both_fixtures
      sleep 8
      dismiss_pixel_launcher_anr_if_present
    fi
    if ui_has "smoke-one.md"; then
      return 0
    fi
    tries=$((tries + 1))
  done
  save_evidence "document-open-fail"
  fail "fixtures did not open ($1)"
}

write_gesture_bindings() {
  # Same run-as prefs injection as visual-regression-check-limited.sh.
  # Keys/values are the stored forms in ViewerSettingsStore /
  # GestureShortcutAction (double_tap_shortcut, swipe_left_shortcut).
  printf '%s\n' "<?xml version='1.0' encoding='utf-8' standalone='yes' ?><map><string name=\"double_tap_shortcut\">open_menu</string><string name=\"swipe_left_shortcut\">next_tab</string></map>" \
    > "$ART_DIR/.viewer_settings.xml"
  adb push "$ART_DIR/.viewer_settings.xml" /data/local/tmp/viewer_settings.xml >/dev/null
  adb shell run-as "$PKG" sh -c \
    "'mkdir -p shared_prefs && cp /data/local/tmp/viewer_settings.xml shared_prefs/viewer_settings.xml'" \
    || fail "writing gesture binding prefs (is the build debuggable?)"
  echo "gesture-smoke debug: injected double_tap_shortcut=open_menu and swipe_left_shortcut=next_tab"
}

fixture_one_b64="$(b64 "$FIXTURE_ONE")"
fixture_two_b64="$(b64 "$FIXTURE_TWO")"

# ---- Phase 1: binding-free menu gestures (defaults, no prefs) ----
adb shell am force-stop "$PKG"
launch_both_fixtures
sleep "$FIRST_LAUNCH_WAIT"
adb shell pidof "$PKG" >/dev/null 2>&1 || fail "app not running (phase 1)"
assert_document_open "phase 1"

assert_gesture_yields "G1-edge-swipe-opens-menu" gesture_edge_swipe_open "Close menu"
assert_gesture_clears "G2-swipe-left-closes-menu" gesture_swipe_close "Close menu"

# ---- Phase 2: bound shortcuts (prefs injected, app restarted) ----
adb shell am force-stop "$PKG"
write_gesture_bindings
launch_both_fixtures
sleep "$NEXT_LAUNCH_WAIT"
adb shell pidof "$PKG" >/dev/null 2>&1 || fail "app not running (phase 2)"
assert_document_open "phase 2"

assert_gesture_yields "G3-double-tap-opens-menu" gesture_double_tap "Close menu"
assert_gesture_clears "G3-cleanup-close-menu" gesture_swipe_close "Close menu"

# G4: detect which fixture body is visible, chevron-left, expect the other.
# Body headings ("Smoke Fixture One/Two") only appear for the ACTIVE tab,
# unlike tab titles which are always present in the tab bar.
if ui_has "Smoke Fixture One"; then
  expect_after_switch="Smoke Fixture Two"
elif ui_has "Smoke Fixture Two"; then
  expect_after_switch="Smoke Fixture One"
else
  save_evidence "G4-precondition-fail"
  fail "G4: neither fixture body visible before tab-switch gesture"
fi
assert_gesture_yields "G4-chevron-left-next-tab" gesture_left_chevron "$expect_after_switch"

# ---- Detection-capability proof (mirrors smoke-render-assert-l5.sh) ----
if ui_has "gesture-smoke-nonexistent"; then
  fail "detection-capability: nonexistent text unexpectedly found"
fi
echo "gesture-smoke detection-capability: nonexistent text correctly absent"

adb logcat -d -v time > "$ART_DIR/logcat.txt" 2>/dev/null || true
adb exec-out screencap -p > "$ART_DIR/screen.png" 2>/dev/null || true

echo "gesture-smoke passed (G1 edge-open / G2 swipe-close / G3 double-tap / G4 chevron next_tab)"
