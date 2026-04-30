# Play Store 掲載情報

この文書は LocalMD Reader v0.1.0 の Google Play 掲載情報です。

2026-04-14 に確認した要件:

- アプリ名: 30 文字以内。
- 短い説明: 80 文字以内。
- 詳細説明: 4,000 文字以内。
- アプリアイコン: 512 x 512 px、alpha ありの 32-bit PNG、1024 KB 以内。
- フィーチャーグラフィック: 1024 x 500 px、JPEG または alpha なしの 24-bit PNG。
- スクリーンショット: 合計 2 枚以上、JPEG または alpha なしの 24-bit PNG。
- 推奨スマホスクリーンショット: 4 枚以上、最小 1080 px、縦 9:16 または横 16:9。

公式参照:

- https://support.google.com/googleplay/android-developer/answer/9859152
- https://support.google.com/googleplay/android-developer/answer/9866151

## アプリ詳細

アプリ名:

```text
LocalMD Reader
```

転記元:

```text
play-store/listing/ja-JP/title.txt
```

短い説明:

```text
ローカルMarkdownをタブ、ダークモード、オフライン表示で読む
```

転記元:

```text
play-store/listing/ja-JP/short-description.txt
```

詳細説明:

```text
LocalMD Reader は、ローカルファイル向けの軽量な Android Markdown ビューアです。

アプリ内または Android のファイルマネージャから .md / .markdown ファイルを開き、端末上でシンプルに読むことができます。

読みやすさを重視した機能:
- ローカル Markdown レンダリング
- 複数タブ
- アプリ再起動後のタブ復元
- ライトテーマとダークテーマ
- ピンチ操作によるフォントサイズ変更
- 最近開いたファイル、最大 5 件
- テーブル、コードブロック、チェックリスト、引用、見出し、リンク

プライバシーを重視した設計:
- ログインなし
- 同期なし
- 解析 SDK なし
- ネットワーク権限なし
- 選択した Markdown の内容は端末上に留まります

LocalMD Reader v0.1.0 は編集ではなく閲覧に集中しています。完全な CommonMark 互換、Mermaid、数式、脚注、リモート画像読み込み、相対画像表示、クラウド同期はこのバージョンでは含まれません。
```

転記元:

```text
play-store/listing/ja-JP/full-description.txt
```

文字数チェック:

```text
Google Play の現在の制限内に収める:
- アプリ名: 30 文字以内
- 短い説明: 80 文字以内
- 詳細説明: 4,000 文字以内
```

編集ルール:

- 価格、割引、ランキング、受賞、ストアバッジには触れない。
- v0.1.0 について、編集、同期、クラウド保存、完全な CommonMark 互換が
  あるようには書かない。
- プライバシーに関する表現は `PRIVACY.ja.md` と
  `docs/play-store-data-safety.ja.md` に合わせる。
- 将来、ネットワークアクセス、同期、広告、解析、クラッシュ送信、リモート
  画像読み込みを追加する場合は、掲載文、スクリーンショット、データ
  セーフティ文書を同時に更新する。

## ストアカテゴリ

メインカテゴリ:

```text
Productivity
```

理由:

LocalMD Reader は、メモ、ドキュメント、ローカルテキスト作業のためのファイル閲覧ツールです。

## アプリアイコン

必要な素材:

```text
play-store/icon-512.png
```

デザインメモ:

```text
docs/icon-design-note.ja.md
```

書き出しコマンド:

```sh
scripts/export-play-store-icon.sh
```

要件:

- 512 x 512 px。
- alpha ありの 32-bit PNG。
- 1024 KB 以内。
- ランキング、価格、ストアバッジ、宣伝文言を入れない。
- ランチャーアイコンの方向性と一致させる。

## フィーチャーグラフィック

必要な素材:

```text
play-store/feature-graphic-1024x500.png
```

書き出しコマンド:

```sh
scripts/export-play-store-feature-graphic.sh
```

要件:

- 1024 x 500 px。
- JPEG または 24-bit PNG。
- alpha なし。
- 純白、純黒、濃いグレーの背景を避ける。
- 主要な視覚要素を中央付近に置く。
- 価格、ランキング、受賞、Google Play バッジ、行動を促す文言を入れない。

ドラフトの方向性:

Markdown 文書を読んでいる場面を清潔に見せます。文書面、コード、リスト、テーブルの要素、LocalMD Reader の印象を中心にし、言語ごとの作り分けを減らすため文字は最小限または無しにします。

代替テキスト:

```text
Markdown文書プレビュー、タブ、読書用コントロールを表示したLocalMD Reader。
```

転記元:

```text
play-store/listing/ja-JP/feature-graphic-alt.txt
```

## スマホスクリーンショット

推奨セット: 縦 1080 x 1920 px を 4 枚。

スクリーンショット 1:

```text
Markdown ファイルを開く、または最近開いたファイルを見るためのウェルカム画面。
```

代替テキスト:

```text
ファイルを開く操作と最近開いたファイル操作があるLocalMD Readerのウェルカム画面。
```

転記元:

```text
play-store/listing/ja-JP/phone-screenshot-01-alt.txt
```

スクリーンショット 2:

```text
見出し、リスト、コード、リンクを含む Markdown 表示画面。
```

代替テキスト:

```text
見出し、リスト、コードブロック、リンクを表示したMarkdown文書。
```

転記元:

```text
play-store/listing/ja-JP/phone-screenshot-02-alt.txt
```

スクリーンショット 3:

```text
テーブルと横スクロールのヒントを表示したダークテーマの閲覧画面。
```

代替テキスト:

```text
横スクロールできるテーブルを表示したダークテーマのMarkdown閲覧画面。
```

転記元:

```text
play-store/listing/ja-JP/phone-screenshot-03-alt.txt
```

スクリーンショット 4:

```text
複数タブと、テーマ、言語、配置、最近開いたファイルのメニュー。
```

代替テキスト:

```text
テーマ、言語、配置、最近開いたファイルを操作できるタブとメニュー。
```

転記元:

```text
play-store/listing/ja-JP/phone-screenshot-04-alt.txt
```

スクリーンショットのルール:

- 実際のアプリ画面を使う。
- 指、端末フレーム、無関係な背景、Google Play バッジを入れない。
- ランキング、価格、受賞、行動を促す文言を入れない。
- 個人のファイル名、プライベートなパス、アカウント名、通知、トークンを表示しない。
- 通知やキャリア情報が見える場合は、アップロード前にステータスバーを整理またはトリミングする。

撮影手順:

```text
docs/play-store-screenshots.ja.md
```

## プレビュー動画

v0.1.0 の判断:

```text
プレビュー動画は用意しない。
```

理由:

このアプリは小さなビューアです。v0.1.0 の価値は、4 枚の分かりやすいスマホスクリーンショットで伝え、動画制作とローカライズの負荷は増やしません。
