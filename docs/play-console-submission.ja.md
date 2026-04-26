# Play Console 提出ガイド

この文書は MdLite Reader v0.1.0 について、Google Play Console に手動入力する
項目をまとめます。

## アプリ掲載情報

英語の掲載情報は次のファイルから入力します。

- `play-store/listing/en-US/title.txt`
- `play-store/listing/en-US/short-description.txt`
- `play-store/listing/en-US/full-description.txt`

日本語の掲載情報は次のファイルから入力します。

- `play-store/listing/ja-JP/title.txt`
- `play-store/listing/ja-JP/short-description.txt`
- `play-store/listing/ja-JP/full-description.txt`

主カテゴリ:

```text
Productivity
```

## 画像アセット

次のアセットをアップロードします。

- `play-store/icon-512.png`
- `play-store/feature-graphic-1024x500.png`
- `play-store/screenshots/phone-01-welcome.jpg`
- `play-store/screenshots/phone-02-document.jpg`
- `play-store/screenshots/phone-03-dark-table.jpg`
- `play-store/screenshots/phone-04-tabs-menu.jpg`

## 代替テキスト

英語の feature graphic 代替テキストは次のファイルから入力します。

```text
play-store/listing/en-US/feature-graphic-alt.txt
```

英語のスクリーンショット代替テキストは次のファイルから入力します。

```text
play-store/listing/en-US/phone-screenshot-01-alt.txt
play-store/listing/en-US/phone-screenshot-02-alt.txt
play-store/listing/en-US/phone-screenshot-03-alt.txt
play-store/listing/en-US/phone-screenshot-04-alt.txt
```

日本語の feature graphic 代替テキストは次のファイルから入力します。

```text
play-store/listing/ja-JP/feature-graphic-alt.txt
```

日本語のスクリーンショット代替テキストは次のファイルから入力します。

```text
play-store/listing/ja-JP/phone-screenshot-01-alt.txt
play-store/listing/ja-JP/phone-screenshot-02-alt.txt
play-store/listing/ja-JP/phone-screenshot-03-alt.txt
play-store/listing/ja-JP/phone-screenshot-04-alt.txt
```

## Privacy Policy URL

次の公開URLを privacy policy URL として使います。

```text
https://gist.github.com/Yos-K/23b876101848591692bc94a5f92dd024
```

このページには英語版と日本語版の両方を含めています。

## Data Safety

`docs/play-store-data-safety.md` を正とします。

回答:

```text
Does your app collect or share any of the required user data types?
No

Does your app share user data with other companies or organizations?
No

Data types:
No data types selected

Data encryption in transit:
Not applicable

Data deletion request mechanism:
Not applicable for collected data
```

理由:

MdLite Reader v0.1.0 には広告、解析 SDK、ログイン、自動クラッシュ送信、
`android.permission.INTERNET` 権限がありません。Markdown 本文、ファイル名、
ファイルパス、Android document URI、最近開いたファイルのメタデータを端末外へ
送信しません。

## クローズドテスト

クローズドテストの運用は `docs/closed-testing-guide.ja.md` を使います。
Google Play Developer API で AAB をアップロードする場合は
`docs/play-developer-api.ja.md` を使います。

テスター募集文:

```text
play-store/testing/closed-test-invitation.ja.txt
```

フィードバックテンプレート:

```text
play-store/testing/closed-test-feedback-template.ja.txt
```

クローズドテストには署名済みリリース AAB を使います。

```text
~/AndroidDev/releases/mdlite-reader/v0.1.0/mdlite-reader-0.1.0-release.aab
```

## 申請前確認

- privacy policy URL をプライベートブラウザで開けることを確認する。
- アップロードするスクリーンショットに個人情報が写っていないことを確認する。
- アップロードする AAB が最新の署名済みリリースビルドであることを確認する。
- Data safety の回答が APK/AAB の権限と一致していることを確認する。
- 公開判断まではリポジトリを private のままにする。

## パッケージ名確認トークン

Play Console で「アップロードされた APK/AAB に必須のトークン ファイルがありません」
と表示された場合は、Play Console に表示される snippet を次のローカルファイルへ
貼り付けます。

```text
src/main/assets/adi-registration.properties
```

その後、リリース AAB を作り直してアップロードします。

```sh
scripts/build-release-aab.sh
```

このトークンファイルは Git の追跡対象外にしています。コミットしません。
