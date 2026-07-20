# 探索セッション: 相互作用 entitlement × features (2026-06-13)

## チャーター

| 項目 | 内容 |
|------|------|
| 対象 | purchase × viewer の2軸相互作用: `FeatureEntitlement` × `ViewerFeature` → `ProFeaturePresentation` / `ProFeaturesPresentation`（PUR4 と PUR2 の合流） |
| 観点 | 権限と特典 tier の直積で「利用可否」がどう決まるか、その境界と沈黙領域。負の空間（空/null/重複記述子）と防御的コピー |
| 動機 | 拡張戦略の次段「単軸 → 状態を共有する2軸の相互作用」（exploratory-testing.md）。6クラスタ単軸一巡後の初の2軸セッション |
| タイムボックス | probe 13 本 |

実行環境: 全対象が domain 層・Android 非依存のため純 JVM probe（`/tmp/intprobe/Probe.java`）で観測。

## probe と観測

| # | 突いた規則/沈黙領域 | probe | 観測 | 判定 | 振り分け |
|---|------|------|------|------|------|
| 1 | PUR4 | `from(Free, catalog)` | count=8 / **全ロック(0 available)** / isPro=false | 規則どおり | （既存テスト有） |
| 2 | PUR4 | `from(Pro, catalog)` | count=8 / **全解放(8 available)** / isPro=true | 規則どおり | （既存テスト有） |
| 3 | tier基準（沈黙） | Free × `[freeFeature, proFeature]` | free=**available** / pro=locked | 規則どおり（tier基準・未文書） | ② glossary + ③ assert |
| 4 | 負の空間 | `from(Free, [])` 空記述子 | length 0・例外なし | 規則どおり（堅牢） | — |
| 5 | 防御 | `from(Free, null記述子)` | IllegalArgumentException | 規則どおり | — |
| 6 | 防御 | `from(Free, [null要素])` | IllegalArgumentException | 規則どおり | — |
| 7 | 防御 | `ProFeaturesPresentation.from(null権限, …)` | IllegalArgumentException | 規則どおり | — |
| 8 | 防御 | `descriptor.isAvailableFor(null)` | IllegalArgumentException | 規則どおり | — |
| 9 | カプセル化（沈黙） | `features()` 返り値を破壊 | 再取得で length 8・要素非null（**防御的コピー**） | 規則どおり（未文書） | ③ assert |
| 10 | 表示 | `statusLabel` | available→Available / locked→Locked | 規則どおり | （既存テスト有） |
| 11 | 2軸整合 | Pro / Free+ready の `purchase().shouldShowAction()` | **false / true**（Pro=解放+購入非表示、Free=ロック+購入表示） | 規則どおり | （既存テスト有） |
| 12 | 経路一致 | 全項目 `item.available == Free.allows(feature)` | 全一致 | 規則どおり | — |
| 13 | カタログ実態 | catalog 中の free-tier 件数 | **0 / 8**（全 Pro-tier） | 規則どおり | ② glossary（裏取り） |

「判定」は3値: **規則どおり** / **規則と異なる** / **規則が沈黙**。

## 振り分けの結果

- **① issue: 0 件**。2軸相互作用は整合的かつ堅牢（PUR4×PUR2 が一貫、null/空に防御的、`features()` は防御的コピー）。**規則と異なる挙動・実害は検出されず**。
- **② glossary**: `domain-glossary-purchase.md` の PUR4 に**「`available` は特典自身の tier で決まり、記述子リスト所属では決まらない」**を明文化（P3/P13）。free-tier 特典をリストに入れると Free でも available になること、現行カタログは全 Pro-tier ゆえ表面化しないこと、2軸の整合（Pro=全解放+購入非表示／Free+ready=全ロック+購入表示）を裏取りつきで記載。
- **③ assert**: 2件の characterization を追加。
  - `ProFeaturePresentationTest#availabilityFollowsTheFeatureTierNotListMembership`（P3: free特典はFreeでもavailable・pro特典はロック）。
  - `ProFeaturesPresentationTest#featuresReturnsADefensiveCopy`（P9: 返り値破壊が内部に波及しない）。

## 次のチャーター候補

- **appearance × viewer**（テーマ設定の SharedPreferences キー共有）の相互作用。ただし SharedPreferences 由来は Android 依存のため、emulator 探索リグ（ops モード）か Robolectric medium が要る。
- purchase の Alloy モデル（`pro-purchase-state.als`）との突合（本ループの次タスク #8）。

## 価値評価（機械可読・集計は scripts/exploration-status.sh）

- probes: 13
- findings: 2
- triage-issue: 0
- triage-glossary: 1
- triage-assert: 2
- time-minutes: 35
