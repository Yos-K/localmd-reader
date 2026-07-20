# AGENTS.md

AIエージェント（Claude Code, Codex CLI等）がこのリポジトリで作業するための文脈ファイル。
人間が読む `README.md` とは別に、エージェントが**作業前に必ず確認すべき制約・規約・判断基準**をまとめる。

> **どの規則がどこで決まっているか**は [`docs/harness/canon-index.md`](docs/harness/canon-index.md)（正典インデックス）で1か所に引ける。新しい正典を作るときは、まずそこに行を足すこと。

---

## プロダクト概要

**LocalMD Reader** — プライバシーファーストのローカルMarkdownビューアー（Android）。

- 広告・トラッキング・ログイン・ネットワーク通信なし
- ローカルファイルのみ読む
- Termux環境からも開発可能

---

## 絶対に守るべき制約（Hard Constraints）

これらの制約はプロダクトのアイデンティティに直結する。**変更には強い根拠と人間の承認が必要**。

### 1. INTERNET permission を絶対に追加しない

`AndroidManifest.xml` に `android.permission.INTERNET` を追加してはならない。

- 理由: プライバシーファーストの設計を**アーキテクチャレベルで強制**するため
- 影響: 解析、クラッシュレポート、リモート設定、フォント読み込み等は全て不可
- 確認方法: 毎PRで `scripts/check-hard-constraints.sh`（CI `fitness`、Androidビルド不要）がマニフェストを検証。リリース時は `scripts/check-release-basics.sh` がビルド済みAPKの permission も検証

### 2. WebView の JavaScript を絶対に有効化しない

`webView.getSettings().setJavaScriptEnabled(false)` を維持する。

- 理由: 信頼できないMarkdownコンテンツに対するXSS対策
- 影響: インタラクティブなMarkdown拡張（一部）が使えない
- 例外: Mermaidレンダリング用に**別途**`MermaidJsRenderEngine`を使用（ユーザーコンテンツとは分離）
- 確認方法: 毎PRで `scripts/check-hard-constraints.sh` が `MainActivity` の reader WebView に `setJavaScriptEnabled(false)` があり `(true)` が無いことを検証

### 3. ファイル300行以内ルール

1ファイル300行以内に収める。超過した場合は責務別に分割する。

- 自動チェック: CIで `scripts/check-file-sizes.sh` が実行される
- 既知の例外: `MainActivity.java`（分割作業中、Issue/PRで段階的削減）

---

## 変更の受け入れ基準（マージ可能の条件）

エージェントが作る変更（PR）がマージされるには、以下を**すべて**満たす必要がある。多くはCIで**機械的に強制**され、違反すると必須チェックが落ちてマージできない。**作業前にこの基準を満たす前提で設計する**こと（CIで落ちてから気づくのではなく、先回りで従う）。

### マージのゲート（branch protection で強制）

- `main` への**直接pushは不可**。必ず feature branch から **PR経由**でマージする（必須承認は0人＝自己マージ可だが、PR自体と会話解決は必須）
- 必須チェック **`fitness` / `test` / `gradle-build` / `mutation`** がすべて成功
- ブランチが base（`main`）に**最新化**されている。レビュースレッド（会話）が**すべて解決済み**
- 強制は管理者にも適用。設定の再現・監査: `sh scripts/setup-branch-protection.sh Yos-K/localmd-reader main`
- **auto-merge レースの検知**: 全チェック緑＋未解決スレッドのみのPRに対応コミットをpushし直後にスレッドを解決すると、新コミットのCI完了を待たず**旧headでマージが発火**しコミットが漏れる（PR #80 で実発生。ブランチ保護では防げないGitHub仕様）。**運用ルール: レビュー対応コミットのpush後、スレッド解決は新コミットのCI完了後に行う**。漏れはマージ直後に `merge-integrity` ワークフロー（`.github/workflows/merge-integrity.yml`）が機械検知し、復旧手順（cherry-pick再提出）つきの issue を自動起票する

