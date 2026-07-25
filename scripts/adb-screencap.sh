#!/data/data/com.termux/files/usr/bin/sh
set -eu

# Some physical devices expose multiple displays. Without an explicit display
# ID, screencap writes a warning into stdout and corrupts the PNG artifact.
display_id="$(adb shell dumpsys SurfaceFlinger --display-id 2>/dev/null \
    | sed -n 's/^Display \([0-9][0-9]*\).*/\1/p' | tail -n 1 | tr -d '\r')"
if [ -n "$display_id" ]; then
    adb exec-out screencap -d "$display_id" -p
else
    adb exec-out screencap -p
fi
