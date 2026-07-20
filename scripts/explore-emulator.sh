#!/data/data/com.termux/files/usr/bin/sh
# explore-emulator.sh — UI 探索セッションの実行装置（advisory・ゲートではない）
#
# なぜ: 純JVM probe は domain/viewer/file 層しか触れない。UI のバグ
# （操作順序依存・想定外状態・クラッシュ）は実際に操作してみないと
# 見つからない。本スクリプトはチャーター駆動の UI 探索を再現可能な形で
# 実行し、分析可能な証跡を残す。run そのものは足場（使い捨て）であり、
# 発見だけを蒸留する——ライフサイクルは exploratory-testing.md 参照。
#
# モード:
#   monkey <seed> <events>  シード付きランダムUIイベント（再現可能）。
#                           パッケージ固定・システムキー除外。crash/ANR を検出
#   ops <file>              操作列ファイルを1行ずつ実行し、各ステップで
#                           スクリーンショット + UI ダンプを採取
#
# ops ファイルの語彙（# 行と空行は無視）:
#   fixture <path>          Markdown fixture を OPEN_TEXTS で開く
#   tap <x> <y>             タップ
#   swipe <x1> <y1> <x2> <y2> [ms]
#   chevron-left <cx> <cy> <size>   "<" ストローク（他方向: -right/-up/-down）
#   key <BACK|HOME|MENU|...>        keyevent
#   text <string>           テキスト入力
#   wait <sec>              待機
#   shot <label>            明示的な証跡採取（各ステップ後にも自動採取）
#
# 使い方（CI から）:
#   SMOKE_ARTIFACT_DIR=... sh scripts/explore-emulator.sh monkey 12345 2000 [pkg]
#   SMOKE_ARTIFACT_DIR=... sh scripts/explore-emulator.sh ops scripts/exploration-ops/<charter>.ops [pkg]
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
MODE="${1:?usage: explore-emulator.sh monkey <seed> <events> [pkg] | ops <file> [pkg]}"
ART_DIR="${SMOKE_ARTIFACT_DIR:-$ROOT/exploration-artifacts}"
mkdir -p "$ART_DIR"

ACTION="io.github.yosk.mdlite.action.OPEN_TEXTS"
EX_TITLES="io.github.yosk.mdlite.extra.MARKDOWN_TITLES"
EX_SOURCES="io.github.yosk.mdlite.extra.MARKDOWN_SOURCES"
EX_TEXTS="io.github.yosk.mdlite.extra.MARKDOWN_TEXTS_BASE64"

step=0

evidence() { # $1=label — スクショ + UIダンプを連番で採取
  step=$((step + 1))
  tag=$(printf '%03d-%s' "$step" "$1")
  adb exec-out screencap -p > "$ART_DIR/$tag.png" 2>/dev/null || true
  adb shell rm -f /sdcard/ui-dump.xml
  adb shell uiautomator dump /sdcard/ui-dump.xml >/dev/null 2>&1 || true
  adb shell cat /sdcard/ui-dump.xml > "$ART_DIR/$tag.xml" 2>/dev/null || true
}

finish_evidence() {
  adb logcat -d -v time > "$ART_DIR/logcat.txt" 2>/dev/null || true
  # AndroidRuntime の "FATAL EXCEPTION" 行自体にパッケージ名は載らない。
  # パッケージは直後の "Process: <pkg>, PID:" 行に出るため、クラッシュブロック
  # （FATAL から数行）を Process 行で相関させて自アプリのクラッシュだけを拾う。
  # 行単独の grep だと自アプリのクラッシュを取りこぼし偽成功を返す。
  if adb logcat -d -v brief 2>/dev/null | grep -A3 -F "FATAL EXCEPTION" | grep -q "Process:.*$PKG"; then
    echo "exploration: FATAL EXCEPTION for $PKG found in logcat (see artifacts)" >&2
    return 1
  fi
  return 0
}

open_fixture() { # $1=fixture path
  name="$(basename "$1")"
  b64="$(base64 < "$1" | tr -d '\n')"
  adb shell am start -n "$PKG/io.github.yosk.mdlite.presentation.MainActivity" -a "$ACTION" \
    --activity-single-top \
    --esa "$EX_TITLES" "$name" --esa "$EX_SOURCES" "$1" --esa "$EX_TEXTS" "$b64" >/dev/null
  sleep 8
}

