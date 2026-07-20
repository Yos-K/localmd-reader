# Play Store スクリーンショット

この文書は LocalMD Reader v0.1.0 のスクリーンショット撮影手順を定義します。

## 安全ルール

- `play-store/samples/` のサンプル Markdown だけを使う。
- 個人のファイル名、プライベートなパス、アカウント名、通知、トークンを表示しない。
- 撮影前に通知を消す。
- 撮影中は Do Not Disturb の利用を推奨する。
- コミット前にすべてのスクリーンショットを確認する。
- ステータスバーやナビゲーション領域に個人情報が含まれる場合は、トリミングまたは撮り直す。

## サンプルファイル

撮影前にサンプルを Download フォルダへコピーします。

```sh
cp play-store/samples/screenshot-document.md /sdcard/Download/mdlite-screenshot-document.md
cp play-store/samples/screenshot-wide-table.md /sdcard/Download/mdlite-screenshot-wide-table.md
```

## 撮影コマンド

現在の画面を撮影します。

```sh
scripts/capture-play-store-screenshot.sh phone-01-welcome
```

出力先:

```text
play-store/screenshots/
```

手動撮影した画像は、コミット前にメタデータを除去し、1080 x 1920 に書き出して配置します。

```sh
scripts/prepare-play-store-screenshot.sh /sdcard/Download/phone-01-welcome.jpg play-store/screenshots/phone-01-welcome.jpg
```

## 必要なセット

スマホスクリーンショット 1:

```text
Markdown ファイルを開く、または最近開いたファイルを見るためのウェルカム画面。
```

撮影名:

```text
phone-01-welcome.png
```

スマホスクリーンショット 2:

```text
見出し、リスト、コード、リンク、テーブルを含む Markdown 表示画面。
```

撮影名:

```text
phone-02-document.png
```

スマホスクリーンショット 3:

```text
横長テーブルを表示したダークテーマの閲覧画面。
```

撮影名:

```text
phone-03-dark-table.png
```

スマホスクリーンショット 4:

```text
テーマ、言語、配置、最近開いたファイルを操作できるタブとメニュー。
```

撮影名:

```text
phone-04-tabs-menu.png
```

## 手動手順

1. `/sdcard/Download/localmd-reader-debug.apk` から最新 debug APK をインストールする。
2. アプリを開き、ウェルカム画面を撮影する。
3. `/sdcard/Download/mdlite-screenshot-document.md` を開く。
4. Markdown 表示画面を撮影する。
5. `/sdcard/Download/mdlite-screenshot-wide-table.md` を開く。
6. ダークテーマへ切り替え、横長テーブル画面を撮影する。
7. 2つのタブを開き、メニューを表示して撮影する。
8. スクリーンショットに個人情報が含まれていないか確認する。
9. 承認したスクリーンショットを `scripts/prepare-play-store-screenshot.sh` で `play-store/screenshots/` に置く。
