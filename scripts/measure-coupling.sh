#!/data/data/com.termux/files/usr/bin/sh
# measure-coupling.sh — パッケージ結合度の測定（advisory・ゲートではない）
#
# なぜ: 結合は「変更の爆発半径」を決め、AIエージェントの改修コストを支配する。
# vladikk の Balanced Coupling（結合の負荷 = 強度 × 距離 × 変動性）に倣い、
# 静的結合（import）と時間的結合（git 共変更）の両面から、
# 「遠くて・強くて・よく変わる」危険な結合を順位付けする。
# 指標定義・閾値・運用は docs/harness/code-quality-metrics.md を参照。
#
# 測定内容:
#   1. パッケージ別 Ca / Ce / 不安定度 I = Ce/(Ca+Ce)（fitness D-01〜D-03）
#   2. SDP違反（安定依存の原則: 自分より不安定なパッケージへ依存している辺）
#   3. Balanced Coupling 上位辺: 強度(import数) × 変動性(被依存側の90日変更数)
#   4. 共変更ペア: 別パッケージなのに同一コミットで頻繁に共変更されるファイル
#      （= import に現れない隠れた結合。シグナル閾値は5回以上）
#
# 依存: git / awk のみ（コンパイル不要・全環境で実行可能）
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
OUT="$ROOT/build/quality"
SRC="src/main/java"
PKG_PREFIX="io.github.yosk.mdlite"
SINCE_DAYS="${COUPLING_SINCE_DAYS:-90}"
COCHANGE_MIN="${COUPLING_COCHANGE_MIN:-5}"
mkdir -p "$OUT"
cd "$ROOT"

# --- 1. import から「ファイル→パッケージ」依存辺を抽出 ---
# 出力: src_pkg \t dst_pkg \t src_file （内部パッケージのみ・自パッケージ除く）
find "$SRC" -name "*.java" -type f | while read -r f; do
  pkg=$(sed -n 's/^package \([a-z0-9.]*\);/\1/p' "$f" | head -1)
  sed -n "s/^import $PKG_PREFIX\.\([a-z0-9.]*\)\.[A-Z][A-Za-z0-9]*;/\1/p" "$f" \
    | sort -u | while read -r dst; do
        full_dst="$PKG_PREFIX.$dst"
        [ "$full_dst" = "$pkg" ] || printf '%s\t%s\t%s\n' "$pkg" "$full_dst" "$f"
      done
done > "$OUT/edges.tsv"

# --- 2. 変動性: 直近 SINCE_DAYS 日のパッケージ別変更回数 ---
git log --since="$SINCE_DAYS days ago" --name-only --pretty=format: -- "$SRC" 2>/dev/null \
  | grep '\.java$' | sed 's|/[^/]*\.java$||; s|^src/main/java/||; s|/|.|g' \
  | sort | uniq -c | awk '{print $2 "\t" $1}' > "$OUT/volatility.tsv"

echo "==================== 結合度測定 ($SRC / 変動性は直近${SINCE_DAYS}日) ===================="
echo ""

# --- 3. Ca / Ce / I とSDP違反・Balanced上位を一括計算 ---
awk -F'\t' -v vol_file="$OUT/volatility.tsv" '
  BEGIN {
    while ((getline line < vol_file) > 0) {
      split(line, vparts, "\t"); vol[vparts[1]] = vparts[2]
    }
  }
  {
    src = $1; dst = $2
    strength[src "\t" dst]++
    if (!(src "\t" dst in seen_pair)) { } # noop
    pkgs[src] = 1; pkgs[dst] = 1
  }
  END {
    # Ce/Ca はパッケージ間の異なり数で数える
    for (e in strength) {
      split(e, p, "\t")
      if (!(e in counted)) { ce[p[1]]++; ca[p[2]]++; counted[e] = 1 }
    }
    printf "--- パッケージ別 Ca(被依存) / Ce(依存) / 不安定度 I / 変更回数 ---\n"
    printf "%-44s %4s %4s %6s %6s\n", "package", "Ca", "Ce", "I", "変更"
    for (k in pkgs) {
      af = ca[k] + 0; ef = ce[k] + 0
      i = (af + ef > 0) ? ef / (af + ef) : 0
      printf "%-44s %4d %4d %6.2f %6d\n", k, af, ef, i, vol[k] + 0
      inst[k] = i
    }
    printf "\n--- SDP違反（自分より不安定なパッケージへの依存。安定依存の原則）---\n"
    sdp = 0
    for (e in strength) {
      split(e, p, "\t")
      if (inst[p[2]] > inst[p[1]] + 0.05) {
        printf "  %s (I=%.2f) -> %s (I=%.2f)  強度%d\n", p[1], inst[p[1]], p[2], inst[p[2]], strength[e]
        sdp++
      }
    }
    if (sdp == 0) printf "  なし（依存の向きは安定側に揃っている）\n"
    printf "\n--- Balanced Coupling 上位辺（負荷 = 強度 × 被依存側の変動性）---\n"
    printf "    「強く依存している先がよく変わる」ほど改修の波及コストが高い\n"
    printf "%-7s %-5s %-5s %s\n", "負荷", "強度", "変動", "依存辺"
    n = 0
    for (e in strength) {
      split(e, p, "\t")
      load[e] = strength[e] * (vol[p[2]] + 0)
    }
    # 上位8件を選択ソート（awk に sort が無いため）
    while (n < 8) {
      best = ""; bv = -1
      for (e in load) if (!(e in done) && load[e] > bv) { bv = load[e]; best = e }
      if (best == "" || bv <= 0) break
      split(best, p, "\t")
      printf "%-7d %-5d %-5d %s -> %s\n", bv, strength[best], vol[p[2]] + 0, p[1], p[2]
      done[best] = 1; n++
    }
  }
' "$OUT/edges.tsv"
echo ""

# --- 4. 共変更ペア（import に現れない時間的結合）---
echo "--- 共変更ペア（別パッケージ・同一コミット ${COCHANGE_MIN}回以上）---"
echo "    静的依存が無い（または逆向きの）共変更は、隠れた結合か責務の分散を示す"
git log --since="$SINCE_DAYS days ago" --name-only --pretty=format:"@@@" -- "$SRC" 2>/dev/null \
  | awk -v min="$COCHANGE_MIN" '
    /^@@@$/ { delete files; nf = 0; next }
    /\.java$/ {
      gsub(/^src\/main\/java\//, ""); gsub(/\.java$/, "")
      nf++; files[nf] = $0
      for (i = 1; i < nf; i++) {
        a = files[i]; b = files[nf]
        pa = a; pb = b; sub(/\/[^\/]*$/, "", pa); sub(/\/[^\/]*$/, "", pb)
        if (pa != pb) {
          pair = (a < b) ? a "\t" b : b "\t" a
          count[pair]++
        }
      }
    }
    END {
      shown = 0
      while (shown < 10) {
        best = ""; bv = min - 1
        for (p in count) if (!(p in done) && count[p] > bv) { bv = count[p]; best = p }
        if (best == "") break
        split(best, q, "\t")
        na = split(q[1], s1, "/"); nb = split(q[2], s2, "/")
        printf "  %2d回  %s ⇔ %s\n", bv, s1[na], s2[nb]
        done[best] = 1; shown++
      }
      if (shown == 0) printf "  なし（閾値 %d 回以上のペアは検出されず）\n", min
    }'
echo ""
echo "詳細: $OUT/edges.tsv（依存辺全量）/ $OUT/volatility.tsv"
echo "measure-coupling done (advisory)"
exit 0
