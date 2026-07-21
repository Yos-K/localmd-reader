# GitHub Actions CI/CD

このプロジェクトでは GitHub Actions のワークフローを使います。継続的
インテグレーション（`ci.yml`）、ミューテーション解析（`mutation.yml`）、
手動のデバイススモークテスト（`device-smoke.yml`）、手動のテーマスクリーンショット
（`theme-screenshots.yml`）、手動の Play リリース（`play-release.yml`）です。

リリースモデルは次の方針です。

- **Free Play release**: 実際に Google Play へ出す `io.github.yosk.mdlite`。
- **Pro preview**: Pro機能の動作確認用 artifact。別の有料アプリとしては
  Google Play にアップロードしません。

Playアプリは無料のままにし、Proはアプリ内の権限解除として扱います。

## CI

`.github/workflows/ci.yml` は pull request と `main` への push で実行します。

高速な `fitness` job は、Android ビルドなしで pull request を早期に失敗させる
ソースレベルのガードを実行します。

```sh
sh scripts/check-file-sizes.sh           # 1ファイルの行数上限
sh scripts/check-hard-constraints.sh     # INTERNET権限なし・reader WebViewのJS無効を保証
sh scripts/check-no-committed-secrets.sh # keystore/鍵/service-accountのコミットなしを保証
sh scripts/check-docs-currency.sh        # ドメイン/ハーネス/ポリシー変更時に対応ドキュメント更新を保証（統合）
```

`test` job は Java、Android SDK Platform 35、Build Tools 35.0.0 を入れてから、
次を実行します。

```sh
sh test.sh
```

`test` job は、チャンネルごとに分けた2つの debug APK artifact を保存します。
artifact 名にはチャンネル・version name・version code・短縮コミットSHA が含まれ、
どれをインストールすべきか一目で分かります。

- `localmd-reader-free-debug-v<versionName>-<versionCode>-<shortSha>`
- `localmd-reader-pro-preview-debug-v<versionName>-<versionCode>-<shortSha>`

CI 実行ページの **Summary** タブ → **Artifacts** からDLします。Play向けビルドは
`free`、Pro挙動の確認は `pro-preview` を選びます。

`gradle-build` job では Gradle 経由でも次を確認します。

- Free / Pro Preview の unit test
- Free debug の Android lint
- Free / Pro Preview の debug APK build
- Free release AAB build

Free と Pro Preview のテストは別々の Gradle 呼び出しで実行し、一方の flavor で蓄積した
Robolectric の状態が次の flavor のテストワーカーメモリを消費しないようにします。各 Gradle
`Test` task は同時ワーカー数1、最大ヒープ1536 MB、20テストクラスごとのワーカー再起動を
適用します。`scripts/test-gradle-test-memory-policy.sh` がこの上限とCIステップの分離を検査します。

### Gradle wrapper

`gradle-build` は system の `gradle` ではなく、リポジトリに同梱した wrapper
(`./gradlew`、バージョンは `gradle/wrapper/gradle-wrapper.properties` で固定) 経由で
Gradle を実行します。これにより CI・ローカル・Play release workflow の間で Gradle
バージョンを再現可能にします。

shell ベースのまま維持しているもの（Termux 手書きスクリプト。今回の変更では廃止しない）:

- `test` job: `sh test.sh`（unit test と品質ゲート）と `sh build.sh`
  （Free / Pro Preview の debug APK）。
- `Play Release` のデフォルト経路: `build_system: script` は引き続き
  `scripts/build-signed-release.sh` と bundletool で署名済み AAB を生成します。
  Gradle 経路 (`build_system: gradle`) は opt-in で `./gradlew` を使い、Play への
  直接アップロードは script ビルドとの突合検証が済むまで gate されたままです。

## Artifacts（成果物）

CI とリリースワークフローはビルド成果物を GitHub Actions の artifact として公開します。
ワークフロー実行を開き、実行サマリ下部の **Artifacts** からダウンロードします。

| Artifact 名のパターン | 成果物 | 生成元 | タイミング |
|---|---|---|---|
| `localmd-reader-free-debug-v<ver>-<code>-<sha>` | Free debug APK | CI の `test` ジョブ | 毎 PR / push |
| `localmd-reader-pro-preview-debug-v<ver>-<code>-<sha>` | Pro preview debug APK | CI の `test` ジョブ | 毎 PR / push |
| `localmd-reader-free-play-aab-v<ver>-<code>-<sha>` | Free 署名済リリース AAB | Play Release の `build-release-aab` ジョブ | 手動実行・`channel: free-play` |
| `localmd-reader-pro-preview-artifact-aab-v<ver>-<code>-<sha>` | Pro preview 署名済リリース AAB | Play Release の `build-release-aab` ジョブ | 手動実行・`channel: pro-preview-artifact` |

