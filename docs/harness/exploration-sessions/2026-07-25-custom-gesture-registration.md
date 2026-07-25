# 探索セッション: カスタムジェスチャー登録 (2026-07-25)

## チャーター

| 項目 | 内容 |
|------|------|
| 対象 | ジェスチャークラスタのプレビュー、登録、割り当て、中断 |
| 観点 | 表示が操作方法と一致するか。未登録・登録済み・描画中の各状態で利用経路が完結するか |
| 動機 | オーナー観測「カスタムジェスチャーのアニメーションで線に対して点の挙動がずれている」 |
| タイムボックス | probe 8本。ローカルPro APKと無線ADB接続の実機を使用 |

## probe と観測

| # | 突いた規則または沈黙領域 | probe | 観測 | 判定 | 振り分け |
|---|---|---|---|---|---|
| P1 | プレビュー線と移動点の一致 | カスタムプレビューの描画実装と移動点の補間を比較 | 線は3次ベジェ曲線、点は制御点を結ぶ折れ線を移動していた | 規則が沈黙 | glossary GES6、例テスト4件 |
| P2 | 未登録状態で有効な操作 | カスタムが「オフ」の行から詳細を開く | タイトルが `カスタムジェスチャー: ` と空欄で、存在しない登録の「削除」も表示された | 規則が沈黙 | glossary GES7、状態型と例テスト2件 |
| P3 | 全画面登録とシステム領域 | カスタム登録画面を開いてスクリーンショットを確認 | 案内文が時計・通知アイコンと重なり読みにくかった | 規則が沈黙 | glossary GES8、境界テスト2件 |
| P4 | 描画から動作割り当てまでの完結 | 実機で十分な大きさの線を描き「検索バーを表示」を選択 | ジェスチャー一覧へ戻り、カスタム行へ選択結果が即時反映された | 規則どおり | 肯定的証拠 |
| P5 | 1動作1割り当て先の可視化 | 動作選択一覧を確認 | 登録済み動作に「割り当て先: 右方向」等が表示された | 規則どおり | 肯定的証拠 |
| P6 | 画面消灯中の物理UI探索 | 操作列の途中で端末が消灯した状態でADB入力を継続 | 入力はアプリへ届かずSystem UIが前面になった | 規則が沈黙（ハーネス制約） | 実機探索時は画面点灯維持が必要 |
| P7 | 描画中の中断 | 描画画面のUIと戻る処理を確認 | 画面上にキャンセル操作がなく、戻る操作にも描画専用処理がなかった | 規則が沈黙 | glossary GES9、キャンセル領域テスト2件 |
| P8 | 登録状態の再起動復元 | 修正版を再起動して登録済み・未登録の両状態を再確認する計画 | 端末ロック後に無線ADBがofflineとなり未確認 | 未確認 | 次回の実機チャーターへ継続 |
| P9 | GES8の実機配線 | 修正版登録画面を実機で撮影 | 初回修正は子ViewへInsetsが届かず通知領域に重なった。Activity取得値の明示渡し後は重なりなし | 規則と異なる | Activity取得値の配線テスト、実機再確認済み |
| P10 | 可視キャンセルと当たり判定 | キャンセル文字の左側をタップ | 初回は文字幅とタップ矩形が別計算で反応しなかった。同一矩形化後は閲覧画面へ戻った | 規則と異なる | 同一矩形モデル、境界テスト、実機再確認済み |
| P11 | GES9の端末戻る | Android 13以降の実機で描画中に戻る | 旧`onBackPressed`ではActivityが終了した。`OnBackInvokedDispatcher`対応後はActivityを維持して描画だけ終了 | 規則と異なる | modern back配線テスト、実機再確認済み |

## 振り分けの結果

