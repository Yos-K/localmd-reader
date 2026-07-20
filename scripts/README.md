# scripts/ 索引（タスク → スクリプト）

## このドキュメントの目的

`scripts/` には多数のスクリプトがある。AIエージェントや開発者が「このタスクではどれを使うか」を、
全ファイルを読まずに特定できるようにするタスク別の索引。各スクリプトは失敗時に非ゼロ終了する。

環境制約: シェルは POSIX `sh`（Termux 前提）。Android ビルド系は SDK を要する（ローカルに無い場合は CI）。
`run-unit-tests.sh` は端末上の過大なヒープ確保を避けるため、JUnit JVMを最大1GBで実行する。
全体のハーネス設計は [`../docs/harness/agent-harness-design.md`](../docs/harness/agent-harness-design.md)、CI/リリースの詳細は
[`../docs/harness/github-actions-cicd.md`](../docs/harness/github-actions-cicd.md) を参照。

---

## ローカル開発フロー（まずここ）

| やりたいこと | スクリプト | 説明 |
|------|------|------|
| 作業ブランチを切る | `start-work.sh <branch>` | `main`/`master` を拒否して作業ブランチを作成 |
| 受け入れ基準をローカル検証 | `pr-preflight.sh ["<title>"]` | CI `fitness` ジョブのミラー（下記チェック＋pass/failサマリ）。PR前に実行 |
| PRを作る | `open-pr.sh "<type>: <title>" [body]` | `pr-preflight.sh` ＋ `test.sh` を通してから `gh pr create` |

`pr-preflight.sh` は SDK 不要・高速。`release-preflight.sh` はリリース前用で別物（後述）。

---

## fitness 検証（毎PRの受け入れ基準・SDK不要）

`pr-preflight.sh` が以下を束ねて実行する。個別にも呼べる。

| 受け入れ基準 | スクリプト |
|------|------|
| PRタイトルが Conventional Commits 準拠 | `check-conventional-title.sh "<title>"` |
| 1ファイル300行以内 | `check-file-sizes.sh` |
| INTERNET不在・reader WebViewのJS無効 | `check-hard-constraints.sh` |
| 鍵・keystore・PEM秘密鍵を未コミット | `check-no-committed-secrets.sh` |
| ドメイン規則・ハーネス・開発ルール・Free/Pro方針変更時に対応ドキュメントを同期 | `check-docs-currency.sh [base]` |
| `feat`/`fix`コミットがADR確認を宣言し、参照ADRが英日ペアと必須5節を持つ | `check-adr-review.sh [base]` |
| ユニットテスト依存jarの取得がHTTPエラー・一時破損を検出する | `check-unit-test-dependency-downloads.sh` |

`check-docs-currency.sh` は glossary と rule-doc を1本に統合した差分ベースガード（PR時のみ／base 既定
`origin/main`）。3レーンを検査する:

- **glossary**: `domain`/`model`/`viewer`/`file` の `.java` 変更 → `docs/domain/domain-glossary*.md`
- **harness**: `.github/workflows/*.yml`・preflight/check系・ブランチ保護・mutation・テスト比率 → `AGENTS.md`/`docs/harness/*.md`/`scripts/README.md`/architecture-package-policy
- **policy**: Free/Pro 境界の中心型（`ViewerFeature`/`FeatureEntitlement(s)`/`ProFeatureCatalog`） → `AGENTS.md`/`free-pro-feature-policy*.md`/glossary

変更種別ごとに該当クラスタを要求する（ハーネス変更に対し domain glossary だけ更新しても通らない）。規則に
無関係な変更は統一トレーラ `Docs-Impact: none (理由)` で全レーンをまとめて免除（旧 `Glossary-Impact:` /
`Rule-Docs-Impact:` も後方互換でレーン別に有効）。詳細は `AGENTS.md`「ドメイン規則と用語集の同期」「開発ルールとハーネス文書の同期」。

`check-adr-review.sh` はbaseからHEADまでの各 `feat:` / `fix:` コミットに、次のどちらか1つを要求する。

