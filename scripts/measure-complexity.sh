#!/data/data/com.termux/files/usr/bin/sh
# measure-complexity.sh — メソッド単位の複雑度測定（advisory・ゲートではない）
#
# なぜ: AIエージェントの改修コストは「分岐の組合せ（循環的複雑度）」と
# 「読解に要する構造把握（認知的複雑度）」に比例して増える。さらに、その差分
# （認知的 − 循環的）は抽出リファクタで減らせる構造ノイズ＝偶有的複雑性の
# 代理指標になる（本質的な分岐は両方に現れるが、ネスト・フロー断絶は認知的
# 複雑度だけを押し上げる）。重複（CPD）も純粋な偶有として併測する。
# 指標定義・閾値・運用は docs/harness/code-quality-metrics.md を参照。
#
# 使い方:
#   sh scripts/measure-complexity.sh            # レポート表示（常に exit 0）
#   sh scripts/measure-complexity.sh --strict   # RED が 1 件でもあれば exit 1
#
# PMD は PATH 上のものを使う。無ければ GitHub releases から取得して
# build/quality/lib にキャッシュする（run-mutation-tests.sh の方式に準拠）。
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
OUT="$ROOT/build/quality"
LIB="$OUT/lib"
SRC="$ROOT/src/main/java"
RULESET="$ROOT/scripts/quality/complexity-ruleset.xml"
PMD_VERSION="7.20.0"
STRICT="${1:-}"
mkdir -p "$OUT" "$LIB"

# 閾値バンド（出典: docs/harness/code-quality-metrics.md）
CC_YELLOW=10   # GREEN <=10 / YELLOW 11-20 / RED >20（fitness S-01）
CC_RED=20
COG_YELLOW=15  # GREEN <=15 / YELLOW 16-25 / RED >25（Sonar 既定 15 起点）
COG_RED=25
CPD_MIN_TOKENS=100

ensure_pmd() {
  if command -v pmd >/dev/null 2>&1; then
    PMD_BIN="pmd"
    return
  fi
  PMD_HOME="$LIB/pmd-bin-$PMD_VERSION"
  PMD_BIN="$PMD_HOME/bin/pmd"
  if [ ! -x "$PMD_BIN" ]; then
    echo "Downloading PMD $PMD_VERSION..."
    curl -sL "https://github.com/pmd/pmd/releases/download/pmd_releases%2F$PMD_VERSION/pmd-dist-$PMD_VERSION-bin.zip" \
      -o "$LIB/pmd.zip"
    unzip -q -o "$LIB/pmd.zip" -d "$LIB"
    rm -f "$LIB/pmd.zip"
  fi
}

ensure_pmd

# PMD exit codes: 0 = 違反なし, 4 = 違反あり（測定 ruleset では全メソッドが
# "違反" として報告されるので 4 が正常）。それ以外は実行エラー。
status=0
"$PMD_BIN" check -d "$SRC" -R "$RULESET" -f text --no-progress \
  > "$OUT/complexity-raw.txt" 2>"$OUT/pmd-errors.txt" || status=$?
if [ "$status" != "0" ] && [ "$status" != "4" ]; then
  echo "measure-complexity failed: PMD exited with $status" >&2
  cat "$OUT/pmd-errors.txt" >&2
  exit 1
fi

# file:line をキーに CC と認知的複雑度を突合し TSV 化
awk -F'\t' '
  /CyclomaticComplexity:/ {
    split($1, loc, ":"); key = loc[1] ":" loc[2]
    if (match($0, /complexity of [0-9]+/)) {
      cc[key] = substr($0, RSTART + 14, RLENGTH - 14)
    }
    if (match($0, /\047[^\047]+\047/)) {
      name[key] = substr($0, RSTART + 1, RLENGTH - 2)
    }
  }
  /CognitiveComplexity:/ {
    split($1, loc, ":"); key = loc[1] ":" loc[2]
    if (match($0, /complexity of [0-9]+/)) {
      cog[key] = substr($0, RSTART + 14, RLENGTH - 14)
    }
  }
  END {
    for (key in cc) {
      c = cc[key]; g = (key in cog) ? cog[key] : 0
      print key "\t" name[key] "\t" c "\t" g "\t" (g - c)
    }
  }