- **① issue**: このセッション内でP1、P2、P3、P7を直接修正したため、別issueは作成しない。
- **② glossary**: `domain-glossary-gestures.md` に GES6（線と点の同一軌跡）、GES7（状態ごとの有効操作）、GES8（システム領域回避）、GES9（描画中断）を追加。
- **③ assert**: `CustomGesturePreviewPathTest` 4件、`CustomGestureMenuTest` 2件、`CustomGestureDrawingLayoutTest` 4件へ蒸留。

## 回帰テストへの蒸留

| finding | 回帰テスト | 確認する仕様 | 状態 |
|---|---|---|---|
| P1 | `CustomGesturePreviewPathTest.animationStartsAtTheRenderedCurveStart` | 点が描画線の始点にある | 追加済み |
| P1 | `CustomGesturePreviewPathTest.animationMidpointUsesTheRenderedCubicCurveInsteadOfItsControlPolygon` | 点が制御点間の折れ線ではなくベジェ曲線を通る | 追加済み |
| P1 | `CustomGesturePreviewPathTest.animationReachesTheCubicEndpointBeforeFollowingTheFinalLine` | ベジェ区間と終端直線が連続する | 追加済み |
| P1 | `CustomGesturePreviewPathTest.animationEndsAtTheRenderedFinalLineEndpoint` | 点が描画線の終点に達する | 追加済み |
| P2 | `CustomGestureMenuTest.unregisteredGestureOffersRegistrationWithoutAFalseDeleteAction` | 未登録時に無効な削除を提示しない | 追加済み |
| P2 | `CustomGestureMenuTest.registeredGestureOffersEveryValidManagementAction` | 登録済みの有効操作を欠落させない | 追加済み |
| P3 | `CustomGestureDrawingLayoutTest.instructionBaselineStartsBelowTheStatusBarInset` | 案内文が上端システム領域より下にある | 追加済み |
| P3 | `CustomGestureDrawingLayoutTest.instructionBaselineKeepsItsTopSpacingWithoutAStatusBarInset` | インセットなしでも通常の上余白を維持する | 追加済み |
| P6 | なし | 画面消灯はアプリ仕様ではなく物理探索ハーネスの実行条件 | テスト対象外。点灯維持を実機手順で扱う |
| P7 | `CustomGestureDrawingLayoutTest.topRightPointInsideTheVisibleCancelLabelCancelsRegistration` | 表示されたキャンセル領域を操作できる | 追加済み |
| P7 | `CustomGestureDrawingLayoutTest.drawingPointOutsideTheCancelLabelRemainsPartOfTheGesture` | 通常の描画をキャンセルと誤認しない | 追加済み |
| P9 | `CustomGestureDrawingInsetsStructureTest.drawingViewReceivesTheInsetAlreadyObservedByTheActivity` | Activityが取得済みのInsetsを描画Viewへ明示的に渡す | 追加済み |
| P10 | `CustomGestureDrawingLayoutTest.visibleLeftSideOfTheCancelLabelBelongsToItsTouchTarget` | 可視キャンセル文字の全幅が同じタップ領域に入る | 追加済み |
| P11 | `CustomGestureDrawingInsetsStructureTest.drawingViewReceivesTheInsetAlreadyObservedByTheActivity` | 描画開始・終了時に新しい戻るコールバックを登録・解除する | 追加済み（同じ配線契約） |

P8は未確認でありfindingではない。次回観測後に欠陥と判定した場合、この表へ回帰テストを追加する。

## 次のチャーター候補

- 登録済みカスタムジェスチャーについて、再起動後の形状・動作・一覧表示を同時に確認する。
- 描画中に画面回転、アプリのバックグラウンド化、プロセス再生成を行った場合の中断状態を確認する。
- カスタムと円・方向ジェスチャーが近い形状の場合の優先順位と誤認識を実機で確認する。

## 価値評価（機械可読・集計は scripts/exploration-status.sh）

- probes: 11
- findings: 8
- triage-issue: 0
- triage-glossary: 4
- triage-assert: 12
- time-minutes: 50