### `fitness` job が毎PRで検証（Androidビルド不要・高速ガード）

| 受け入れ基準 | 強制スクリプト |
|------|----------------|
| PRタイトルが Conventional Commits 準拠 | `scripts/check-conventional-title.sh` |
| `INTERNET` permission 無し / reader WebView の JS 無効 | `scripts/check-hard-constraints.sh` |
| 1ファイル300行以内 | `scripts/check-file-sizes.sh` |
| 鍵・keystore・service-account・PEM秘密鍵を**コミットしない** | `scripts/check-no-committed-secrets.sh` |
| ドメイン規則・開発ルール・CI/CD・ハーネス・Free/Pro方針の変更時は対応ドキュメントを同期（下記ルール） | `scripts/check-docs-currency.sh` |
| `feat:` / `fix:` コミットは実装前にADRを確認し、`ADR-Review:`で結果を宣言する | `scripts/check-adr-review.sh` |

### ドメイン規則と用語集の同期

ドメイン用語集（[`docs/domain/domain-glossary.md`](docs/domain/domain-glossary.md) ほか）は**ドメイン規則の正典**。
規則と実装が乖離すると、用語集が古くなりエージェントに誤った前提を与える（無いより有害）。**だから**、
規則を変える実装では用語集を同期する。**全実装が対象ではない**——以下のときだけ必須:

- 新しいドメイン型を追加した
- L1（語が単独で守る不変条件）/ L2（語と語の間の規則・ポリシー）/ L3（操作の契約）を追加・変更した

満たし方は2択。`scripts/check-docs-currency.sh`（CI `fitness`、PR時のみ。glossary/rule-doc 統合ガード）が
機械的に検出する。

- `docs/domain/domain-glossary*.md` の該当クラスタを更新する、**または**
- 規則に影響しない変更（バグ修正 / UI・presentation / 純粋リファクタ / リネーム / infra）なら、コミットに
  統一トレーラで宣言する: `Docs-Impact: none (理由)`（旧 `Glossary-Impact:` も後方互換で有効）

対象は `domain` / `viewer` / `file`（Android非依存）。`presentation` の変更はこのガードの対象外。

### 開発ルールとハーネス文書の同期

`AGENTS.md`、`docs/harness/*.md`、`docs/product/free-pro-feature-policy*.md`、
`docs/product/architecture-package-policy.md`、`scripts/README.md` は、エージェントと人間が作業判断に使う
**開発ルールの正典**。CI/CD、preflight、ブランチ保護、テスト戦略、Free/Pro境界などを変えたときに同期しないと、
次の作業者が古い手順で動く。

`scripts/check-docs-currency.sh`（CI `fitness`、PR時のみ。glossary と同じ統合ガード）は、次のようなルール保持
ファイルの変更を検出し、関連するルールドキュメントの更新が無ければ失敗する。無関係なドキュメント更新
（例: ハーネス変更に対して domain glossary だけを更新する）は同期済みとは扱わない。

- `.github/workflows/*.yml`
- `scripts/check-*.sh`、`scripts/*preflight*.sh`、`scripts/setup-branch-protection.sh`、
  `scripts/run-mutation-tests.sh`、`scripts/test-balance-report.sh` などのハーネス入口
- Free/Pro 境界の中心型（`ViewerFeature`、`FeatureEntitlement`、`FeatureEntitlements`、`ProFeatureCatalog`）

規則に影響しない純粋リファクタやコメント変更なら、コミットに統一トレーラで宣言する（旧 `Rule-Docs-Impact:`
も後方互換で有効。1つの `Docs-Impact:` で触れた全ドキュメントレーン＝glossary/harness/policy をまとめて免除）:

```text
Docs-Impact: none (pure refactor, no rule or workflow behavior change)
```

### Architecture Decision Record（ADR）の確認

