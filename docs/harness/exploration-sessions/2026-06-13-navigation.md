# 探索セッション: navigation (2026-06-13)

## チャーター

| 項目 | 内容 |
|------|------|
| 対象 | navigation クラスタ（`domain-glossary-navigation.md`：見出し・目次・スクロール位置・検索） |
| 観点 | 規則が沈黙している箇所（負の空間）と境界値。特に `規則なし` と明記された `HeadingScrollPosition.estimatedHeadingIndex` と、文書化の薄い `MarkdownHeadings.fromMarkdown` の解析範囲 |
| 動機 | T2（未探索×変更ホットスポット交差）。`exploration-status.sh` で navigation は未探索（0回）。見出し抽出系は rendering と隣接し変更頻度が高い |
| タイムボックス | probe 17 本 |

実行環境: 全対象が domain/viewer 層で **Android 非依存**のため、純 JVM の scratch コード（`/tmp/navprobe/Probe.java`）を javac/Java 21 で直接コンパイル・実行して観測した。probe は足場であり本ログに観測を蒸留する。

## probe と観測

| # | 突いた規則/沈黙領域 | probe | 観測 | 判定 | 振り分け |
|---|------|------|------|------|------|
| 1 | フェンス検出（沈黙） | 3スペースインデントの \`\`\` 内に `# Inside` | `# Inside` を見出しと誤カウント（count=2） | 規則が沈黙 | ① #168 |
| 2 | フェンス検出（沈黙） | 開き \`\`\`、閉じ `\`\`\` `（末尾空白）、`# After` | コードブロックが閉じず `# After` 消失（count=0） | 規則が沈黙 | ① #168 |
| 3 | 閉じ ATX（沈黙） | `## Title ##` | title=`Title ##`（閉じ `##` が残る）、anchor=`title` | 規則が沈黙 | ② glossary（範囲注記） |
| 4 | アンカー正規化 L1 | `# 概要` → `# 詳細`（CJK のみ） | anchor=`heading`, `heading-2`（CJK は全て `heading` に潰れる） | 規則どおり（ASCII 限定の帰結） | ② glossary + ③ assert |
| 5 | `estimatedHeadingIndex`（規則なし） | count=3 / range=100 で scrollY を掃引 | y0–24→0, y25–74→1, y75–100→2（progress を round で写像） | 規則が沈黙（近似） | ② glossary（既存テストで固定済み） |
| 6 | クランプ | scrollY=-50→0 / scrollY=999>range→最終 index | -50→0、999→2 | 規則どおり | （既存テスト有） |
| 7 | ATX のスペース要件 | `#\tTitle`（タブ） | 見出しにならない（count=0） | 規則が沈黙 | ② glossary（範囲注記） |
| 8 | ATX のスペース要件 | `#  Title`（半角2つ） | title=`Title`（trim 済み）count=1 | 規則どおり | — |
| 9 | WebView メトリクス | `fromWebViewMetrics(50,1000,400,2.0)` count=5 | estIdx=0（scaled=2000, range=1600, progress≈0.031） | 規則どおり | （既存テスト有） |
| 10 | アンカー一意化 N1 | `# Foo` → `# foo` | `foo`, `foo-2`（大文字小文字を畳んで衝突→採番） | 規則どおり | ③ assert |
| 11 | アンカー衝突（沈黙） | `# @@@`（記号のみ）→ `# heading`（literal） | `heading`, `heading-2`（空正規化と literal `heading` が衝突） | 規則が沈黙 | ② glossary（注記） |
| 12 | 検索 L1 | `DocumentSearchQuery.from("\t \n ")` | isActive=false, text=`''` | 規則どおり | — |
| 13 | 目次ラベル（沈黙） | L1 `A` と L3 `C` のラベル | `A` / `    C`（(level-1)×2 スペース字下げ） | 規則が沈黙 | ② glossary |
| 14 | setext 見出し（沈黙） | `Title\n====` | 見出しにならない（count=0、ATX のみ対応） | 規則が沈黙 | ② glossary（範囲注記） |
| 15 | ATX のスペース要件 | `#Heading`（スペース無し） | 見出しにならない（count=0） | 規則どおり | （既存テスト有） |
| 16 | アンカー L1 | `# 123`（数字のみ） | anchor=`123`（数字は許容文字） | 規則どおり | ③ assert |
| 17 | 検索 N3 | `session.search(空白のみ)` | hasActiveQuery=false（`empty()` に正規化） | 規則どおり | （既存テスト有） |

## 振り分けの結果

- **① issue**: [#168](https://github.com/Yos-K/localmd-reader/issues/168) — コードフェンス検出の CommonMark 逸脱（P1 インデント / P2 末尾空白閉じ）。目次の見出し数が狂う実害。NO_COVERAGE。
- **② glossary**: `domain-glossary-navigation.md` に4点を追記（いずれも実装から「なぜ」を裏取り）。
  - MarkdownHeadingAnchors L1: アンカーは ASCII（a–z/0–9/ハイフン）限定。**CJK 等の非 ASCII 見出しは全て `heading` に正規化され `heading-2/3…` で採番**（P4/P11）。
  - MarkdownHeadings L3: パーサが認識するのは**列0の ATX（`#`×1–6＋半角スペース）と列0コードフェンスのみ**。setext・閉じ ATX・タブ区切り・インデント/末尾空白フェンスは範囲外（P3/P7/P14、ただしフェンスの一部は #168 で要修正）。
  - HeadingScrollPosition L3: `estimatedHeadingIndex` は近似（`min(1, scrollY/scrollRange)` を round で写像し両端をクランプ）。閾値例は P5（既存テストで固定済み）。
  - TableOfContentsItems L3: ラベルは `(level-1)×2` スペースの字下げ＋title（P13）。
- **③ assert**: `MarkdownHeadingsTest` に意図された未カバー挙動の characterization を2件追加（CJK アンカー潰れ P4、大文字小文字衝突の採番 P10）。

## 次のチャーター候補

- purchase クラスタ（未探索）。ステートフル PBT パイロットと合流予定（domain-knowledge-loop.md）。
- rendering × navigation の相互作用: コードフェンス検出は rendering 側（HtmlPageBuilder 等）と共有概念。#168 修正時に rendering 側のフェンス扱いと突き合わせる。

## 価値評価（機械可読・集計は scripts/exploration-status.sh）

- probes: 17
- findings: 8
- triage-issue: 1
- triage-glossary: 4
- triage-assert: 2
- time-minutes: 40
