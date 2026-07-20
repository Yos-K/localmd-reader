# 探索セッション: rendering code preview (2026-07-01)

## チャーター

| 項目 | 内容 |
|------|------|
| 対象 | rendering クラスタ（`domain-glossary-rendering.md`） |
| 観点 | HTML / Markdown コードブロックの Raw / Preview 切り替えで、仕様が沈黙している入力の境界を突く |
| 動機 | 直近で `JavaSimpleMarkdownRenderer` と `HtmlPageBuilder` を変更し、コードブロックの新しい表示状態を追加したため |
| タイムボックス | probe 11本 |

## probe と観測

| # | 突いた規則(または沈黙領域) | probe | 観測 | 判定 | 振り分け |
|---|---|---|---|---|---|
| 1 | コードフェンス言語名の大文字小文字 | `HTML` フェンスで Raw / Preview が出るか | 既存実装では通常コードブロックになった | 規則が沈黙 | assert |
| 2 | コードフェンス言語名の大文字小文字 | `Markdown` フェンスで Raw / Preview が出るか | 既存実装では通常コードブロックになった | 規則が沈黙 | assert |
| 3 | HTMLプレビューの属性付き許可タグ | `<h1 class="..." onclick="...">` をプレビューできるか | 既存実装では開始タグだけエスケープされ、プレビューとして崩れた | 規則が沈黙 | assert |
| 4 | HTMLプレビューの安全境界 | 属性付き許可タグの属性が出力されないか | 属性を落として `<h1>...</h1>` として表示する仕様にした | 規則どおり | assert |
| 5 | L5 smoke の検知範囲 | 大文字 `HTML` / `Markdown` を fixture に入れたとき Raw / Preview ラベルを検知できるか | `smoke-render-assert-l5.sh` に `Preview` の可視テキスト確認を追加 | 規則どおり | assert |
| 6 | コードフェンス info string のメタ情報 | `html title="example"` フェンスで Raw / Preview が出るか | 既存実装では言語名全体を unsafe とみなし通常コードブロックになった | 規則が沈黙 | glossary / assert |
| 7 | Markdown プレビューと info string | `markdown source=readme` フェンスで Markdown プレビューが出るか | 既存実装では通常コードブロックになった | 規則が沈黙 | glossary / assert |
| 8 | コードハイライトと info string | `java linenums` フェンスで Java ハイライトが効くか | 既存実装では通常コードブロックになった | 規則が沈黙 | glossary / assert |
| 9 | L5 smoke の操作確認 | `Preview` ラベルを実際にタップし、Markdown プレビュー本文が見えるか | 以前の L5 smoke は `Preview` ラベルの存在だけを見ており、切替操作そのものは検査していなかった。`tap_visible_text` を追加し、Preview タップ後の表示を検査するようにした | 規則どおり | assert |
| 10 | 複数 previewable code block の操作確認 | 同一文書内の Markdown / HTML Preview を別々にタップできるか | 以前の L5 smoke は最初の `Preview` だけをタップしていた。出現位置を指定して2つ目の `Preview` もタップし、HTML プレビュー本文を確認するようにした | 規則どおり | assert |
| 11 | Raw への復帰操作 | HTML Preview 表示後に Raw をタップし、raw source に戻れるか | 以前の L5 smoke は Preview へ進む方向だけを検査していた。2つ目の `Raw` をタップし、プレビューでは削除される `onclick` 属性が raw source として見えることを確認するようにした | 規則どおり | assert |

## 振り分けの結果

- ①issue: なし。小さく再現でき、その場で修正した。
- ②glossary: `domain-glossary-rendering.md` に「info string の最初のトークンだけを言語名として扱う」規則を追加。
- ③assert: `JavaSimpleMarkdownRendererTest` に6件、`smoke-render-assert-l5.sh` に4件追加。

## 次のチャーター候補

- Raw / Preview の切替操作は UIAutomator で複数ブロックの往復確認まで到達した。次は Preview 表示中に文書スクロールやフォント変更を行ったときの状態保持を確認する。

## 価値評価（機械可読・集計は scripts/exploration-status.sh）

- probes: 11
- findings: 6
- triage-issue: 0
- triage-glossary: 1
- triage-assert: 10
- time-minutes: 70
