# 本番リリース手順

この手順書は、Google Play の本番環境へのアクセスが承認された後、LocalMD Reader
の初回本番リリースを行うためのものです。

## 前提条件

- Play Console で本番環境へのアクセスが承認されている。
- `main` で `./test.sh` が成功する。
- `VERSION` が正しい。
- 本番ロールアウト手順以外の未解決ブロッカーがない。
- production signing key がリポジトリ外にある。
- `BUNDLETOOL_JAR` がローカルの bundletool jar を指している。
- Google Play service account JSON はリポジトリ外にある。

## 1. バージョンを上げるか判断する

本番リリースで新しい AAB をアップロードする場合は、`VERSION_NAME` と
`VERSION_CODE` の両方を上げます。`VERSION_CODE` だけを上げるリリースは、
明示的に記録した緊急再ビルド以外では行いません。

バグ修正相当の更新:

```sh
scripts/version-bump.sh patch
```

機能追加相当の更新:

```sh
scripts/version-bump.sh minor
```

すでに審査済みの closed testing release を、追加アーティファクトなしで production
へ昇格できる場合は、現在のバージョンを維持します。

必ず確認します。

```sh
scripts/version-show.sh
scripts/version-check.sh
```

## 2. リリースチェックを実行する

```sh
./test.sh
```

テスト、リリースチェック、第三者通知チェック、バージョン整合性チェックのいずれかが
失敗した場合は先に進みません。

## 3. 署名済み AAB を作る

```sh
scripts/build-signed-release.sh aab
```

デフォルト出力:

```text
build/release/mdlite-reader-<VERSION_NAME>-release.aab
```

AAB をリリース用の退避場所にコピーします。

```sh
mkdir -p "$HOME/AndroidDev/releases/mdlite-reader/v$(. ./VERSION; echo "$VERSION_NAME")"
cp "build/release/mdlite-reader-$(. ./VERSION; echo "$VERSION_NAME")-release.aab" \
  "$HOME/AndroidDev/releases/mdlite-reader/v$(. ./VERSION; echo "$VERSION_NAME")/"
sha256sum "$HOME/AndroidDev/releases/mdlite-reader/v$(. ./VERSION; echo "$VERSION_NAME")/"*.aab \
  > "$HOME/AndroidDev/releases/mdlite-reader/v$(. ./VERSION; echo "$VERSION_NAME")/SHA256SUMS"
```

## 4. Play Console でリリースする

次のどちらかで進めます。

- Play Console が許可する場合、承認済み closed testing release を production へ昇格する。
- 新しく作成した AAB を production track へアップロードする。

送信前に確認します。

- リリースノートが `docs/release/release-notes-v0.1.0.ja.md` と合っている。
- **`play-store/release-notes/<locale>/whatsnew.txt` の内容が今回のバージョンに合っている（手動確認必須）。**
  `check-release-notes.sh` はファイルの存在確認と500字制限チェックのみ行い、本文の版数言及は確認しない。
  バージョン更新時にリリースノートを更新し忘れても自動検知されず、旧版の内容がPlay Storeに配信される
  リスクがある。（リスク受容: 旧版数grepによるstale検知はPR #135でのファイル化移行に伴い廃止）
- ストア掲載文、アイコン、フィーチャーグラフィック、スクリーンショットが最新。
- Data safety と privacy policy が完了している。
- 国/地域の設定が意図通り。

production rollout を審査へ送信します。

## 5. リポジトリ状態を更新する

production release を送信または承認後に確認します。

```sh
git status --short
git log --oneline -5
```

リリースメタデータの更新があれば Conventional Commits でコミットします。

正確なリリースコミットにタグを付けます。

```sh
git tag v$(. ./VERSION; echo "$VERSION_NAME")
git push origin main --tags
```

GitHub Releases を使う場合は、`docs/release/release-notes-v0.1.0.md` を元に作成します。
署名鍵、service account JSON、APK、AAB、非公開のテスター情報はリポジトリに
アップロードしません。

## 6. リリース後確認

- ブラウザから Play Store 掲載ページを開く。
- Android 端末で Google Play からインストールする。
- ローカル Markdown ファイルを開く。
- 他アプリから Markdown ファイルを開く。
- Free ビルドに Pro 専用設定が出ていないことを確認する。
- Privacy と Pro features 画面が開けることを確認する。
- Play Console でクラッシュやポリシー警告を確認する。

## ロールバック

ロールアウト完了前に重大な問題が見つかった場合は、Play Console でロールアウトを
停止または一時停止します。すでに公開済みの場合は、より大きい `VERSION_CODE` の
patch version を作成し、この手順を再実行して修正版を送信します。