```text
ADR-Review: docs/adr/0003-markdown-library-navigation.md
ADR-Review: none (no architectural decision is affected because ...)
```

参照パスの存在、英日ペア、決定事項・選択肢・採用理由・不採用理由・見直し契機の必須節も検査する。
ガード自体のfixtureテストは `sh scripts/test-adr-review-guard.sh`、ローカルcommit-msgフックの
fixtureテストは `sh scripts/test-adr-commit-msg-hook.sh`。

## テスト・その他検証

| やりたいこと | スクリプト | 説明 |
|------|------|------|
| ユニットテスト一式＋debugビルド | `../test.sh` | version-check / run-unit-tests / test-smells / third-party-notices / build / release-basics を集約 |
| ユニットテストのみ | `run-unit-tests.sh` | JUnit 5 自動ディスカバリ。Maven jar は `curl --fail --retry` で取得し、`jar tf` で妥当性検査してから使用 |
| （内部）kt/java 混在コンパイル | `android-kotlin-compile.sh` | `run-unit-tests.sh`/`run-mutation-tests.sh` が source する共通ヘルパー（harness-kit v0.8.0〜）。純 Java でもコンパイルは本ヘルパー経由なので**必須の co-dependency**。直接実行はしない |
| テストスメル検査 | `check-test-smells.sh` | テストコードの品質静的解析 |
| third-party notices 整合 | `check-third-party-notices.sh` | バンドルassetのライセンス表記確認 |
| OSメタデータ混入検査 | `test-no-os-metadata.sh` | `.DS_Store` 等のOS/エディタメタデータが tracked にならず、`.gitignore` で再混入を防ぐことを検証 |
| L5描画スモーク契約の検査 | `test-smoke-render-contract.sh` | `smoke-render.md` がコードフェンス info metadata を含み、L5 assert が Preview/Raw トグル操作を検査することを検証 |
| Interaction Storming ガード検査 | `test-interaction-storming-guard.sh` | 画面状態に完了・脱出・回復コマンドが揃っていることを検査するガードの正常系/異常系を検証 |
| Interaction Model 検査 | `test-interaction-model-check.sh` | 状態・コマンド・遷移モデルが到達可能性と安定状態への復帰可能性を検出できることを検証 |
| Interaction Command Traceability 検査 | `test-interaction-command-traceability.sh` | モデル操作・実装入口・動作テストの欠落と locator のドリフトを検出できることを検証 |
| テストサイズ比率の集計 | `test-balance-report.sh [--strict]` | small/medium/large の比率を目安バンドと照合（[`test-strategy.md`](../docs/harness/test-strategy.md)）。`--strict` で逸脱を非ゼロ終了 |
| 探索ループの状況確認 | `exploration-status.sh` | 探索セッション台帳・クラスタカバレッジ・次チャーター候補シグナルを集計（advisory・常に exit 0） |
| 複雑度の測定 | `measure-complexity.sh [--strict]` | 循環的/認知的複雑度・偶有的複雑性の代理・重複を測定（[`code-quality-metrics.md`](../docs/harness/code-quality-metrics.md)・advisory） |
| 結合度の測定 | `measure-coupling.sh` | Ca/Ce/不安定度・SDP違反・Balanced Coupling・共変更ペアを測定（同上・advisory） |
| UI 探索の実行 | `explore-emulator.sh <monkey\|ops> ...` | エミュレータでのUI探索（monkey=シード付きランダム / ops=操作列+全ステップ証跡）。CI は `exploration-emulator.yml` |
| 消費スクリプトのドリフト検知 | `check-kit-drift.sh [--strict]` | `consumed_scripts` が pin した harness-kit と byte-identical か（diff-zero 不変条件）を検査。既定 advisory（exit 0・kit 到達不可時は SKIP）、`--strict` でドリフトを非ゼロ終了。CI は `harness-drift-check.yml`（非ブロッキング） |
| Actions artifact保持期限 | `check-artifact-storage-policy.sh` | 全`upload-artifact` stepに1〜7日の`retention-days`があることを検査 |
| 操作surface登録 | `check-interaction-surface-registration.sh` | 選択肢を持つAndroid UIが状態モデルのsurface IDを宣言していることを検査 |
| 操作コマンド追跡 | `check-interaction-command-traceability.sh` | `interaction-command-contracts.psv` のモデル遷移・実装入口・動作テストがすべて実在することを検査 |

