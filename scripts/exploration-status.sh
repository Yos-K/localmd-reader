#!/data/data/com.termux/files/usr/bin/sh
# exploration-status.sh — 探索テストループの自己操縦シグナル
#
# 探索（exploratory-testing.md）を単発の思いつきでなく継続ループとして回すため、
# 「どこを・なぜ次に探索すべきか」「探索は価値を出し続けているか」を
# セッションログと git 履歴から機械的に集計して提示する。
#
# 出力（すべて advisory。ゲートではないので常に exit 0）:
#   1. セッション台帳     — 各セッションの probe 数・発見数・振り分け実績
#   2. クラスタカバレッジ — クラスタごとの最終探索日と累積発見。未探索を明示
#   3. 変更ホットスポット — 直近30日の変更集中ファイルとクラスタ推定（ヒューリスティック）
#   4. シグナル           — 「変更が集中しているのに未探索/陳腐化」「2回連続発見ゼロ」
#
# セッションログの様式（機械可読フッター）は exploratory-testing.md を参照。
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
SESSIONS_DIR="$ROOT/docs/harness/exploration-sessions"
GLOSSARY_DIR="$ROOT/docs/domain"
SINCE_DAYS="${EXPLORATION_HOTSPOT_DAYS:-30}"
STALE_DAYS="${EXPLORATION_STALE_DAYS:-45}"

# クラスタ一覧は用語集ファイル名から導出する（用語集が探索範囲の全体集合）。
clusters() {
  for f in "$GLOSSARY_DIR"/domain-glossary-*.md; do
    basename "$f" .md | sed 's/^domain-glossary-//'
  done
}

# 変更ファイル → クラスタの推定。クラス名キーワードによるヒューリスティック
# （厳密な帰属は用語集が正典。ここでは「次の探索候補」の示唆が目的）。
guess_cluster() {
  case "$1" in
    *Gesture*|*SwipeMenu*|*SampledPoints*|*Chevron*) echo "gestures" ;;
    *Theme*|*FontSize*|*ViewerLanguage*|*ControlsPlacement*|*Appearance*) echo "appearance" ;;
    *Purchase*|*Billing*|*Entitlement*) echo "purchase" ;;
    *Tab*|*Recent*|*Pin*) echo "navigation" ;;
    *Render*|*Markdown*|*Mermaid*|*Highlight*|*Html*|*Image*) echo "rendering" ;;
    *) echo "viewer" ;;
  esac
}

field() { # $1=file $2=key → フッターの値（無ければ "-"）
  v="$(grep -E "^- $2: " "$1" 2>/dev/null | head -1 | sed "s/^- $2: //")"
  [ -n "$v" ] && echo "$v" || echo "-"
}

session_cluster() { # チャーター表の用語集参照からクラスタを特定
  grep -o 'domain-glossary-[a-z]*\.md' "$1" 2>/dev/null | head -1 \
    | sed 's/^domain-glossary-//; s/\.md$//' || true
}

