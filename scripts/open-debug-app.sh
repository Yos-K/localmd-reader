#!/data/data/com.termux/files/usr/bin/sh
set -eu

am start \
  -n io.github.yosk.mdlite/.presentation.MainActivity \
  -a android.intent.action.MAIN \
  -c android.intent.category.LAUNCHER \
  --activity-single-top \
  > /dev/null

echo "Opened LocalMD Reader."