作業開始時に [`docs/adr/README.md`](docs/adr/README.md) を読み、変更対象に適用されるADRを確認する。
特に、ユーザーの指摘を受けて画面構造、ナビゲーション、責務境界、永続化方式、セキュリティ境界、
ビルド経路を変える場合は、実装前にADRを新規作成、更新、または後継ADRで置換する。

ADRには必ず、決定事項、検討した他の選択肢、選択した理由、選択しなかった理由、決定を見直す契機を残す。
英語ADRを正典とし、同名の `.ja.md` を同期する。

すべての `feat:` / `fix:` コミットは、コミット本文に次のどちらかを1つだけ付ける。

```text
ADR-Review: docs/adr/0003-markdown-library-navigation.md
ADR-Review: none (no architectural decision is affected because the change only corrects copy)
```

`none` は理由なしでは使えず、関連ADRがある場合にも使わない。`scripts/check-adr-review.sh` が各コミットの
宣言、参照先、英日ペア、必須節をローカルpreflightとCI `fitness` の両方で検証し、未確認コミットを
マージ不能にする。

### `test` / `gradle-build` job が検証

- ユニットテスト全pass（`sh test.sh` / `./gradlew testFreeDebugUnitTest`）
- アーキテクチャのレイヤールール（ArchUnit。`domain` は Android 非依存 等、本ファイルのレイヤー間依存ルールに一致）
- テストスメル無し（`scripts/check-test-smells.sh`）／third-party notices 整合（`scripts/check-third-party-notices.sh`）
- Free / Pro Preview の debug ビルド・lint・Free release AAB ビルド

### リリース時に追加で満たす基準（手動 Play Release）

- 現バージョンの release notes（`docs/release/release-notes-v<ver>.md` と `.ja.md`）が存在し最新であること。Play Release ワークフローがビルド前に `scripts/check-release-notes.sh` で検証する
- Play にアップロードされるのは **free-play のみ**（pro-preview はワークフローのガードで拒否）
- リリースの検証手順・各チェックの詳細は `docs/harness/github-actions-cicd.md`（Play Release 節）を参照

> ハーネス全体（CI / リリース / デバイススモークの設計と検証）の詳細は `docs/harness/github-actions-cicd.md` を参照。ローカルでは `sh scripts/open-pr.sh "<type>: <title>"` がタイトル検証＋テスト実行してからPRを作る。

> **ミューテーションテスト**: `mutation` ジョブ（`.github/workflows/mutation.yml`）がロジック層変更時に検出力を測る。結果を分析してテストを強化し floor を上げる手順は `docs/harness/mutation-analysis-rule.md`（**SURVIVED を最優先で潰す／floor を下げて緑にしない**）。ローカルは `sh scripts/run-mutation-tests.sh`。

---

## ソフト制約（Soft Constraints, 強い推奨）

### 4. 外部依存を極小化する

新しいライブラリ追加は要検討。現状の外部依存:
- `com.android.billingclient:billing` (proPreviewのみ)
- `mermaid.min.js` (基本表示機能として assets にバンドル)
- `JUnit Jupiter` (テストのみ)

Markdownパーサーやテストアサーションも自作している。**「軽量・最小依存」が文化**。

### 5. Free/Pro分岐は型で表現する

`if (BuildConfig.PRO_FEATURES_ENABLED)` のような直接分岐ではなく、`FeatureEntitlement` + `EntitlementSource` で表現する。Free は基本機能を完整に提供し、Pro は快適化・効率化のための機能だけを `ProFeatureCatalog` に登録する。各用語の構成要素・不変条件・振る舞いは [`docs/domain/domain-glossary.md`](docs/domain/domain-glossary.md) を参照。

### 6. ドメイン層に Android依存を入れない

`io.github.yosk.mdlite.domain` パッケージから `android.*` をimportしてはならない。テストの高速化（JVMで実行可能）とドメインロジックの純粋性を守るため。

---

## アーキテクチャ

