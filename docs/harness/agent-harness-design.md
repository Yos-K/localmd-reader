# エージェント向けハーネス設計

## このドキュメントの目的

AIエージェントとチームが速く・安全に開発できるよう、ハーネス（CI / リリース / スモーク /
エージェント向けコンテキスト）が満たすべき4つの設計観点を定義し、現状・ギャップ・対応方針を
1か所に集約する。

読者（人間・AIエージェント）は、ここを見れば「ハーネスとして何が足りていて、何を次にやるべきか」を、
各判断の根拠（実ファイル）付きで把握できる。

進行中タスクの一覧は [`claude-harness-engineering-todo.md`](./claude-harness-engineering-todo.md)
を、エージェント向けの制約・受け入れ基準は [`../../AGENTS.md`](../../AGENTS.md) を参照。

---

## 4つの設計観点とギャップ分析

各観点を「ハーネス自体の受け入れ基準」とみなし、現状・ギャップ・対応を整理する。
**なぜ問題か → だから何をするか** をセットで記載する。

### 観点1: エージェント向けコンテキスト整備

- **現状（根拠）**: `AGENTS.md` がエージェントの起点として制約・規約・受け入れ基準まで記載。
  `scripts/` に約60本のスクリプトが存在する。
- **なぜ問題か**: スクリプトの索引が無いため、エージェントは「このタスクでどれを使うか」を
  約60ファイルを読まないと判断できず、誤ったスクリプト選択や再実装のリスクがある。
- **だから（対応）**: タスク種別（build / test / release / Play / fitness / smoke / version）→
  スクリプトの対応表 `scripts/README.md` を新設し、`AGENTS.md` から参照する。
- **状態**: DONE（本PR。`scripts/README.md` 追加）

### 観点2: CI/CD・品質基盤の設計

- **現状（根拠）**: `.github/workflows/ci.yml`（`fitness` / `test` / `gradle-build`）、
  `device-smoke.yml`、`play-release.yml`。`scripts/setup-branch-protection.sh` で保護を
  再現可能化し、fitnessガード（タイトル / 300行 / hard constraints / secrets）を必須化済み。
- **なぜ問題か**: `scripts/open-pr.sh` はPRタイトル検証とテストしか実行せず、fitnessガード
  （`check-hard-constraints.sh` / `check-file-sizes.sh` / `check-no-committed-secrets.sh`）を
  ローカルで走らせない。エージェントはCIで初めて落ちて手戻りする。
- **だから（対応）**: CI `fitness` ジョブのローカルミラー `scripts/pr-preflight.sh` を新設し、
  `open-pr.sh` に組み込んでローカルPRフローでfitnessゲート全体を走らせる。
- **状態**: DONE（本PR）

### 観点3: AI出力の受け入れ基準定義

- **現状（根拠）**: `AGENTS.md`「変更の受け入れ基準（マージ可能の条件）」節で、branch
  protection と各 fitness スクリプトによる機械的強制を明文化済み。
- **なぜ問題か**: 基準は文書化されているが、エージェントがマージ前に「自分の変更が基準を
  満たすか」を確認するローカル動線が弱い（観点2と表裏一体）。
- **だから（対応）**: `scripts/pr-preflight.sh` が受け入れ基準のローカル代理ゲートとなる。
  緑なら CI `fitness` が通る、という1コマンドの自己検証を提供する。
- **状態**: DONE（本PR）

### 観点4: ドメイン知識の文書化

- **現状（根拠）**: `architecture-package-policy.md` / `free-pro-feature-policy.md` /
  `play-billing-design.md` / `pro-development-context.md` / `markdown-parser-migration-criteria.md`
  等、ドメイン文書は豊富。
- **なぜ問題か**: ユビキタス言語（中核ドメイン用語）の用語集が無く、`FeatureEntitlement` 等が
  各文書とコードに散在する。共通規約「専門用語が5個以上なら用語集を別ファイルに分離」を
  未充足で、エージェントが用語の正典を1か所で引けない。
- **だから（対応）**: `docs/domain-glossary.md` を新設し、実コードを出典として中核用語を
  構成要素・同一性・不変条件・振る舞い付きで集約、`AGENTS.md` から相互リンクする。
- **状態**: DONE（本PR。`docs/domain-glossary.md` 追加）

---

## 対応サマリ

| 設計観点 | 必要なハーネス設計 | 成果物 | 状態 |
|------|------|------|------|
| エージェント向けコンテキスト整備 | タスク→スクリプト索引 | `scripts/README.md` | DONE |
| CI/CD・品質基盤の設計 | 受け入れ基準のローカル一括検証 | `scripts/pr-preflight.sh` | DONE |
| AI出力の受け入れ基準定義 | 基準のローカル代理ゲート（fitnessミラー） | `scripts/pr-preflight.sh` ＋ `open-pr.sh` 統合 | DONE |
| ドメイン知識の文書化 | ユビキタス言語の用語集 | `docs/domain-glossary.md` | DONE |

---

## 受け入れ基準（ハーネスとして満たすべき状態）

- エージェントは1つのローカルコマンド `sh scripts/pr-preflight.sh "<title>"` で、CIが強制する
  Androidビルド不要の受け入れチェックをすべて再現でき、緑なら `fitness` ジョブが通る。
- エージェントは単一の索引からタスクに対応するスクリプトを特定でき、`scripts/` 全体を
  走査しなくてよい。
- `AGENTS.md` とコードで使われるドメイン用語が、1つの正典（用語集）に解決する。

---

## 実装メモ: `scripts/pr-preflight.sh`

CI `fitness` ジョブ（`.github/workflows/ci.yml`）と、Android SDK不要の関連ガードを実行する。

1. Conventional Commits タイトル（引数を渡したときのみ。CIはPRタイトルを検証）
2. 1ファイル300行以内（`check-file-sizes.sh`）
3. Hard Constraints: INTERNET不在・reader WebViewのJS無効（`check-hard-constraints.sh`）
4. コミット済みシークレット走査（`check-no-committed-secrets.sh`）
5. ドキュメント同期（`check-docs-currency.sh`）
6. テストスメル（`check-test-smells.sh`）
7. テスト依存取得契約（`check-unit-test-dependency-downloads.sh`）
8. Actions artifact保持期限（`check-artifact-storage-policy.sh`）
9. 操作フロー完全性（`check-interaction-storming.sh`）
10. 操作状態モデル（`check-interaction-model.sh`）
11. 操作コマンドのモデル・実装・動作テスト追跡（`check-interaction-command-traceability.sh`）
12. 操作surface登録（`check-interaction-surface-registration.sh`）

ユニットテストとGradleビルド（CI `test` / `gradle-build` ジョブ）はAndroid SDKを要するため
意図的に含めない。preflight は「ビルド不要・高速ガード」のローカル再現に限定する。

各チェックの出力を表示したうえで、最後に PASS / FAIL / SKIP のサマリを出し、1つでも
FAIL があれば非ゼロ終了する（CIが拒否する変更をローカルで先に検知する）。

なお `scripts/release-preflight.sh`（リリース前のpre-upload検証: version整合 / release notes /
package ID / Play upload free限定ガード等）とは目的が異なる。`pr-preflight.sh` は**PR前**の
fitnessゲート、`release-preflight.sh` は**リリース前**のゲートで、両者は補完関係にある。