`<ver>` = version name、`<code>` = version code、`<sha>` = コミット SHA 先頭7文字
（「Resolve build metadata」ステップが `scripts/version-env.sh` から解決）。

- **名前の安定性**: 命名スキームは固定で、version bump で変わるのは埋め込みの `<ver>`/`<code>` のみ。
  各ビルドは一意かつ予測可能に命名され、バージョン間で名前が衝突しない。
- **資格情報**: CI の debug APK は Play Console 資格情報不要。リリース AAB ジョブは、`upload_to_play` を
  要求しない限り Play にアップロードせず署名済 AAB を artifact として生成する（アップロード可能なのは `free-play` のみ）。

ダウンロード手順:

- PR の場合: その CI チェック → CI 実行 → **Artifacts**。
- リリース AAB の場合: Actions タブ → Play Release → 該当実行 → **Artifacts**。

## Device Smoke

**共有 setup（emulator 系ワークフロー共通）:** emulator を起動する6本
（`device-smoke` / `gesture-smoke` / `smoke-render-l5` / `theme-screenshots` /
`visual-regression-check-limited` / `exploration-emulator`）は、同一の setup を
composite action `.github/actions/emulator-build-setup` に集約している——メタ解決・
Java/Gradle セットアップ・KVM 有効化・指定 `flavor`（Free / ProPreview）の debug
APK ビルド。ビルドは runner 由来の `gradle` ではなく、リポジトリに固定した
Gradle wrapper（`./gradlew`）で実行する。これにより AGP との互換性は
チェックイン済み wrapper のバージョンに従う。各ワークフローは固有の
emulator-runner ステップ・アップロード・サマリだけを持つ。setup の修正が6つの
ほぼ同一コピーに散らず1箇所で済む（この重複が device-smoke と smoke-render-l5 を
2026-06-12 まで静かに壊していた）。

**emulator-runner の script 制約（smoke 系ワークフロー共通）:**
`reactivecircus/android-emulator-runner` は `script:` の各行を**別々の `sh -c`**で
実行するため、シェル変数・関数定義は行をまたいで持続しない（gesture-smoke
run 27285682358 で実測。device-smoke と smoke-render-l5 はこの問題で 2026-06-12 まで
壊れていた）。各行は自己完結にすること——成果物ディレクトリは変数に入れず
インライン展開し、補助処理は1文に畳み込む。

`.github/workflows/device-smoke.yml` は手動実行専用です。Free debug APK をビルドし、
Android 16（API 36）エミュレータを起動して APK をインストールし、`scripts/emulator-smoke.sh`
を実行します。スモークのラダーを進み、最初の問題で失敗します。

- **L2 launch**: `MainActivity` を起動し、プロセスが生きていることを確認。
- **L3 single-file intent**: `OPEN_TEXTS` インテント（`scripts/mdlite-open.sh` と
  同じ、本文を base64 で渡す方式）で Markdown を1件開き、クラッシュしないことを確認。
- **L4 multiple-file intent**: Markdown を2件タブとして開き、クラッシュしないことを確認。

fixture は `scripts/smoke-fixtures/` にあります。エミュレータの実行が PR CI に
組み込めるほど安定するまで、本ワークフローは手動のままにします。

実行ごと（成功・失敗いずれも）に `smoke-artifacts/logcat.txt` とスクリーンショットを
収集し、`device-smoke-evidence-…` artifact としてアップロードし、実行サマリ
（package・channel・version・commit・結果）をワークフローサマリに書き出します。
これにより、失敗を install / launch / intent open / render / crash のどれかに
ローカル再現なしで分類できます。device smoke はリポジトリ内の fixture だけを開き、
アプリは INTERNET 権限を持たず文書内容をログ出力しないため、logcat にユーザー文書や
シークレットを含めない設計です。

## Harness ROI

`.github/workflows/harness-roi.yml` は週次および手動実行で動く advisory job です。
次の3つの信号を workflow summary と `harness-roi-report` artifact に残します。

- `scripts/collect-gate-history.sh` による論理ゲート別の発火実績・CIコスト
- `scripts/test-balance-report.sh` によるテストピラミッド比率
- `scripts/measure-complexity.sh` による複雑度・重複ホットスポット

この job は非ブロッキングです。テスト削除、必須ゲート降格、branch protection 変更は
自動実行しません。出力はハーネスROIフレームワークの keep / strengthen / consolidate /
downgrade-to-advisory / removal-candidate 判断の証拠として使い、削除・降格は人間が承認した
PRで行います。

