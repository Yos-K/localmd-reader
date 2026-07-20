#!/data/data/com.termux/files/usr/bin/sh
set -eu

if [ "$#" -eq 0 ]; then
  echo "Usage: mdlite-open.sh FILE.md [FILE2.md ...]" >&2
  exit 2
fi

paths=""
titles=""
sources=""
texts=""
for input_path in "$@"; do
  case "$input_path" in
    /*) absolute_path="$input_path" ;;
    *) absolute_path="$(pwd)/$input_path" ;;
  esac

  if [ ! -f "$absolute_path" ]; then
    echo "Not a file: $absolute_path" >&2
    exit 2
  fi

  title=$(basename "$absolute_path")
  escaped_title=$(printf '%s' "$title" | sed 's/\\/\\\\/g; s/,/\\,/g')
  escaped_source=$(printf '%s' "$absolute_path" | sed 's/\\/\\\\/g; s/,/\\,/g')
  encoded_text=$(base64 -w 0 "$absolute_path")

  if [ -z "$paths" ]; then
    paths="$absolute_path"
    titles="$escaped_title"
    sources="$escaped_source"
    texts="$encoded_text"
  else
    paths="$paths
$absolute_path"
    titles="$titles,$escaped_title"
    sources="$sources,$escaped_source"
    texts="$texts,$encoded_text"
  fi
done

am start \
  -n io.github.yosk.mdlite/.presentation.MainActivity \
  -a io.github.yosk.mdlite.action.OPEN_TEXTS \
  --activity-single-top \
  --esa io.github.yosk.mdlite.extra.MARKDOWN_TITLES "$titles" \
  --esa io.github.yosk.mdlite.extra.MARKDOWN_SOURCES "$sources" \
  --esa io.github.yosk.mdlite.extra.MARKDOWN_TEXTS_BASE64 "$texts" \
  > /dev/null
