# 探索セッション: 相互作用 appearance × viewer（SharedPreferences 共有） (2026-06-13)

## チャーター

| 項目 | 内容 |
|------|------|
| 対象 | appearance × viewer の2軸相互作用のうち、**SharedPreferences キー共有**（拡張戦略の「状態を共有する2軸」優先対象） |
| 観点 | 複数の設定が同じ prefs ファイル/キーに書くことで起きる衝突・意図しない相互消去。キー名前空間の分離と書き手の単一性 |
| 動機 | exploratory-testing.md の拡張戦略「SharedPreferences のキーを共有する組を優先」。テーマ（appearance）が viewer 設定と同居している可能性 |
| タイムボックス | 静的監査（probe ではなく grep ベース。対象が presentation 層=Android Context 依存のため） |

実行環境: 対象ストアは presentation 層で Android `Context`/`SharedPreferences` 依存。純 JVM probe は不可のため、**(prefs ファイル, キー) の網羅と書き手の特定による静的監査**で代替した。

## 監査と観測

| # | 突いた懸念 | 監査 | 観測 | 判定 |
|---|------|------|------|------|
| 1 | テーマの永続化先 | `viewer_theme` を書く箇所 | `ViewerSettingsStore` のみ。prefs ファイルは **`viewer_settings`**（viewer 設定と同居） | 規則が沈黙（同居が未文書） |
| 2 | ファイルの書き手の単一性 | 全 `getSharedPreferences(...)` の所有 | 各ファイルを書くのは1クラスのみ（clipboard_history→ClipboardHistoryStore / pro_purchase_cache→…Storage / recent・pinned・open_tabs→TabPersistence / **viewer_settings→ViewerSettingsStore**） | 規則どおり（衝突の調整役が一意） |
| 3 | ファイル内キー重複 | `viewer_settings` の全キー | 11キー（viewer_theme/viewer_language/controls_placement/double_tap_shortcut/circle_gesture_shortcut/custom_gesture_*/swipe_*）すべて相異 | 規則どおり |
| 4 | キー名の使い回し | `"items"` が4ファイルに出現 | open_tabs/recent_documents/pinned_documents/clipboard_history — **すべて別ファイル**（prefs ファイルは別名前空間）→ 衝突なし | 規則どおり |
| 5 | テーマの意図しない一括消去 | `viewer_settings` の `clear()`/`remove()` | 一括 `clear()` は**無し**。`remove()` はカスタムジェスチャ2キーのみ → テーマが他設定リセットに巻き込まれる経路なし | 規則どおり |

「判定」は3値: **規則どおり** / **規則と異なる** / **規則が沈黙**。

## 振り分けの結果

- **① issue: 0 件**。SharedPreferences の共有は健全: **prefs ファイルごとに書き手が一意**、ファイル内キーは相異、キー名の使い回しは別ファイル間のみ、テーマを巻き込む一括消去なし。**規則と異なる挙動・実害は検出されず**。
- **② glossary**: `domain-glossary-appearance.md` の L3 永続化境界に、テーマが viewer 設定と同じ `viewer_settings` ファイルに同居すること・書き手が `ViewerSettingsStore` 一意であること・衝突ゼロを監査で確認したことを明文化。
- **③ assert: 0 件**。対象が Android 依存で純 JVM テスト化が難しく、キー定数は private static。名前空間分離は「ファイルごと単一書き手」という構造で担保されており、現状テストの追加価値は低い（将来 viewer_settings に2つ目の書き手を足すなら、その時に衝突検査を検討）。

## 次のチャーター候補

- 2軸相互作用の他の組（purchase×viewer は実施済み）。残る Android 依存の相互作用は emulator ops リグでの実走探索が要る。
- TabPersistence の `recent`/`pinned`/`open_tabs` 間の状態相互作用（同一文書がタブと履歴に同時に存在する等）。

## 価値評価（機械可読・集計は scripts/exploration-status.sh）

- probes: 5
- findings: 1
- triage-issue: 0
- triage-glossary: 1
- triage-assert: 0
- time-minutes: 25
