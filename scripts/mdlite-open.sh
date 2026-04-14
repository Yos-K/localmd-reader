#!/data/data/com.termux/files/usr/bin/sh
set -eu

if [ "$#" -eq 0 ]; then
  echo "Usage: mdlite-open.sh FILE.md [FILE2.md ...]" >&2
  exit 2
fi

paths=""
for input_path in "$@"; do
  case "$input_path" in
    /*) absolute_path="$input_path" ;;
    *) absolute_path="$(pwd)/$input_path" ;;
  esac

  if [ ! -f "$absolute_path" ]; then
    echo "Not a file: $absolute_path" >&2
    exit 2
  fi

  if [ -z "$paths" ]; then
    paths="$absolute_path"
  else
    paths="$paths
$absolute_path"
  fi
done

printf '%s\n' "$paths" | while IFS= read -r markdown_path; do
  termux-open --view --content-type text/markdown "$markdown_path"
done