`run-unit-tests.sh` と `run-mutation-tests.sh` が生成する `BuildConfig` は、通常の
軽量テスト fixture として `PRO_FEATURES_ENABLED=false`、
`PLAY_BILLING_ENABLED=false` を明示する。Pro/Billing 有効ビルドの確認は
debug/release build または CI artifact 側で行い、small test の既定 fixture とは混ぜない。

---

## ビルド（APK / AAB・SDK必要）

| やりたいこと | スクリプト | 説明 |
|------|------|------|
| Free debug APK | `build-free-debug-apk.sh` | Free フレーバーの debug APK |
| Pro preview debug APK | `build-pro-debug-apk.sh` | Pro preview フレーバーの debug APK |
| 既定ビルド | `../build.sh` | フレーバー指定でAPKを生成（CIが使用） |
| Free release AAB | `build-release-aab.sh` | Play 提出用 AAB |
| release APK | `build-release-apk.sh` | 署名前の release APK |
| 署名付き release | `build-signed-release.sh [apk|aab|all]` | keystore で署名（要 `MDLITE_RELEASE_KEYSTORE` ・bundletool） |
| Billing manifest 適用 | `apply-billing-manifest.sh` | Pro 課金用 manifest を反映 |

手書きビルドは `javac -parameters` を使用する。現行JDKがprivate内部クラスへ生成する名前なしの
合成パラメータを、Termux SDKの旧D8が処理できず内部エラーになることを防ぐためである。

---

## リリース（pre-upload 検証・バージョン・鍵）

| やりたいこと | スクリプト | 説明 |
|------|------|------|
| リリース前検証（1コマンド） | `release-preflight.sh` | version整合 / versionName再利用禁止 / release notes / hard constraints / secrets / notices / package ID / Play free限定ガード を集約しpass/fail要約。ソースレベルのみ |
| Play upload 前の環境変数検証 | `play-upload-preflight.sh` | アップロード必須の WIF variables ＋署名 secrets が env にあるか検証（値は出力しない）。不足名を挙げ即fail。Play Release が upload 時に実行 |
| リリースnotes存在・最新確認 | `check-release-notes.sh` | 現バージョンの notes 存在と stale 検知 |
| APKレベルの基本検証 | `check-release-basics.sh` | ビルド済みAPKの permission 等を検証 |
| バージョンを上げる | `version-bump.sh` | 通常リリース用。versionName と versionCode を同時に更新 |
| versionCode だけを上げる | `version-code-bump.sh` | 通常リリースでは使用禁止。明示的な緊急再ビルドのみ |
| バージョン整合確認 | `version-check.sh` | versionName/Code の整合性確認 |
| 現バージョン表示 | `version-show.sh` | 現在の versionName/Code を出力 |
| release keystore 作成 | `create-release-keystore.sh` | 署名鍵を生成（コミット禁止） |
| Play service account 鍵作成 | `create-google-play-service-account-key.sh` | アップロード用鍵を生成（コミット禁止） |

---

## Play Store 連携（python、要認証）

| やりたいこと | スクリプト |
|------|------|
| クローズドテストにアップロード | `play-upload-closed-test.py` |
| トラック一覧 | `play-list-tracks.py` |
| API セットアップ確認 | `play-check-api-setup.py` |
| ストア掲載文の更新 | `play-update-listing.py` / `play-update-listing-text.py` |
| スクリーンショット撮影/加工 | `capture-play-store-screenshot.sh` / `prepare-play-store-screenshot.sh` |
| アイコン/Feature Graphic 書き出し | `export-play-store-icon.sh` / `export-play-store-feature-graphic.sh` |