' "$OUT/complexity-raw.txt" | sort -t'	' -k4,4nr > "$OUT/methods.tsv"

total=$(wc -l < "$OUT/methods.tsv" | tr -d ' ')
if [ "$total" -eq 0 ]; then
  echo "measure-complexity failed: no methods parsed (PMD output format changed?)" >&2
  exit 1
fi

echo "==================== 複雑度測定 (src/main/java) ===================="
echo ""
echo "対象メソッド数: $total"
echo ""

echo "--- バンド集計（循環的複雑度 CC / 認知的複雑度 Cog）---"
awk -F'\t' -v cy="$CC_YELLOW" -v cr="$CC_RED" -v gy="$COG_YELLOW" -v gr="$COG_RED" '
  { if ($3 <= cy) ccg++; else if ($3 <= cr) ccy++; else ccr++
    if ($4 <= gy) cgg++; else if ($4 <= gr) cgy++; else cgr++ }
  END {
    printf "CC : GREEN(<=%d) %d / YELLOW(%d-%d) %d / RED(>%d) %d\n", cy, ccg+0, cy+1, cr, ccy+0, cr, ccr+0
    printf "Cog: GREEN(<=%d) %d / YELLOW(%d-%d) %d / RED(>%d) %d\n", gy, cgg+0, gy+1, gr, cgy+0, gr, cgr+0
  }' "$OUT/methods.tsv"
echo ""

echo "--- 認知的複雑度 上位15（AI読解コストのホットスポット）---"
printf '%-4s %-4s %-5s %s\n' "Cog" "CC" "差分" "メソッド（場所）"
head -15 "$OUT/methods.tsv" | awk -F'\t' '{
  n = split($1, p, "/"); printf "%-4s %-4s %-5s %s  (%s:%s)\n", $4, $3, $5, $2, p[n-1] "/" p[n], ""
}' | sed 's|:(.*)||'
echo ""

echo "--- 偶有的複雑性の代理: (認知的 − CC) 上位10 ---"
echo "    差分が大きいほど、ドメイン分岐でなくネスト・構造ノイズが複雑さの源泉"
echo "    （= Extract Method 等で挙動を変えず削減できる見込みが高い）"
printf '%-5s %-4s %-4s %s\n' "差分" "Cog" "CC" "メソッド（ファイル）"
sort -t'	' -k5,5nr "$OUT/methods.tsv" | head -10 | awk -F'\t' '{
  n = split($1, p, "/"); printf "%-5s %-4s %-4s %s  (%s)\n", $5, $4, $3, $2, p[n]
}'
echo ""

echo "--- 重複（CPD, ${CPD_MIN_TOKENS}トークン以上・偶有的複雑性そのもの）---"
cpd_status=0
"$PMD_BIN" cpd --minimum-tokens "$CPD_MIN_TOKENS" --dir "$SRC" --language java \
  > "$OUT/cpd-raw.txt" 2>/dev/null || cpd_status=$?
if [ "$cpd_status" != "0" ] && [ "$cpd_status" != "4" ]; then
  echo "(CPD 実行エラー: exit $cpd_status — スキップ)"
else
  blocks=$(grep -c "^Found a " "$OUT/cpd-raw.txt" 2>/dev/null) || blocks=0
  dup_lines=$(awk '/^Found a /{ if (match($0, /[0-9]+ line/)) s += substr($0, RSTART, RLENGTH-5) } END { print s+0 }' "$OUT/cpd-raw.txt")
  echo "重複ブロック: $blocks 件 / 重複行（1出現あたり）: $dup_lines 行"
  grep -A2 "^Found a " "$OUT/cpd-raw.txt" | grep "Starting at line" | head -6 | sed 's/^/  /'
fi
echo ""
echo "詳細: $OUT/methods.tsv（TSV全量）/ $OUT/cpd-raw.txt"

red_total=$(awk -F'\t' -v cr="$CC_RED" -v gr="$COG_RED" '$3 > cr || $4 > gr' "$OUT/methods.tsv" | wc -l | tr -d ' ')
if [ "$STRICT" = "--strict" ] && [ "$red_total" -gt 0 ]; then
  echo "measure-complexity --strict: RED メソッドが $red_total 件あります" >&2
  exit 1
fi
echo "measure-complexity done (RED: $red_total 件 / advisory)"
exit 0
