#!/data/data/com.termux/files/usr/bin/sh
set -eu

package_name="${MDLITE_PACKAGE:-io.github.yosk.mdlite}"
activity_name="io.github.yosk.mdlite.presentation.MainActivity"
documents_base64=""

if [ "$#" -eq 0 ]; then
  echo "Usage: mdlite-open.sh FILE.md [FILE2.md ...]" >&2
  exit 2
fi

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
  encoded_title=$(printf '%s' "$title" | base64 -w 0)
  encoded_source=$(printf '%s' "$absolute_path" | base64 -w 0)
  encoded_text=$(base64 -w 0 "$absolute_path")
  encoded_document="$encoded_title:$encoded_source:$encoded_text"
  if [ -z "$documents_base64" ]; then
    documents_base64="$encoded_document"
  else
    documents_base64="$documents_base64,$encoded_document"
  fi
done

am start \
  -n "$package_name/$activity_name" \
  -a io.github.yosk.mdlite.action.OPEN_TEXTS_BASE64 \
  --activity-single-top \
  --es io.github.yosk.mdlite.extra.MARKDOWN_DOCUMENTS_BASE64 "$documents_base64" \
  > /dev/null