---

## スモーク（実機/エミュレータ・adb）

| やりたいこと | スクリプト | 説明 |
|------|------|------|
| エミュレータでスモーク | `emulator-smoke.sh` | adb で install→launch→Markdown Intent open を検証 |
| 描画内容をassert (L5) | `smoke-render-assert-l5.sh` | 表/コード/Mermaid の可視テキストを UIAutomator dump で検証 |
| ジェスチャーをassert (L5) | `smoke-gesture-l5.sh` | エッジスワイプ/メニュー閉/ダブルタップ/シェブロンを実タッチ経路で検証 |
| 視覚リグレッション簡易検査 | `visual-regression-check-limited.sh` | 全テーマでメニュー必須項目の存在を dump で検証（非ゲート） |
| 接続デバイスでスモーク | `smoke-debug-app.sh` | 接続中デバイスに対する軽量スモーク |
| debug アプリ起動 | `open-debug-app.sh` | インストール済み debug アプリを起動 |
| fixture を開く | `open-debug-fixture.sh` | デバッグ用 Markdown fixture を Intent で開く |
| fixture を生成 | `create-debug-markdown-fixture.sh` | スモーク用の Markdown を作成 |
| スクリーンショット取得 | `capture-debug-screenshot.sh` | debug 画面を撮影 |
| Termux から md を開く | `mdlite-open.sh FILE.md ...` | Termux から本アプリへ Intent で渡す |

---

## セットアップ（一度きり・環境構築）

| やりたいこと | スクリプト | 説明 |
|------|------|------|
| ブランチ保護を適用 | `setup-branch-protection.sh <owner/repo> <branch>` | 必須チェック＋PR必須化を再現可能に適用 |
| GitHub Actions 環境を構成 | `setup-github-actions-repo.sh` | release-build / play-console 等の environment を作成 |
| Android 依存を準備 | `prepare-android-dependencies.sh` | ビルドに必要な依存を取得（`android-dependency-env.sh` が環境変数を供給） |

---

## ハーネス自己評価（二階ループ・gatecrate 導入）

ハーネス自体が効いているかを計測し、効かない/過剰な層を剪定する「二階ループ」（gatecrate の
second-order loop）。重い harness を長期的に健全に保つための評価・修理ツール。`.takt/` のワークフロー
（`harness-evaluate-cycle` / `harness-liveness-converge`）と組み合わせて使う。初回評価は
[`../docs/evaluations/2026-06-15.md`](../docs/evaluations/2026-06-15.md)。

| やりたいこと | スクリプト | 説明 |
|------|------|------|
| 予防ゲートの生存証明 | `probe-gate-liveness.sh` | `harness.config.sh` の `PROBE_GATES` 各ゲートに合成違反を注入し拒否（ALIVE）するか確認。発火ゼロの予防ゲートが黙って壊れていないことを機械判定 |
| ゲートの発火/コスト集計 | `collect-gate-history.sh` | CI 実行履歴（`gh`）からゲート別 runs/fires/CI秒を集計（ROI 評価の入力） |
| テスト scaffold のコンパイル検査 | `check-test-compiles.sh` | ignored テストもビルドされるため scaffold が非コンパイルでないかを build-no-run で検査 |
| スクリプトの POSIX 移植性検査 | `check-posix-portability.sh` | 出荷スクリプトを `#!/bin/sh`・bashism なしに保ち bash/zsh/fish・Git Bash で動くことを機械保証 |

## 補足

- 画像処理用の Java ヘルパー（`ResizePng.java` / `StripImageMetadata.java` /
  `ExportPlayStoreIcon.java` 等）と環境変数供給用の `*-env.sh`、`fitness-exceptions.txt`
  （300行ルールの除外リスト）は上記タスクから間接的に使われる補助ファイル。
- 新しいスクリプトを追加したら、この索引の該当カテゴリにも1行追加すること。