chevron() { # $1=dir $2=cx $3=cy $4=size — 往復ストロークを motionevent で描く
  d="$1"; cx="$2"; cy="$3"; sz="$4"; h=$((sz / 2))
  case "$d" in
    left)  p="DOWN $((cx+h)) $((cy-h));MOVE $((cx-h)) $cy;UP $((cx+h)) $((cy+h))" ;;
    right) p="DOWN $((cx-h)) $((cy-h));MOVE $((cx+h)) $cy;UP $((cx-h)) $((cy+h))" ;;
    up)    p="DOWN $((cx-h)) $((cy+h));MOVE $cx $((cy-h));UP $((cx+h)) $((cy+h))" ;;
    down)  p="DOWN $((cx-h)) $((cy-h));MOVE $cx $((cy+h));UP $((cx+h)) $((cy-h))" ;;
  esac
  old_ifs=$IFS; IFS=';'
  for ev in $p; do adb shell input motionevent $ev; done
  IFS=$old_ifs
}

case "$MODE" in
  monkey)
    SEED="${2:?seed required}"; EVENTS="${3:?event count required}"; PKG="${4:-io.github.yosk.mdlite.pro.debug}"
    echo "exploration(monkey): pkg=$PKG seed=$SEED events=$EVENTS"
    # 意味のある状態から始める: fixture を開いてから嵐を起こす
    open_fixture "$ROOT/scripts/smoke-fixtures/smoke-render.md"
    evidence "before-monkey"
    status=0
    adb shell monkey -p "$PKG" --throttle 80 --pct-syskeys 0 -s "$SEED" -v "$EVENTS" \
      > "$ART_DIR/monkey-output.txt" 2>&1 || status=$?
    evidence "after-monkey"
    crashes=$(grep -c "^// CRASH" "$ART_DIR/monkey-output.txt" 2>/dev/null) || crashes=0
    anrs=$(grep -c "^// NOT RESPONDING" "$ART_DIR/monkey-output.txt" 2>/dev/null) || anrs=0
    echo "monkey done: exit=$status crashes=$crashes anrs=$anrs (seed=$SEED で再現可能)"
    finish_evidence || status=1
    if [ "$status" != "0" ] || [ "$crashes" != "0" ] || [ "$anrs" != "0" ]; then
      echo "exploration(monkey): findings present — triage the artifacts (seed=$SEED)" >&2
      exit 1
    fi
    ;;
  ops)
    OPS_FILE="${2:?ops file required}"; PKG="${3:-io.github.yosk.mdlite.pro.debug}"
    [ -f "$OPS_FILE" ] || { echo "ops file not found: $OPS_FILE" >&2; exit 2; }
    echo "exploration(ops): pkg=$PKG file=$OPS_FILE"
    lineno=0
    while IFS= read -r line || [ -n "$line" ]; do
      lineno=$((lineno + 1))
      case "$line" in ''|'#'*) continue ;; esac
      set -- $line
      op="$1"
      echo "  L$lineno: $line"
      case "$op" in
        fixture) open_fixture "$ROOT/$2" ;;
        tap)     adb shell input tap "$2" "$3"; sleep 2 ;;
        swipe)   adb shell input swipe "$2" "$3" "$4" "$5" "${6:-150}"; sleep 2 ;;
        chevron-left)  chevron left  "$2" "$3" "$4"; sleep 2 ;;
        chevron-right) chevron right "$2" "$3" "$4"; sleep 2 ;;
        chevron-up)    chevron up    "$2" "$3" "$4"; sleep 2 ;;
        chevron-down)  chevron down  "$2" "$3" "$4"; sleep 2 ;;
        key)     adb shell input keyevent "KEYCODE_$2"; sleep 2 ;;
        text)    shift; adb shell input text "$*"; sleep 1 ;;
        wait)    sleep "$2" ;;
        shot)    evidence "$2"; continue ;;
        *) echo "  unknown op '$op' (L$lineno) — skip" >&2; continue ;;
      esac
      evidence "$(printf 'L%02d-%s' "$lineno" "$op")"
    done < "$OPS_FILE"
    finish_evidence
    echo "exploration(ops): $step evidence steps captured in $ART_DIR"
    ;;
  *) echo "unknown mode: $MODE" >&2; exit 2 ;;
esac
echo "explore-emulator done"