## Branch Protection

`main` は保護ブランチです。直接 push せず、必ず feature branch から pull request
を作成します。

`main` に merge する前に、次の check が成功している必要があります。

- `fitness`（高速なソースレベルガード: 1ファイル行数、Hard Constraints）
- `test`
- `gradle-build`
- `mutation`

merge 前に pull request が必須です（必須承認数は0なので個人開発は自己承認で
詰まりませんが、`main` への直接 push は拒否されます）。base branch は最新化されて
いる必要があります。force push と branch deletion は禁止します。conversation は
merge 前に解決します。保護は管理者にも適用されます。

保護ポリシーは再現可能です。再適用・監査は次を使います。

```sh
sh scripts/setup-branch-protection.sh Yos-K/localmd-reader main
```

通常の作業開始は次を使います。

```sh
sh scripts/start-work.sh feature/short-topic
```

作業後、ローカル確認と pull request 作成は次を使います。

```sh
sh scripts/open-pr.sh "type: short title"
```

commit message と pull request title は Conventional Commits に従います。pull
request では `fitness` job が `scripts/check-conventional-title.sh`（ローカルの
`scripts/open-pr.sh` と同じチェック）でタイトルを検証するため、規約に従わない
タイトルは必須チェックで失敗します。

## Play Release

`.github/workflows/play-release.yml` は手動実行専用です。

ビルド前に、1つの preflight コマンドで簡潔な pass/fail 要約を出力し、問題が
あれば早期に失敗します。

```sh
sh scripts/release-preflight.sh
```

preflight はソースレベルのリリースチェックを集約します: version 整合性、release
notes の存在と文字数制限（`play-store/release-notes/en-US/whatsnew.txt` および
`ja-JP/whatsnew.txt` を `scripts/check-release-notes.sh` が sh+awk で検証 ―
python3 非依存・Termux 互換。各ファイル 500 字以内を要求。`docs/release/*.md`
および `*.ja.md` の存在も確認）、Hard Constraints（INTERNET 無し・reader WebView
の JS 無効）、コミット済みシークレット無し、third-party notices、free アップロード
package ID（`io.github.yosk.mdlite`、manifest と `app/build.gradle` で一致を検証）、
Gradle フレーバーの suffix（`.pro` / `.debug`）、Play アップロードの free 限定ガード。
デフォルトの script リリース経路は全チャンネルで `io.github.yosk.mdlite` をビルドし、
pro preview はアップロードされないため、Play に到達する package は free のみです。
APK レベルのチェックはビルド済み APK に対して後段で実行されます。

その後、既存のリリーススクリプトで署名済み AAB を作成します。

```sh
sh scripts/build-release-aab.sh
```

チャンネルは次のどちらかを選びます。

- `free-play`: Play向けパッケージの署名済み release AAB。
- `pro-preview-artifact`: Pro機能有効の確認用署名済み AAB。

Google Play にアップロードできるのは `free-play` だけです。
`pro-preview-artifact` で `upload_to_play` を有効にした場合は、アップロード前に
失敗させます。

デフォルトでは AAB をビルドして artifact に保存するだけです。
Google Play にアップロードする場合だけ `free-play` で `upload_to_play` を
`true` にします。

AAB artifact は
`localmd-reader-<channel>-aab-v<versionName>-<versionCode>-<shortSha>` という名前で、
ワークフロー実行の **Artifacts** からDLできます。

各実行は、サマリ（package・channel・build system・version・commit・track・Play
アップロードを実行したか/スキップしたか）をワークフローサマリに書き出すため、
ログを読まずに何が出荷されたか確認できます。

デフォルトのトラックは、現在のクローズドテストに合わせて `alpha` です。

`changes_not_sent_for_review` はデフォルトで `false` にしています。現在このアプリでは
Play Console が変更を自動で審査送信する状態で、`true` を指定すると Android Publisher
API の commit が拒否されるためです。

このワークフローでは release status を `completed` と `draft` に絞っています。
段階的ロールアウト用の `inProgress` は、配信割合や停止・再開ルールを決めてから
別ワークフローとして追加します。

## Play アップロードの標準確認手順

通常のsource CIは公開`Yos-K/localmd-reader`で実行し、署名とPlayへのuploadは
非公開`Yos-K/localmd-reader-release`だけで実行する。ローカルremoteは公開sourceを
`origin`、非公開releaseを`release`とする。

Free 版を Play Console にアップロードする場合は、workflow を直接叩かず、
次のラッパーを使います。