echo "==================== 探索ループ status ===================="
echo ""
echo "--- 1. セッション台帳 ---"
printf '%-12s %-12s %7s %9s %22s\n' "日付" "クラスタ" "probes" "findings" "振り分け(issue/gls/assert)"
total_sessions=0
for log in "$SESSIONS_DIR"/*.md; do
  [ -e "$log" ] || continue
  total_sessions=$((total_sessions + 1))
  date_part="$(basename "$log" | cut -c1-10)"
  cluster="$(session_cluster "$log")"
  [ -n "$cluster" ] || cluster="(不明)"
  probes="$(field "$log" probes)"
  findings="$(field "$log" findings)"
  ti="$(field "$log" triage-issue)"
  tg="$(field "$log" triage-glossary)"
  ta="$(field "$log" triage-assert)"
  printf '%-12s %-12s %7s %9s %22s\n' "$date_part" "$cluster" "$probes" "$findings" "$ti/$tg/$ta"
done
[ "$total_sessions" -gt 0 ] || echo "(セッションログなし)"

echo ""
echo "--- 2. クラスタカバレッジ ---"
printf '%-12s %8s %-12s %10s\n' "クラスタ" "回数" "最終探索" "累積findings"
unexplored=""
for c in $(clusters); do
  count=0
  last=""
  sum=0
  for log in "$SESSIONS_DIR"/*.md; do
    [ -e "$log" ] || continue
    [ "$(session_cluster "$log")" = "$c" ] || continue
    count=$((count + 1))
    last="$(basename "$log" | cut -c1-10)"
    f="$(field "$log" findings)"
    case "$f" in *[!0-9]*) ;; *) sum=$((sum + f)) ;; esac
  done
  if [ "$count" -eq 0 ]; then
    printf '%-12s %8s %-12s %10s\n' "$c" "0" "未探索" "-"
    unexplored="$unexplored $c"
  else
    printf '%-12s %8s %-12s %10s\n' "$c" "$count" "$last" "$sum"
  fi
done

echo ""
echo "--- 3. 変更ホットスポット（直近${SINCE_DAYS}日・src/main 上位10）---"
hotspots="$(cd "$ROOT" && git log --since="$SINCE_DAYS days ago" --name-only --pretty=format: \
  -- 'src/main/java/**' 2>/dev/null | grep '\.java$' | sort | uniq -c | sort -rn | head -10)"
hot_clusters=""
if [ -n "$hotspots" ]; then
  echo "$hotspots" | while read -r n path; do
    [ -n "$path" ] || continue
    printf '%4s回  %-18s %s\n' "$n" "[$(guess_cluster "$(basename "$path")")]" "$(basename "$path")"
  done
  hot_clusters="$(echo "$hotspots" | awk '{print $2}' | while read -r p; do
    [ -n "$p" ] && guess_cluster "$(basename "$p")"; done | sort -u)"
else
  echo "(直近${SINCE_DAYS}日に src/main の変更なし)"
fi

echo ""
echo "--- 4. シグナル（次のチャーター候補の根拠）---"
signal_count=0

# 4a. 変更が集中しているのに未探索/陳腐化しているクラスタ
today_epoch="$(date +%s)"
for c in $hot_clusters; do
  last=""
  for log in "$SESSIONS_DIR"/*.md; do
    [ -e "$log" ] || continue
    [ "$(session_cluster "$log")" = "$c" ] || continue
    last="$(basename "$log" | cut -c1-10)"
  done
  if [ -z "$last" ]; then
    echo "★ クラスタ [$c] は変更ホットスポットだが一度も探索されていない → 最優先チャーター候補"
    signal_count=$((signal_count + 1))
  else
    last_epoch="$(date -j -f %Y-%m-%d "$last" +%s 2>/dev/null || date -d "$last" +%s 2>/dev/null || echo 0)"
    if [ "$last_epoch" -gt 0 ] && [ $(( (today_epoch - last_epoch) / 86400 )) -gt "$STALE_DAYS" ]; then
      echo "★ クラスタ [$c] は変更が集中しているが最終探索から${STALE_DAYS}日超 → 再探索候補"
      signal_count=$((signal_count + 1))
    fi
  fi
done

# 4b. 同一クラスタ2回連続発見ゼロ（停止基準: 次のクラスタへ移る）
for c in $(clusters); do
  recent2="$(for log in "$SESSIONS_DIR"/*.md; do
    [ -e "$log" ] || continue
    [ "$(session_cluster "$log")" = "$c" ] || continue
    field "$log" findings
  done | tail -2 | tr '\n' ' ')"
  if [ "$recent2" = "0 0 " ]; then
    echo "■ クラスタ [$c] は2回連続発見ゼロ → 停止基準該当。次のクラスタ/次の層（相互作用・負の空間）へ"
    signal_count=$((signal_count + 1))
  fi
done

# 4c. 未探索クラスタの列挙（ホットスポットでなくても網羅の観点で提示）
if [ -n "$unexplored" ]; then
  echo "・未探索クラスタ:$unexplored"
  signal_count=$((signal_count + 1))
fi

[ "$signal_count" -gt 0 ] || echo "(シグナルなし: 全クラスタ探索済みかつ陳腐化なし)"

echo ""
echo "次の一歩: シグナルからチャーター（対象/観点/動機/タイムボックス）を書き、"
echo "exploratory-testing.md の手順でセッションを実施。終了後はフッターを必ず記録。"
exit 0
