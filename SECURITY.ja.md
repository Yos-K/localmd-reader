# セキュリティポリシー

MdLite Reader は権限を最小化し、Markdown レンダリングをローカルで行います。

## サポート対象バージョン

MdLite Reader は private の開発初期段階です。初回公開リリースの準備が整うまでは、
セキュリティ修正は現在の `main` ブランチに適用します。

## 脆弱性の報告

private 開発中のセキュリティ問題は、メンテナへ直接報告してください。

リポジトリを public にする時点で、この節に専用の報告先または GitHub Security
Advisory の手順を追記します。

## セキュリティ原則

- ユーザーが選択したファイルを開くために必要な権限だけを使う
- v0.1.0 では `INTERNET` 権限を要求しない
- 広告、解析、自動クラッシュ送信を入れない
- 個人情報をログに出さない
- 本番署名鍵をリポジトリに保存しない
- Markdown パース処理を Android UI コードから分離する

## ファイルアクセス

MdLite Reader は、ユーザーが選択した文書を Android のファイルアクセス機能で
開きます。

`MANAGE_EXTERNAL_STORAGE` のような広範なストレージ権限は要求しません。

## WebView 表示

WebView はローカル表示面としてのみ使用します。

必須設定:

- JavaScript 無効
- DOM storage 無効
- Database 無効
- File access 無効
- Content access 無効

Markdown の生 HTML は表示前にエスケープします。

## リンク

クリック可能な Markdown リンクとして表示するのは HTTP / HTTPS のみです。

`javascript:` などの非対応スキームはクリック可能な anchor にしてはいけません。

## リリース前チェック

リリース前に確認すること:

- `./test.sh` を実行する
- APK が `android.permission.INTERNET` を要求していないことを確認する
- WebView の安全性テストが通っていることを確認する
- 本番署名鍵がコミットされていないことを確認する
- プライバシードキュメントが実際のアプリ挙動と一致していることを確認する
