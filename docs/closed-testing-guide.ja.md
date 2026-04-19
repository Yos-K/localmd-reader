# クローズドテストガイド

この文書は MdLite Reader v0.1.0 のクローズドテスト運用を定義します。

## 目的

Google Play のクローズドテストを使って、Production access の要件を満たし、
初回の Play 配布ビルドが実機で動作することを確認します。

## 要件

新しい個人 Google Play デベロッパーアカウントでは、Production access の前に
次が必要になる場合があります。

- クローズドテストトラック。
- 12人以上のテスターが opt-in している。
- テスターが14日間連続で opt-in 状態を維持する。
- テスト期間後に Production access を申請する。

Internal testing はこの要件を満たしません。

## テストビルド

署名済みリリース AAB をクローズドテストトラックへアップロードします。

```text
~/AndroidDev/releases/mdlite-reader/v0.1.0/mdlite-reader-0.1.0-release.aab
```

パッケージ所有権確認後に AAB を作り直した場合は、もう一度 staging します。

```sh
mkdir -p ~/AndroidDev/releases/mdlite-reader/v0.1.0
cp build/release/mdlite-reader-0.1.0-release.aab ~/AndroidDev/releases/mdlite-reader/v0.1.0/mdlite-reader-0.1.0-release.aab
sha256sum ~/AndroidDev/releases/mdlite-reader/v0.1.0/mdlite-reader-0.1.0-release.aab > ~/AndroidDev/releases/mdlite-reader/v0.1.0/SHA256SUMS
```

## テスター準備

推奨するテスターリスト:

- MdLite Reader テスト専用の Google Group またはメールリストを使う。
- 14日間の開始前に12人以上を追加する。
- テスターには Play のテストリンクから opt-in してもらう。
- 14日間が終わるまで opt-out しないよう依頼する。

## フィードバック窓口

窓口は一つに絞ります。

- Google Form。
- メールアドレス。
- リポジトリを公開している場合は GitHub Discussion または issue。

テスターに個人的な Markdown ファイルの送付は求めません。個人情報を含まない短い
説明やスクリーンショットで報告してもらいます。

## テスト範囲

テスターには次を確認してもらいます。

- Google Play からインストールできる。
- アプリが起動する。
- ローカルの `.md` / `.markdown` ファイルを開ける。
- 本文、見出し、リスト、リンク、テーブル、コードブロックが読める。
- タブを開閉できる。
- ライト/ダークテーマを切り替えられる。
- ピンチでフォントサイズを変更できる。
- プライバシーダイアログを開ける。

## Production access 申請メモ

14日間の間に次を記録します。

- opt-in したテスター数。
- 開始日と終了日。
- 確認した端末や Android バージョン。
- 見つかった問題と修正内容。
- Production に進めると判断した理由。

これらの記録は Production access 申請時に利用できます。
