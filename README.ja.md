# MdLite Reader

MdLite Reader は、軽量な Android 向け Markdown ビューアです。

ローカル Markdown ファイルをすばやく読むことを目的とし、広告、トラッキング、
ログイン、同期、ネットワークアクセスを前提にしません。

## 機能

- `.md` と `.markdown` ファイルを開く
- Android のファイルマネージャから Markdown ファイルを開く
- ローカルで Markdown をレンダリングし、制限した WebView で表示する
- ライトテーマとダークテーマ
- ピンチ操作によるフォントサイズ変更
- 最近開いたファイル、最大 5 件
- 最近開いたファイルの履歴クリア
- 複数タブ
- 前回開いていたタブの復元
- 操作部品を上部または下部へ移動
- `INTERNET` 権限なし

## 対応 Markdown

MdLite Reader v0.1.0 では、小さな Markdown サブセットを意図的に実装しています。

対応:

- 見出し
- 段落
- 箇条書き
- 番号付きリスト
- チェックリスト
- 引用
- フェンス付きコードブロック
- インラインコード
- HTTP / HTTPS リンク
- テーブル
- 水平線
- 生 HTML のエスケープ

v0.1.0 では非対応:

- Markdown 編集
- 完全な CommonMark 互換
- Mermaid
- 数式
- 脚注
- リモート画像読み込み
- 相対画像表示
- クラウド同期

## プライバシー

MdLite Reader は個人情報を収集しません。

アプリはユーザーが選択したファイルだけを読み取ります。Markdown 本文は端末上で
レンダリングされ、アプリによってアップロードされません。

詳しくは [PRIVACY.ja.md](PRIVACY.ja.md) を参照してください。

## セキュリティ

初期版ではネットワーク権限を要求せず、表示用 WebView の JavaScript も無効にします。

詳しくは [SECURITY.ja.md](SECURITY.ja.md) を参照してください。

## ビルド

現在は Termux 上の軽量 Android SDK 環境でビルドします。

```sh
cd ~/AndroidDev
. ./env.sh
cd projects/mdlite-reader
./build.sh
```

生成される debug APK:

```text
app-debug.apk
```

## テスト

```sh
./test.sh
```

テストスクリプトは JVM ユニットテスト、debug APK ビルド、署名検証、
`INTERNET` 権限が含まれていないことの確認を行います。

## リリース署名

本番署名にはリポジトリ外の keystore を使います。

詳しくは [docs/release-signing.ja.md](docs/release-signing.ja.md) を参照してください。

## リポジトリ状態

初期開発中は private のままにし、初回リリース準備が整った時点で public にします。

## ライセンス

Apache License 2.0 です。[LICENSE](LICENSE) を参照してください。
