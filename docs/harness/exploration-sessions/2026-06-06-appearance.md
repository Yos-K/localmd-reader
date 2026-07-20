# 探索セッション: 外観クラスタ (2026-06-06)

issue #100 の試行セッション#1。運用ルールは [`../exploratory-testing.md`](../exploratory-testing.md) を参照。

## チャーター

| 項目 | 内容 |
|------|------|
| 対象 | 外観クラスタ（`docs/domain/domain-glossary-appearance.md` の ViewerTheme / ViewerThemeStyle / FontSize / ViewerLanguage） |
| 観点 | L1/L2 規則の境界値・規則が沈黙している操作（永続化往復・Pro 系テーマの toggled・異常な pinch scale・null 入力） |
| 動機 | 次の視覚 PR #72（Pro テーマ差別化）がテーマ追加を触るため、テーマ規則の実挙動と文書の一致を先に確認する |
| タイムボックス | probe 20本以内（実績: probe 11本 + 呼び出し元のコード読み 2件） |

実行環境: 純 JVM（`javac` で domain/viewer/file を直接コンパイルし scratch main で観測。Android 不要、1 probe 数秒）。

## probe と観測

| # | 突いた規則(または沈黙領域) | probe | 観測 | 判定 | 振り分け |
|---|---------------------------|-------|------|------|---------|
| P1 | L1: 未知の保存値→light | `fromStoredValue(null/""/"DARK"/" dark ")` | すべて light（厳密一致） | 規則どおり | （L1 に「厳密一致」を補記） |
| P2 | 沈黙: 永続化往復 | 全7テーマで `storedValue→fromStoredValue` | 全て同一テーマに復元 | 規則が沈黙 | ② L1 追記 + ③ テスト追加 |
| P3 | 沈黙: Pro 系テーマの `toggled()` | 全7テーマで `toggled()` | dark/amoled→light、他5種→dark（列挙 `isDark()` 依存） | 規則が沈黙 | ② L3 追記 |
| P4 | 沈黙: Free 権限×Pro テーマからの `next()` | 全7テーマで `next(free)` | `toggled()` に退避（P3 と同じ向き） | 規則が沈黙 | ② L3 追記（P3 と統合） |
| P5 | T1: Pro 巡回の完全性 | light から `next(pro)`×7 | 7種全てを訪れ light に戻る | 規則どおり | （既存テストが段毎にカバー済み） |
| P6 | L1: `hasDarkBackground` の輝度判定 | 全7テーマで観測 | dark/amoled/aurora=true、他4種=false。aurora で列挙 `isDark()` と食い違い（文書どおり） | 規則どおり | — |
| P7 | 沈黙: `ViewerThemeStyle.from(null)` | null を渡す | 例外を投げず light の配色に倒す | 規則が沈黙 | ② L1 追記 |
| P8 | F1: FontSize 境界 | `of(11)`/`of(12).decreased()`/`of(28).increased()` | 例外/据え置き/据え置き | 規則どおり | — |
| P9 | 沈黙: 異常な pinch scale | `changedByPinchScale(0/-1/NaN/+Inf)` | 0/-1/NaN→**12(MIN_SP) に collapse**、+Inf→28 | 規則が沈黙（挙動は驚き最小に反する） | ① issue #102 |
| P10 | L1: 未知の言語→英語 | `fromStoredValue(null/"fr"/"JA")` | すべて英語 | 規則どおり | — |
| P11 | 沈黙: Pro 失効後の保存済み Pro テーマ | `ViewerSettingsStore.loadViewerTheme` のコード読み + 既存 medium テスト確認 | Free 権限+保存値が Pro テーマ→light に倒す（dark のみ維持）。`freeEntitlementDoesNotRestoreProOnlyTheme` でテスト済みだが**用語集に未記載** | 規則が沈黙 | ② T1 の L3 追記 |

補足（コード読みによる確認）:
- `ViewerTheme.toggled()` は production では `next()` 経由でのみ呼ばれる（直接呼び出しは `ControlsPlacement.toggled()` のみで別物）。
  Free は読み込み境界（P11）で light/dark に絞られるため、P3/P4 の Pro 系挙動は通常経路では発火しない（防御的挙動）。

## 振り分けの結果

- **① issue**: [#102](https://github.com/Yos-K/localmd-reader/issues/102) — `changedByPinchScale` が NaN/0/負で MIN_SP に collapse（P9）
- **② glossary 追記**（`domain-glossary-appearance.md`）: 永続化往復の L1（P2）、`from(null)` の L1（P7）、
  読み込み境界の失効フォールバック L3（P11）、`toggled()` の向きと到達性 L3（P3/P4）、#102 参照（P9）
- **③ テスト追加**: `ViewerThemeTest.storedValueRoundTripsForEveryTheme`（P2。欠陥挿入で dusk→mist に変えると失敗することを確認済み）

## 次のチャーター候補

- viewer クラスタ「開く判定」: OPEN_TEXTS / intent の事後条件（#97 の暗黙規則はまだ用語集に無い）
- 相互作用層: テーマ×システムダークモード初期値（`storedThemeValueOrSystemDefault`）、言語×テーマの保存キー独立性
- 負の空間: 壊れた Markdown / 巨大入力に対するレンダラ（mutation の SURVIVED が集中する `JavaSimpleMarkdownRenderer`）

## 価値評価（機械可読・集計は scripts/exploration-status.sh）

数値はすべて本文の probe 表・振り分け節から導出（2026-06-11 に様式追加時へ遡及記入）。

- probes: 11
- findings: 6
- triage-issue: 1
- triage-glossary: 5
- triage-assert: 1
- time-minutes: 未計測（様式導入前のセッション）

findings の内訳: 判定「規則が沈黙」= P2, P3, P4, P7, P9, P11 の6件（「規則と異なる」は0件）。