```
src/main/java/io/github/yosk/mdlite/
├── domain/          # 値オブジェクト、ポリシー、ビジネスルール (Android非依存)
├── infrastructure/  # 外部システム連携 (Markdown描画、Play Billing、永続化)
├── viewer/          # 表示モデル (Tab、ジェスチャー、テーマ)
├── file/            # ファイル操作 (Recent、Restorable)
└── presentation/    # Activity、UI構築 (Android依存)
```

### レイヤー間依存ルール

- `domain` ← `infrastructure`, `viewer`, `file`, `presentation`
- `domain` は他レイヤーをimportしない
- `presentation` のみ Android Framework を直接使用

---

## 開発ワークフロー

### 作業開始時（必須）

**コード・設定・ドキュメントを変更する作業は、必ず最初に作業用ブランチを作成してから始める。`main` 上で直接作業しない。**

```sh
git switch -c <type>/<short-topic>   # 例: test/medium-intent-open, fix/97-...
```

- **なぜ**: `main` への直接pushは branch protection で不可（[マージのゲート](#マージのゲートbranch-protection-で強制)参照）。`main` で作業を進めると、後からブランチへ退避する手間が生じ、`git stash`/`rebase` の競合や `.git/index.lock` 多発（IDE起因）と相まってコミット消失事故につながりやすい。**最初にブランチを切れば、`main` は常にクリーンに保て、PR単位で安全に積める。**
- **いつ**: 調査だけで終わらず1行でも変更する見込みになった時点で切る。会話のみ・読み取りのみの作業はブランチ不要。
- **ブランチ名**: コミット種別（`feat`/`fix`/`test`/`refactor`/`docs`/`chore`）を接頭辞にし、内容が分かる短い名前にする。
- **粒度**: 独立した変更は独立したブランチ＝独立したPRにする（「小さなPRを推奨順で」の方針）。
- **Gitフック**: `sh scripts/start-work.sh <branch>` はブランチ作成と同時に
  `.githooks/pre-commit`（シークレット検査＋300行検査）と `.githooks/commit-msg`
  （`feat`/`fix` のADR確認宣言）を有効化する。手動有効化は
  `git config core.hooksPath .githooks`。エージェント中立（Claude Code / Codex / 人間を等しく守る）。
  設計根拠は [`docs/harness/ecc-reference-evaluation.md`](docs/harness/ecc-reference-evaluation.md)。

### 機能PR完了時（推奨）: 探索ループの確認

機能PRをマージしたら `sh scripts/exploration-status.sh` を実行し、★シグナル
（変更が集中しているのに未探索/陳腐化のクラスタ）があれば、次の探索チャーター候補を
オーナーに提案する。運用ルール・トリガー・自己評価の様式は
[`docs/harness/exploratory-testing.md`](docs/harness/exploratory-testing.md) の「継続ループ」節を参照。
探索はマージゲートではない（提案ベース・flaky 厳禁の原則は同文書のとおり）。

### consumed_scripts を編集するとき（必須運用ルール）

`sync-manifest.yaml` の `consumed_scripts` に列挙されたスクリプト（harness-kit から消費している
もの）は、pin した `harness_kit_version` と **byte-identical** であることが同期機構の前提（diff-zero
不変条件）。**なぜ**: ここが破れると週次 `harness-sync` が逆向き diff を提案し、kit の古い内容で
ローカルの改善を上書きする PR が立つ（ドリフト窓9日でアダプタ27本が全乖離した実績）。

- **だから**: consumed_scripts を変更する PR は、同週内に harness-kit への還元 PR を対にし、新タグ発行
  後に `sync-manifest.yaml` の pin を上げる（手順: [`docs/harness-portability/harness-kit-migration-proposal.md`](docs/harness-portability/harness-kit-migration-proposal.md)）。kit に還元しない一時的な固有変更が必要なら、その
  スクリプトを `consumed_scripts` から外して「自前管理」に戻す。
- **可視化**: `harness-drift-check.yml`（**非ブロッキング advisory**・毎PR/週次）が
  `scripts/check-kit-drift.sh` でこの不変条件を再検査し、破れていれば Step Summary と警告
  アノテーションに出す。マージは止めない（還元 or 差し戻しの判断は人が行う）。ローカル確認は
  `sh scripts/check-kit-drift.sh`。

### ビルド・テスト

```sh
# Termux環境（Android SDK不要なテストのみ）
sh scripts/run-unit-tests.sh    # JUnit 5自動ディスカバリ

# Termux環境（フルAPKビルド）
sh build.sh

# CI環境（Gradle）
./gradlew testFreeDebugUnitTest
./gradlew assembleFreeDebug
./gradlew bundleFreeRelease
```

### コミットメッセージ規約

Conventional Commits を使用:
- `feat:` 新機能
- `fix:` バグ修正
- `refactor:` 振る舞いを変えない構造変更
- `test:` テスト追加・修正
- `build:` ビルド設定・CI変更
- `docs:` ドキュメント
- `chore:` その他

AIエージェントが作成したコミットには `Co-Authored-By: <agent-name> <noreply@anthropic.com>` を追加する。

**PRタイトルも Conventional Commits 準拠が必須**。CI `fitness` の `scripts/check-conventional-title.sh` が pull_request で検証し、非準拠は必須チェックで失敗する。

### PR作成

`main` への直接pushは不可。feature branch から PR を作る（`sh scripts/open-pr.sh "<type>: <title>"` がタイトル検証とテストを実行してから作成）。`.github/pull_request_template.md` のテンプレートに従う。特に:
- **Summary**: なぜこの変更が必要か
- **Test plan**: 何を検証したか
- **Hard Constraints チェック**: INTERNET permission追加していないか等

---

## よくある落とし穴（AIエージェント向け）

### 1. 「INTERNETを追加すれば簡単」と思った時

→ 絶対に追加しない。代替手段（バンドルアセット、ユーザー操作）を検討する。

### 2. 「BuildConfig.PRO_FEATURES_ENABLED で分岐」と思った時

→ `FeatureEntitlement.allows(ViewerFeature.XXX)` を使う。Pro機能カタログへの登録も忘れずに。

### 3. 「テストにAndroid依存を入れたい」と思った時

→ `Context` や `SharedPreferences` が必要なら、それは `domain` ではなく `infrastructure` か `presentation` のテストにすべきサイン。ドメインロジックを抽出してから純粋テストを書く。

### 4. 「ファイルが300行を超えた」時

→ CIで止まる前に分割する。`MainActivity.java` の分割パターン（PR #12）が参考になる: 責務別クラスを `presentation` パッケージ内に切り出し、package-privateフィールド経由で連携する。

### 5. 「外部ライブラリを追加したい」と思った時

→ まず自作で対応できないか検討する。既存パターン（`JavaSimpleMarkdownRenderer`、`TestAssertions`）に倣う。追加する場合はIssueで議論してから。

---

## ドキュメント体系

| 場所 | 内容 |
|------|------|
| `AGENTS.md`（このファイル） | AIエージェント向け制約・規約・**受け入れ基準** |
| `README.md` | 人間のユーザー向け説明 |
| `docs/harness/github-actions-cicd.md` | **ハーネス（CI/リリース/スモーク）の設計・検証の詳細** |
| `docs/harness/agent-harness-design.md` | エージェント向けハーネスの設計観点・ギャップ・受け入れ基準 |
| `docs/harness/mutation-analysis-rule.md` | **ミューテーション結果の分析・改善ルール**: status 別対応・mutator→テスト対応・floor の ratchet |
| `docs/domain/domain-glossary.md` | **ドメイン用語集（ユビキタス言語）**: 構成要素・同一性・不変条件・振る舞い |
| `scripts/README.md` | タスク→スクリプト索引（どのタスクでどれを使うか） |
| `docs/` | 詳細ドキュメント（設計、リリース手順、ドメイン方針等） |

---

重要な設計判断は [`docs/adr/README.md`](docs/adr/README.md) を正典とする。PR本文は個別実装の補助文脈であり、
ADRの代わりにはしない。
