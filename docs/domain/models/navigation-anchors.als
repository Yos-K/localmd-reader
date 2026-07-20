// 仕様モデル: ナビゲーションクラスタ（見出しアンカーの一意性と目次の導出）
//
// docs/domain/domain-glossary-navigation.md の L2 ルールを Alloy 6 で形式化する:
// - N1: アンカーIDは文書内で一意（同名見出しでもジャンプ先が一意に定まる）
// - N2: 目次は見出しから導出する（独立に持たず、見出しと食い違わない）
// - N4: 見出し移動は文書内に留まり、先頭・末尾で循環する
// 目的: ルール群の無矛盾性検査と、仕様変更時の保証喪失を反例として提示する設計段階ハーネス。
//
// 対応する実装（モデルの写し先であり検査対象ではない）:
// - src/main/java/io/github/yosk/mdlite/domain/MarkdownHeadingAnchors.java
//   （nextAnchorId: 初出は base、再出現は base-2/base-3 ... と採番）
// - src/main/java/io/github/yosk/mdlite/domain/MarkdownHeadings.java（fromMarkdown）
// - src/main/java/io/github/yosk/mdlite/domain/TableOfContentsItems.java（from: 見出し1つに目次項目1つ）
// - src/main/java/io/github/yosk/mdlite/domain/HeadingNavigation.java
//   （Unavailable/Destination、next/previous の循環）
//
// 抽象化の注記:
// - アンカーの文字列正規化（小文字英数+ハイフン、非ASCII/空は "heading" に畳む）は本モデルでは
//   検査しない。正規化後の「base」を atom に抽象する。複数の見出しが同じ Base を共有しうることで
//   衝突（同名見出し・CJK や記号のみで全て "heading" に畳まれる最悪ケース。探索 2026-06-13 P4/P11）を表す。
// - 見出しの文書内出現順は util/ordering で総順序として表す（採番は出現順に依存するため）。
// - N3（検索セッションの正規化）は状態写像で別物のため本モデルの対象外。
//
// 実行: scripts/check-domain-model.sh

module navigationAnchors

open util/ordering[Heading]

// 正規化後のアンカー基底。複数の見出しが同じ Base を共有しうる（衝突）。
sig Base {}

// 文書中の見出し（util/ordering により文書内出現順の総順序を持つ）。
sig Heading { base: one Base }

// 同じ base を持つ、自分より前（文書順）の見出し集合。
// アンカーは (base, このサイズ=出現順位) で識別される:
//   出現順位 0 → base そのもの、1 → base-2、2 → base-3 ...（nextAnchorId の採番）
fun earlierSameBase[h: Heading]: set Heading {
  { g: Heading | g.base = h.base and lt[g, h] }
}

// ==== N1: アンカーは文書内で一意 ====

// 保証1: 異なる見出しは異なるアンカーを得る。
// 異 base なら基底が違うので別、同 base なら出現順位が違うので別。
// 出現順位は総順序上の「先行同 base 見出し数」で、同 base 内で狭義単調 → 必ず相異なる。
//（破ると: 同名見出しで目次が誤った見出しへジャンプする）
assert AnchorsAreUniqueWithinDocument {
  all disj h1, h2: Heading |
    h1.base = h2.base implies #earlierSameBase[h1] != #earlierSameBase[h2]
}
check AnchorsAreUniqueWithinDocument for 8

// 保証2: すべての見出しが単一の base に畳まれても（CJK/記号のみで全て "heading" になる最悪ケース）、
// アンカーは一意であり続ける（heading, heading-2, heading-3 ...）。
//（破ると: 日本語文書など base が総崩れする文書でジャンプ先が衝突する）
assert UniqueEvenWhenAllShareOneBase {
  (one Base) implies (all disj h1, h2: Heading | #earlierSameBase[h1] != #earlierSameBase[h2])
}
check UniqueEvenWhenAllShareOneBase for 8

// ==== N2: 目次は見出しから導出する ====

// 目次項目は見出しを参照する（独立した文言を持たない）。
sig TocItem { source: one Heading }

// from(headings): 各見出しに目次項目をちょうど1つ作る（派生のルール）。
fact tocDerivedFromHeadings {
  all h: Heading | one i: TocItem | i.source = h
}

// 保証3: 目次は見出しと全単射（同数・取りこぼし無し・重複無し）。
// 派生ルールの帰結として、本文の見出しと目次が件数・対応で食い違わないことを保証する。
//（破ると: 本文に在る見出しが目次に出ない／目次の項目が見出しと対応しない）
assert TocBijectsHeadings {
  (all disj i1, i2: TocItem | i1.source != i2.source)   // 単射（1見出しに2項目を作らない）
  and (all h: Heading | some i: TocItem | i.source = h)  // 全射（取りこぼし無し）
  and #TocItem = #Heading                                 // 同数
}
check TocBijectsHeadings for 8

// ==== N4: 見出し移動は有効な文書内の移動先だけを取る ====

fun nextHeading[h: Heading]: one Heading {
  h = last implies first else next[h]
}

fun previousHeading[h: Heading]: one Heading {
  h = first implies last else prev[h]
}

// 保証4: 見出しが存在する限り、次・前の移動先は必ず同じ文書の見出しである。
assert NavigationStaysWithinDocument {
  some Heading implies all h: Heading |
    nextHeading[h] in Heading and previousHeading[h] in Heading
}
check NavigationStaysWithinDocument for 8

// 保証5: 末尾の次は先頭、先頭の前は末尾となる。
assert NavigationWrapsAtBothEnds {
  some Heading implies nextHeading[last] = first and previousHeading[first] = last
}
check NavigationWrapsAtBothEnds for 8

// 保証6: 次へ移動してから前へ移動すると、元の見出しへ戻る（逆方向も同様）。
assert NextAndPreviousAreInverse {
  some Heading implies all h: Heading |
    previousHeading[nextHeading[h]] = h and nextHeading[previousHeading[h]] = h
}
check NextAndPreviousAreInverse for 8
