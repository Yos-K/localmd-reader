# 探索セッション: カスタムジェスチャーの永続化 (2026-07-25)

## チャーター

| 項目 | 内容 |
|------|------|
| 対象 | カスタムジェスチャーの保存、再起動復元、削除 |
| 観点 | 形状と動作が常に一組で復元され、欠損状態や削除済み状態が登録済みに化けないか |
| 動機 | 前セッションP8で無線ADB切断により再起動復元が未確認となった |
| タイムボックス | probe 8本 |

## probe と観測

| # | 突いた規則または沈黙領域 | probe | 観測 | 判定 | 振り分け |
|---|---|---|---|---|---|
| P1 | 正常な形状と動作の組 | 保存値へ変換後に復元 | 形状と`next_tab`動作を一組で復元できた | 規則が沈黙 | glossary GES10、例テスト |
| P2 | 動作だけが残る欠損状態 | 空の形状と`next_tab`を復元 | 登録なしへfail closedした | 規則が沈黙 | glossary GES10、例テスト |
| P3 | 未知の保存動作 | 正常形状と`unknown_action`を復元 | 登録なしへfail closedした | 規則が沈黙 | glossary GES10、例テスト |
| P4 | 破損した保存形状 | `not-a-shape`と`next_tab`を復元 | 例外を外へ漏らさず登録なしになった | 規則が沈黙 | glossary GES10、例テスト |
| P5 | 非有限座標 | 保存値と描画入力へNaNを混入 | 修正前は`CustomGestureShape`を構築でき、Always-Validを破った | 規則と異なる | 有限性L1、例テスト2件 |
| P6 | 保存の途中状態 | `saveCustomGestureShortcut`のEditor操作を確認 | 形状と動作を同じEditorで書き、1回の`apply`で確定していた | 規則どおり | 肯定的証拠 |
| P7 | 削除の途中状態 | `clearCustomGestureShortcut`のEditor操作を確認 | 形状と動作を同じEditorで削除し、1回の`apply`で確定していた | 規則どおり | 肯定的証拠 |
| P8 | プロセス再起動後の実機復元 | 登録済みでforce-stop・起動後、削除して再度force-stop・起動 | 登録済みの形状動作は「検索バーを表示」として復元され、削除後は「オフ」を維持した | 規則どおり | 実機の肯定的証拠 |

## 振り分けの結果

- **① issue**: P5を同セッション内で修正したため別issueは作成しない。
- **② glossary**: `domain-glossary-gestures.md` のCustomGestureShape L1へ有限性、GES10へ保存ペアの完全復元規則を追加。
- **③ assert**: P1からP5を7件の例テストへ蒸留。

## 回帰テストへの蒸留

| finding | 回帰テスト | 確認する仕様 | 状態または未反映理由 |
|---|---|---|---|
| P1 | `CustomGestureShortcutTest.validStoredShapeAndActionRestoreOneCompleteShortcut` | 完全な保存ペアは形状と動作を同時に復元する | 追加済み |
| P2 | `CustomGestureShortcutTest.missingStoredShapeRestoresNoShortcut` | 形状欠損は登録なしへ閉じる | 追加済み |
| P3 | `CustomGestureShortcutTest.unknownStoredActionRestoresNoShortcut` | 未知動作は登録なしへ閉じる | 追加済み |
| P4 | `CustomGestureShortcutTest.malformedStoredShapeRestoresNoShortcut` | 破損形状は例外を漏らさず登録なしへ閉じる | 追加済み |
| P5 | `CustomGestureShapeTest.storedShapeRejectsNonFiniteCoordinatesBeforeCreatingADomainObject` | 非有限な保存座標を拒否する | 追加済み |
| P5 | `CustomGestureShapeTest.drawnShapeRejectsNonFiniteCoordinatesBeforeNormalization` | 非有限な描画座標を正規化前に拒否する | 追加済み |
| P5 | `CustomGestureShortcutTest.nonFiniteStoredShapeRestoresNoShortcut` | 非有限な保存ペアは登録なしへ閉じる | 追加済み |
| P6 | なし | SharedPreferences Editorの同一トランザクション構造 | Android境界。現時点はコード観測、将来medium test候補 |
| P7 | なし | SharedPreferences Editorの同一トランザクション構造 | Android境界。現時点はコード観測、将来medium test候補 |
| P8 | なし | 実機再起動後の復元 | 実機確認済み。自動化はAndroid medium test候補 |

## 次のチャーター候補

- 再起動後の一覧復元は確認済み。復元した形状を描いて割り当て動作が実際に発火することを確認する。
- 保存途中のプロセス停止をmedium testで再現する価値を評価する。

## 価値評価（機械可読・集計は scripts/exploration-status.sh）

- probes: 8
- findings: 5
- triage-issue: 0
- triage-glossary: 2
- triage-assert: 7
- time-minutes: 20
