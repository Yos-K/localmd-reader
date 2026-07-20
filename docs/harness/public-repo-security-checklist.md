# 公開リポジトリ安全性チェックリスト

このドキュメントは、公開ソースリポジトリ（localmd-reader）と非公開リリースリポジトリ
（localmd-reader-release）の境界、および公開前のセキュリティ確認手順をまとめたものである。
読者はリリース担当者・AI エージェントの両方を想定する。

**最大リスク**: シークレット（keystore・認証キー・パスワード）のコミットまたはワークフローログへの漏洩。
発覚後の対応（ローテーション・git 履歴からの除去）は非常にコストが高い。**事前防止が最優先**。

---

## 1. 目的と背景

公開ソースとリリース運用で問題になるのは主に以下の 3 点：

1. **ログ漏洩**: GitHub Actions のワークフローログにシークレット値が出力された場合、
   ログ閲覧権限を持つ全員に露出する（公開リポジトリでは全インターネット）。
2. **コミット漏洩**: keystore・.env・サービスアカウント鍵をコミットすると、
   git 履歴に永続化され、削除後も fork・キャッシュ経由で流通しうる。
3. **履歴漏洩**: 現在の tree が安全でも、過去 branch・PR・tag に個人情報や内部資料があれば、
   リポジトリ公開時にそれらも公開される。

cmd_007 の教訓（後述 セクション 3 参照）から、
**マージ前のシークレット走査ゲートを必ず通す**ことを原則とした。

### 1-1. リポジトリ境界

| 用途 | リポジトリ | 公開範囲 |
|------|------------|----------|
| ソース、通常 CI、Issue、PR | `Yos-K/localmd-reader` | Public |
| 署名 build、Play upload、release secrets | `Yos-K/localmd-reader-release` | Private |

公開側の workflow は署名鍵や Play 認証を参照しない。リリース操作の既定値は非公開側とし、
`sh scripts/test-release-repository-boundary.sh` で退行を検出する。決定理由は ADR-0014 を参照する。

### 1-2. 公開前履歴監査（2026-07-20）

- 公開対象 `main` の全到達 commit を走査し、秘密鍵、API token、keystore、署名済み成果物、
  サービスアカウント JSON、既知の登録 token がないことを確認した。
- 公開対象 tree のメールアドレスは GitHub noreply とテスト用 `.invalid` のみに限定されている。
- 旧 private repository の branch・PR ref には公開しない author email が存在したため、
  旧 repository 自体は private のまま保持し、監査済み snapshot から公開履歴を作成する。
- GitHub Environment secrets は値を複製せず、private release repository に保持する。

---

## 2. リリース前レビューチェックリスト

### 2-1. ワークフローログへのシークレット出力確認

| # | 確認項目 | 判定方法 | 根拠（行番号） |
|---|----------|----------|----------------|
| W-1 | シークレットは env ブロック経由でのみ渡される | `grep -n "secrets\." .github/workflows/play-release.yml` で env 外参照がないことを確認 | play-release.yml L108-111, L130, L142-144, L158-160 |
| W-2 | シークレット値を echo/print するステップがない | `grep -n "echo.*\$" .github/workflows/*.yml` の結果にシークレット変数が含まれないことを確認 | play-release.yml L71-73, L219-229 はすべてバージョン/SHA/入力値のみ |
| W-3 | base64 デコードはファイルへの書き出しのみ | keystore decode ステップ確認: `printf '%s' "$MDLITE_RELEASE_KEYSTORE_BASE64" \| base64 --decode > "$RUNNER_TEMP/mdlite-release.jks"` — stdout に出力されない | play-release.yml L132-133 |
| W-4 | base64 エンコードによるマスク迂回がない | ワークフローに `base64 -e` / `openssl base64` / `xxd` でシークレットを再エンコードするステップがないことを確認 | play-release.yml 全体を目視確認済み |
| W-5 | preflight スクリプトは存在チェックのみ | `play-upload-preflight.sh` の `require()` 関数は `ok <NAME>` または `MISSING <NAME>` のみを出力し、値は print しない | scripts/play-upload-preflight.sh L24-30 |
| W-6 | GitHub Actions の自動マスクが機能している | 実行ログで `MDLITE_RELEASE_KEYSTORE_BASE64: ***`、`MDLITE_RELEASE_KEY_ALIAS: ***` など全シークレットが `***` で表示されることを確認 | run #26389053762 ログ（2026-05-25 成功 run） |

**確認コマンド（PR 作成前）**:
```sh
grep -n "secrets\." .github/workflows/*.yml | grep -v "#"
grep -n "echo.*\$\|run:.*echo" .github/workflows/*.yml | grep -v "GITHUB_OUTPUT\|STEP_SUMMARY\|version\|sha\|channel\|status"
```

**現在の状態（2026-06-07 確認済み）**:

| シークレット | 参照箇所 | ログ出力 | 判定 |
|-------------|---------|---------|------|
| `MDLITE_RELEASE_KEYSTORE_BASE64` | play-release.yml L108, L130 | `***`（マスク済み） | OK |
| `MDLITE_RELEASE_KEY_ALIAS` | play-release.yml L109, L142, L158 | `***`（マスク済み） | OK |
| `MDLITE_RELEASE_STORE_PASS` | play-release.yml L110, L143, L159 | `***`（マスク済み） | OK |
| `MDLITE_RELEASE_KEY_PASS` | play-release.yml L111, L144, L160 | `***`（マスク済み） | OK |

**判定: 生値・デコード値ともにログ非露出を確認**（run ID: 26389053762）

---

