# Google Play Developer API

この文書は MdLite Reader の API ベースのリリース運用を定義します。

## 対象範囲

Google Play Developer Publishing API で、繰り返し行うリリース作業を自動化します。

- 署名済み AAB のアップロード。
- アップロードした AAB のテストトラックへの割り当て。
- リリースノートの設定。
- edit の commit または validate。

初回設定には Play Console UI が残ります。

- 法的同意と申告。
- デベロッパーアカウント確認。
- アプリ作成。
- パッケージ所有権確認。
- サービスアカウント権限設定。
- Production access 申請。

Google の API は `edits` ワークフローを使います。変更は edit に積まれ、commit する
まで反映されません。

## サービスアカウント設定

`gcloud` が使える環境では、サービスアカウント作成と JSON key 生成をスクリプトで
実行できます。Google Cloud Shell で実行するのが簡単です。

```sh
scripts/create-google-play-service-account-key.sh PROJECT_ID
```

出力されるファイル:

```text
google-play-service-account.json
```

このファイルを Termux 側の次の場所に置きます。

```text
~/AndroidDev/secrets/google-play-service-account.json
```

その後、Play Console 側でサービスアカウントをユーザーとして追加します。

1. Play Console を開く。
2. `Users and permissions` を開く。
3. スクリプトが出力したサービスアカウントのメールアドレスを招待する。
4. MdLite Reader のリリース管理に必要な最小権限だけを付与する。

JSON key はコミットしません。リポジトリでは `service-account*.json` を ignore
していますが、推奨保存先はリポジトリ外です。

## Python 依存関係

API 依存関係をインストールします。

```sh
python3 -m pip install --user -r requirements-play-api.txt
```

ローカルのセットアップ状態を確認します。

```sh
scripts/play-check-api-setup.py
```

## AAB を Closed Testing へアップロード

テストトラックIDは Play Console 側の設定に依存します。`alpha`、`beta`、
`internal`、またはカスタム closed testing track id など、Play Console の track id
を使います。

利用可能な track id を確認します。

```sh
scripts/play-list-tracks.py \
  --service-account "$HOME/AndroidDev/secrets/google-play-service-account.json"
```

draft リリースを作成する場合:

```sh
scripts/play-upload-closed-test.py \
  --service-account "$HOME/AndroidDev/secrets/google-play-service-account.json" \
  --track TRACK_ID \
  --status draft
```

closed test として配信するリリースを作成する場合:

```sh
scripts/play-upload-closed-test.py \
  --service-account "$HOME/AndroidDev/secrets/google-play-service-account.json" \
  --track TRACK_ID \
  --status completed
```

デフォルトの AAB:

```text
~/AndroidDev/releases/mdlite-reader/v0.1.0/mdlite-reader-0.1.0-release.aab
```

現在の v0.1.0 API アップロード:

```text
Track: alpha
Status: draft
Bundle versionCode: 2
Edit: 01079045793414669908
```

versionCode 1 は Google Play にアップロード済みです。target SDK 35 のビルドでは
versionCode 2 以降を使います。

パッケージ名:

```text
io.github.yosk.mdlite
```

## 注意

- API edit を開いている間に Play Console UI で変更すると、その edit は無効になる場合があります。
- API が「初回 APK/AAB が Play Console からアップロードされていない」として拒否する場合は、
  初回アップロードだけ Play Console で行い、その後のリリースから API を使います。
- API 権限確認だけ行う場合は `--validate-only` を使います。

## ストア掲載情報を更新

英語/日本語の掲載文、アプリアイコン、フィーチャーグラフィックを更新します。

```sh
scripts/play-update-listing.py
```

入力元:

```text
play-store/listing/en-US/*.txt
play-store/listing/ja-JP/*.txt
play-store/icon-512.png
play-store/feature-graphic-1024x500.png
```

commit せずに検証だけ行う場合:

```sh
scripts/play-update-listing.py --validate-only
```

現在の v0.1.0 掲載情報 API 更新:

```text
Status: committed
Edit: 00044233089121248600
Locales: en-US, ja-JP
Images: icon, featureGraphic
```
