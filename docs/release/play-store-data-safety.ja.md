# Play Store Data Safety

この文書は LocalMD Reader v0.1.0 の Google Play Data safety 申告方針を定義します。

2026-04-15 に確認した公式要件:

- Google Play Data safety form:
  https://support.google.com/googleplay/android-developer/answer/10787469
- Google Play User Data policy:
  https://support.google.com/googleplay/android-developer/answer/10144311

## アプリ概要

LocalMD Reader v0.1.0 はローカルファーストの Android Markdown ビューアです。

このアプリは次の方針です。

- 広告なし。
- 解析 SDK なし。
- ログインやアカウントなし。
- 自動クラッシュ送信なし。
- `android.permission.INTERNET` 権限なし。
- Markdown 本文をアップロードしない。
- ファイル名、ファイルパス、Android document URI、最近開いたファイルのメタデータを端末外へ送信しない。

## Data Safety Form の回答

Data collection and security:

```text
Does your app collect or share any of the required user data types?
No
```

理由:

Google Play では、collection はユーザーデータをアプリから端末外へ送信することを
指します。LocalMD Reader v0.1.0 はネットワーク権限を要求せず、ユーザーデータを
送信しません。

Data sharing:

```text
Does your app share user data with other companies or organizations?
No
```

理由:

LocalMD Reader v0.1.0 は、ユーザーデータを開発者、第三者サービス、SDK、その他の
組織へ送信しません。

Data types:

```text
No data types selected
```

理由:

Google Play が定義するユーザーデータ型を収集または共有しません。

Data encryption in transit:

```text
Not applicable
```

理由:

ユーザーデータを収集または送信しません。

Data deletion request mechanism:

```text
Not applicable for collected data
```

理由:

アプリはデータを収集しません。端末内の app-private preferences はアプリデータの
削除で消去できます。最近開いたファイルの履歴はアプリ内からもクリアできます。

## 収集されないローカルデータ

アプリは app-private storage に小さな設定を保存します。

- テーマ設定。
- 操作部品の配置設定。
- UI 言語設定。
- 最近開いたファイルのメタデータ、最大 5 件。
- タブ復元用の開いていたタブのメタデータ。

最近開いたファイルとタブのメタデータには、表示名と Android document URI が
含まれる場合があります。このデータは端末上に留まり、アプリによって送信されません。

## Privacy Policy URL

次の公開URLを privacy policy URL として使います。

```text
https://gist.github.com/Yos-K/23b876101848591692bc94a5f92dd024
```

リリース要件:

- 開発中はリポジトリを private のままにする。
- Play Store 申請中とリリース後も privacy policy URL を公開状態に保つ。

## アプリ内プライバシーテキスト

v0.1.0 では、個人情報またはセンシティブデータの収集、送信、同期、販売、共有を
行わないため、prominent disclosure や同意ダイアログは不要です。

アプリ内では、ハンバーガーメニューからローカルのプライバシーテキストを確認できます。

```text
メニュー -> プライバシー
```

このダイアログはローカルテキストです。v0.1.0 は `INTERNET` 権限を要求しないため、
ネットワーク URL は開きません。

## 変更管理

将来のバージョンで次を追加する場合は、リリース前にこの文書、`PRIVACY.md`、
`PRIVACY.ja.md`、Play Console Data safety form を更新します。

- ネットワークアクセス。
- 解析。
- 広告。
- ログインまたはアカウント。
- 同期。
- クラッシュレポート。
- リモート画像読み込み。
- クラウドストレージ連携。
- ユーザーデータを収集または共有する SDK。
