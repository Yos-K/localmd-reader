# MdLite Reader

MdLite Reader は、軽量な Android 向け Markdown ビューアです。

高速、ローカルファースト、プライバシー尊重を重視します。

## 方針

- 広告なし
- トラッキングなし
- ログインなし
- ネットワーク権限なし
- ログに個人情報を入れない
- ローカル Markdown ファイルを読む

## 状態

開発初期段階です。初回リリース準備チェックが完了するまで、リポジトリは private のままにします。

## ビルド

```sh
cd ~/AndroidDev
. ./env.sh
cd projects/mdlite-reader
./build.sh
```

生成される APK:

```text
app-debug.apk
```

## テスト

```sh
./test.sh
```

現在のテストスクリプトは APK をビルドし、署名検証と `INTERNET` 権限が含まれていないことを確認します。

## ライセンス

Apache License 2.0 です。[LICENSE](LICENSE) を参照してください。
