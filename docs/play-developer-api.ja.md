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

1. Google Cloud Console を開く。
2. Play Console 自動化用の project を作成または選択する。
3. `Google Play Android Developer API` を有効化する。
4. サービスアカウントを作成する。
5. そのサービスアカウントの JSON key を作成する。
6. JSON key をリポジトリ外に保存する。

```text
~/AndroidDev/secrets/google-play-service-account.json
```

7. Play Console を開く。
8. `Users and permissions` を開く。
9. サービスアカウントのメールアドレスをユーザーとして招待する。
10. MdLite Reader のリリース管理に必要な最小権限だけを付与する。

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

パッケージ名:

```text
io.github.yosk.mdlite
```

## 注意

- API edit を開いている間に Play Console UI で変更すると、その edit は無効になる場合があります。
- API が「初回 APK/AAB が Play Console からアップロードされていない」として拒否する場合は、
  初回アップロードだけ Play Console で行い、その後のリリースから API を使います。
- API 権限確認だけ行う場合は `--validate-only` を使います。
