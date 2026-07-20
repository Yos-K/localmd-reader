# ADR-0013: 文書タブのセッション遷移を一つのcontrollerで完了する

状態: 採用

## 決定事項

`OpenDocumentTabs`をalways-validなタブ状態として維持し、タブの選択、閉じる、前、次のコマンドを
`DocumentTabSessionController`へ集約する。controllerは正となる状態の更新、古い状態メッセージの消去、
表示文言とタブ表示の更新、アクティブ文書の描画、復元可能タブの保存までを一つの遷移として完了する。

新規文書を開く操作は、ファイル、外部Intent、クリップボード下書きごとに検証、履歴、永続化、アンカーの
要件が異なるため、それぞれの調停役に残す。

## 検討した選択肢

- clickとgestureの各handlerに完了処理を重複したまま残す。
- Androidの描画と永続化を`OpenDocumentTabs`へ移す。
- タブを生成、置換、選択、閉じるすべての操作を直ちに一つのcontrollerへ集約する。
- 同等の選択・閉じる遷移だけを先に集約する。

## 選択理由

clickとgestureの入口は同じ複数段階の更新を行っていた。いずれかを省くと、表示文書、タブ列、状態メッセージ、
復元セッションが`OpenDocumentTabs`と不一致になる。一つのcontrollerにより、不変なviewer modelへAndroidの
関心事を持ち込まず、application境界で完了処理を一体化できる。

## 選択しなかった理由

調停処理の重複は入口ごとの退行を許す。Android Viewや永続化を`OpenDocumentTabs`へ入れると依存方向が逆転する。
すべてのopen経路を一度に移すと、最近開いたファイルへの記録、一時文書の永続化、移動先アンカーなどの意味ある
差異が隠れる。

## 決定を見直す契機

文書を開く方針が明示的なcommandまたはeffectとして表現された場合、タブ状態を`MainActivity`から完全に移す場合、
またはbackground/session serviceが開いた文書の正となる場合に見直す。