```sh
sh scripts/play-upload-free-github-actions.sh Yos-K/localmd-reader-release main alpha
```

このスクリプトは Play Release workflow を `free-play` / `alpha` /
`upload_to_play=true` / `changes_not_sent_for_review=false` で起動し、run id と
確認コマンドを出力します。

実行中は次で監視します。

```sh
gh run watch RUN_ID --repo Yos-K/localmd-reader-release --interval 20
```

失敗時は次で、run 概要・失敗 job・失敗ログをまとめて確認します。

```sh
sh scripts/github-run-report.sh RUN_ID Yos-K/localmd-reader-release
```

Play upload preflight で止まった場合は、`play-console` Environment の
Variables/Secrets が不足しています。値はログに出さず、不足している名前だけを
表示します。

## GitHub Environments

次の2つの environment を作成します。

- `release-build`
- `play-console`

GitHub API 経由で作成できます。

```sh
sh scripts/setup-github-actions-repo.sh Yos-K/localmd-reader-release
```

artifact 作成だけのリリースビルドと Play アップロードの両方で保護された
署名情報を使いたい場合、release keystore 系の secrets は両方に登録します。

Google Cloud OIDC 用の variables は `play-console` だけに登録します。

`play-console` には、リポジトリのプランが対応していれば required reviewers を
設定します。GitHub は environment gate を通過するまで environment secrets を
job に渡しません。

required reviewers はスクリプトでは設定しません。個人開発では、自分だけを
必須レビュアーにするとリリースフローを詰まらせる可能性があるためです。

## 必要な GitHub Secrets と Variables

`upload_to_play` を要求した場合、`scripts/play-upload-preflight.sh` がリリースジョブの早い段階で実行され、
下記のいずれかが未設定なら（値は一切出力せず）不足している variable/secret 名を挙げて即座に失敗する。
署名済 AAB をビルドしてから認証・アップロードで失敗する無駄を防ぐ。

- `MDLITE_RELEASE_KEYSTORE_BASE64`: release keystore を base64 化した文字列。
- `MDLITE_RELEASE_KEY_ALIAS`: release key alias。
- `MDLITE_RELEASE_STORE_PASS`: release keystore password。
- `MDLITE_RELEASE_KEY_PASS`: release key password。
- `GCP_WORKLOAD_IDENTITY_PROVIDER`: Workload Identity Provider のリソース名。
- `GCP_SERVICE_ACCOUNT`: Play リリース用サービスアカウントのメールアドレス。

GitHub CLI で登録できます。

```sh
gh secret set MDLITE_RELEASE_KEYSTORE_BASE64 --env release-build --repo Yos-K/localmd-reader-release
gh secret set MDLITE_RELEASE_KEY_ALIAS --env release-build --repo Yos-K/localmd-reader-release
gh secret set MDLITE_RELEASE_STORE_PASS --env release-build --repo Yos-K/localmd-reader-release
gh secret set MDLITE_RELEASE_KEY_PASS --env release-build --repo Yos-K/localmd-reader-release

gh secret set MDLITE_RELEASE_KEYSTORE_BASE64 --env play-console --repo Yos-K/localmd-reader-release
gh secret set MDLITE_RELEASE_KEY_ALIAS --env play-console --repo Yos-K/localmd-reader-release
gh secret set MDLITE_RELEASE_STORE_PASS --env play-console --repo Yos-K/localmd-reader-release
gh secret set MDLITE_RELEASE_KEY_PASS --env play-console --repo Yos-K/localmd-reader-release
gh variable set GCP_WORKLOAD_IDENTITY_PROVIDER --env play-console --repo Yos-K/localmd-reader-release
gh variable set GCP_SERVICE_ACCOUNT --env play-console --repo Yos-K/localmd-reader-release
```

## Keystore Secret の作成

ローカルで次を実行し、出力を `MDLITE_RELEASE_KEYSTORE_BASE64` として登録します。

```sh
base64 -w 0 /path/to/mdlite-reader-release.jks
```

`-w` がない環境では次を使います。

```sh
base64 /path/to/mdlite-reader-release.jks | tr -d '\n'
```

keystore、service account JSON、Play Console token は絶対にコミットしません。
CI の認証は GitHub OIDC と Google Cloud Workload Identity Federation を使うため、
service account JSON key は不要です。

最後の砦として、`fitness` job が `scripts/check-no-committed-secrets.sh` を実行し、
keystore/鍵/service-account ファイルや PEM 秘密鍵が追跡された場合はビルドを失敗
させます。リリースワークフローはシークレットを環境変数経由でのみ渡し、値を echo
することはありません。