### 2-2. .gitignore によるシークレットファイルの除外確認

| # | ファイルパターン | .gitignore に含まれるか | 根拠 |
|---|-----------------|------------------------|------|
| G-1 | `*.keystore` | ✅ | .gitignore L（`*.keystore`） |
| G-2 | `*.jks` | ✅ | .gitignore L（`*.jks`） |
| G-3 | `secrets.properties` | ✅ | .gitignore L（`secrets.properties`） |
| G-4 | `.env` | ✅ | .gitignore L（`.env`） |
| G-5 | `service-account*.json` | ✅ | .gitignore L（`service-account*.json`, `*service-account*.json`） |

> **注意**: `*.pem` および `*.key` は `.gitignore` に含まれていないが、
> `check-no-committed-secrets.sh` のスキャン対象（`git ls-files` チェック）でカバーされている。
> 意図的に委譲した設計のため、問題なし。

---

### 2-3. push 前シークレット走査ゲート

**必須**: PR を開く前に以下を実行し、出力が "No committed secrets detected" であることを確認する。

```sh
sh scripts/check-no-committed-secrets.sh
```

走査内容（scripts/check-no-committed-secrets.sh）:
- `*.jks`, `*.keystore`, `*.p12`, `*.pfx`, `*.pem`, `*.key`, `key.properties`, `secrets.properties`,
  `.env`, `*service-account*.json`, `play-console-*.json` が git ls-files に含まれていないことを検証
- `-----BEGIN [A-Z ]* PRIVATE KEY-----` マーカーが追跡ファイルに含まれていないことを検証

CI でも `ci.yml` の `check-no-committed-secrets` ステップ（L43）で自動実行される。

---

### 2-4. 外部スクリプト・依存チェーン確認

**play-upload-closed-test.py の API キーハードコード確認**:

| 確認項目 | 判定 | 根拠 |
|----------|------|------|
| ハードコードされた API キーがない | ✅ | スクリプトに `api_key` 等のリテラルなし（grep 確認済み） |
| 認証は `google.auth.default()` を使用 | ✅ | scripts/play-upload-closed-test.py L116: ADC 経由 |
| WIF（Workload Identity Federation）を優先 | ✅ | `--service-account` は明示指定時のみ、デフォルトは ADC |

```sh
grep -n "api_key\|api-key\|apikey\|key.*=.*['\"]" scripts/play-upload-closed-test.py
```

**play-upload-preflight.sh のシークレット値出力確認**:

`play-upload-preflight.sh` の `require()` 関数は変数の存在のみチェックし、値は print しない。
（`scripts/play-upload-preflight.sh` L8–28 確認済み: 出力は変数名のみ、シークレット値は一切 print しない）

```sh
grep -n "echo\|print\|printf" scripts/play-upload-preflight.sh
```

---

### 2-5. その他の残件（backlog 由来）

backlog「Security and Public Repository Checks」の Remaining tasks より:

| # | 残件 | 状態 |
|---|------|------|
| S-1 | リリースワークフローがシークレット値を出力しないことの確認 | ✅ 本文書で確認済み（2026-06-07） |
| S-2 | 公開リポジトリ安全性レビューのチェックリスト文書化 | ✅ 本文書（2026-06-07） |
| S-3 | サービスアカウント JSON キーをレガシー/手動用途のみとし WIF を優先 | ✅ play-upload-closed-test.py は ADC/WIF 優先設計済み |

---

## 3. cmd_007 の教訓

**背景**: cmd_007 は「harness-kit の公開リポジトリ化」に際して、
公開 push 前のシークレット走査ゲートと走査順序の重要性が確認されたタスク。

**教訓**:
1. **走査は push 前に行う**: コミット後の除去は git 履歴から完全に消えず、
   fork・キャッシュ経由で拡散リスクがある
2. **自動走査（CI）だけに頼らない**: `check-no-committed-secrets.sh` をローカルでも実行する
3. **走査順序**: `sh scripts/pr-preflight.sh` → `sh scripts/check-no-committed-secrets.sh` の順で実行し、
   後者が "No committed secrets detected" であることを確認してから push

---

## 4. harness-kit への適用可能性

**評価**: ✅ 一部適用可能（partial）

| 項目 | 適用可否 | 理由 |
|------|---------|------|
| シークレット走査スクリプト（`check-no-committed-secrets.sh`） | ✓ 適用推奨 | PEM キーパターン検出は言語非依存 |
| `.gitignore` パターン（`*.jks`, `.env`, `service-account*`）| ✓ 適用推奨 | 公開リポジトリ共通 |
| CI ログのシークレットマスク確認手順 | ✓ 適用推奨 | GitHub Actions を使う全 PJ で有効 |
| `play-upload-preflight.sh` の設計パターン | △ 参考のみ | Play Console 固有だが「値を print しない preflight」パターンは汎用 |
| Play Console 関連チェック | ✗ 非適用 | localmd-reader 固有 |

WIF（Workload Identity Federation）優先の設計方針は harness-kit でも適用を推奨。
harness-kit では代替の artifact 保護機構を個別に設計すること。

---

## 5. 参考リンク

- `scripts/check-no-committed-secrets.sh` — コミット走査スクリプト
- `scripts/play-upload-preflight.sh` — Play Console 事前チェック（値は出力しない設計）
- `.github/workflows/play-release.yml` — リリースワークフロー本体
- `docs/harness/github-actions-cicd.md` — GitHub Actions CI/CD 全体設計
- `docs/harness/claude-harness-engineering-backlog.md` セクション「Security and Public Repository Checks」
