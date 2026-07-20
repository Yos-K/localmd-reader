# harness-kit 消費切替 提案書

> 対象: localmd-reader → harness-kit v0.1.0 参照への切替  
> ステータス: **裁可済み（2026-06-07）**  
> 作成: 2026-06-07 / cmd_022

---

## 1. 目的と背景

### なぜ切替が必要か

harness-kit は localmd-reader のハーネスを汎用化して抽出したリポジトリ（cmd_007 で殿裁可済み）。
現在 localmd-reader は harness-kit と同一内容のスクリプト群をローカルコピーとして保持しており、
**ハーネス改善を localmd-reader 側で行っても harness-kit へ還流する仕組みがない**。

切替により得られる効果:
- **ドッグフーディング**: localmd-reader が消費側第1号となり、sync-manifest 方式を本番実証できる
- **二重管理の解消**: core/adapter の 36 ファイルの改修が harness-kit 一か所で完結し、他プロジェクトへ伝播
- **明確な境界**: 「harness-kit が管理するファイル」と「localmd-reader 固有のファイル」が構造上分離される

### 切替しないと何が起きるか

harness-kit のバグ修正・改善を localmd-reader へ手動で持ち込む必要が生じる（逆方向も同様）。
ハーネスが「抽出されたが消費されない」状態で放置されると、設計の正当性が失われる。

---

## 2. 切替対象の全体像

### 2-1. scripts 対応表

| 分類 | harness-kit 参照元 | 該当ファイル数 | 代表例 |
|------|------------------|-------------|--------|
| 汎用コア層 | `core/scripts/` | 9 | check-conventional-title.sh, version-env.sh |
| Android-JVM アダプタ層 | `adapters/android-jvm/scripts/` | 27 | run-mutation-tests.sh, check-test-smells.sh |
| **localmd-reader 固有（移行対象外）** | — | 残り全て | check-hard-constraints.sh, check-docs-currency.sh 等 |

**確認根拠**: `sync-manifests/android-jvm.yaml` のホワイトリスト照合 + localmd-reader `scripts/` 実ファイル確認済み（2026-06-07）。

固有スクリプト（移行対象外）の主なもの:
```
check-domain-model.sh / check-docs-currency.sh / check-hard-constraints.sh
check-release-notes.sh / check-third-party-notices.sh
apply-billing-manifest.sh / pr-preflight.sh / release-preflight.sh
play-upload-closed-test.py / smoke-render-assert-l5.sh / create-debug-markdown-fixture.sh 等
```

### 2-2. workflows 対応表

| workflow | harness-kit 参照元 | 対応方針 |
|----------|------------------|---------|
| `ci.yml` | `adapters/android-jvm/workflows/ci.yml` | ベース利用 + 固有ステップを追記 |
| `mutation.yml` | `adapters/android-jvm/workflows/mutation.yml` | そのまま利用（logic-layer 判定パターンのみ固有調整） |
| `device-smoke.yml` | `adapters/android-jvm/workflows/device-smoke.yml` | そのまま利用 |
| `domain-model-check.yml` | — | 固有。harness-kit 対象外 |
| `play-release.yml` | — | 固有。harness-kit 対象外 |
| `smoke-render-l5.yml` | — | 固有。harness-kit 対象外 |
| `theme-screenshots.yml` | — | 固有。harness-kit 対象外 |

**確認根拠**: `adapters/android-jvm/workflows/` の実ファイルと localmd-reader `.github/workflows/` を照合済み。

localmd-reader 版 `ci.yml` が harness-kit テンプレートに追加しているステップ（固有維持が必要）:
- `fitness` ジョブ: `check-hard-constraints.sh`, `check-docs-currency.sh`
- `test` ジョブ: Free/Pro 両 APK ビルド + artifact upload（flavor 構成が固有）

### 2-3. プロジェクト固有設定の保持方法

harness-kit の `templates/harness-config.yaml.example` に対応する `harness.config.sh` を localmd-reader ルートに置き、
各スクリプトがこのファイルを `. "$ROOT/harness.config.sh"` で読む方式。

保持が必要な固有設定（変数例）:
```
MUTATION_THRESHOLD=82          # floor ratchet（絶対に下げない。正は run-mutation-tests.sh の既定値＝2026-06-12 時点 82）
TARGET_CLASSES=io.github.yosk.mdlite.domain.*,...
BUILDCONFIG_PACKAGE=io.github.yosk.mdlite.infrastructure
APP_PACKAGE=io.github.yosk.mdlite
APP_THEMES="light dark"
APP_THEME_PREF_KEY=viewer_theme
```

---

## 3. 段階的切替手順

### フェーズ1: 汎用コア層の置換（core/scripts/ 9 ファイル）

対象: `check-conventional-title.sh`, `check-no-committed-secrets.sh`, `version-check.sh`,
`version-env.sh`, `version-show.sh`, `start-work.sh`, `prepare-play-store-screenshot.sh`,
`ResizePng.java`, `StripImageMetadata.java`

手順:
1. harness-kit を clone または `install.sh --profile minimal` で core ファイルを取得
2. diff で localmd-reader 版と照合し、localmd-reader 固有の変更がないことを確認
3. harness-kit 版で上書き、`sync-manifest.yaml` を追加（参照バージョン記録）
4. ローカルで `sh scripts/pr-preflight.sh` と CI を通してから PR 作成

切戻し: PR を revert するだけ（フェーズ単独で切戻し可能）。

### フェーズ2: Android-JVM アダプタ層の置換（adapters/android-jvm/scripts/ 27 ファイル）

対象: `run-mutation-tests.sh`, `check-test-smells.sh`, `build-*.sh`, `check-file-sizes.sh` 等

手順:
1. `install.sh --profile standard` でアダプタファイルを取得
2. diff で localmd-reader 版と照合し、固有設定（MUTATION_THRESHOLD 等）が harness-config.sh 方式に変換済みか確認
3. 固有設定を `harness.config.sh` に切り出してスクリプト本体を置換
4. ローカル mutation 計測で floor（run-mutation-tests.sh の既定値。2026-06-12 時点 82）が維持されることを確認してから PR 作成

切戻し: フェーズ1 と独立して revert 可能。

### フェーズ3: 固有設定の外出し（harness-config.yaml）

対象: 残余の固有設定（APP_NAME, APP_PACKAGE, smoke インテント等）の `harness.config.sh` への集約

手順:
1. `harness.config.sh` に全固有変数を集約
2. 固有スクリプト（check-hard-constraints.sh 等）が `harness.config.sh` を参照するよう調整
3. README に「固有設定はここを変更せよ」の一元案内を追記

切戻し: `harness.config.sh` を削除し各スクリプトに変数を戻す。フェーズ1/2 に影響しない。

> **フェーズ依存性の注意**: `harness.config.sh` はフェーズ2で新規作成しフェーズ3で完成する。フェーズ独立 revert は順次マージを前提とする（フェーズ3先行マージ後のフェーズ2 revert は `harness.config.sh` の扱いを別途検討のこと）。

---

## 4. リスク評価と緩和策

| リスク | 深刻度 | 発生条件 | 緩和策 |
|--------|--------|---------|--------|
| CI 空白期間（required checks が通らない） | 高 | harness-kit 版スクリプトにバグがある場合 | フェーズごとに独立 PR + 全ジョブ pass 確認後にマージ |
| mutation floor が下がる | 高 | run-mutation-tests.sh の MUTATION_THRESHOLD 引継ぎ漏れ | フェーズ2 の diff 確認でハードチェック。floor（現行値はスクリプト既定が正）を harness.config.sh に明記 |
| アプリ挙動の変化（Hard Constraint 違反） | 高 | scripts の挙動差分をレビューしない場合 | 全ファイルを diff で照合してから置換（harness-kit では純粋なハーネス変更のみが許容） |
| 固有スクリプトの誤上書き | 中 | ホワイトリスト外ファイルを install.sh が上書きする誤操作 | install.sh + sync-manifest ホワイトリスト方式で構造上保護されている |
| sync-manifest バージョン不整合 | 低 | harness-kit 更新後に localmd-reader 側の `sync-manifest.yaml` を更新し忘れ | sync-check.sh をローカル/CI から実行してドリフトを検知 |

---

## 5. required checks への影響

現在の required checks: `fitness / test / gradle-build / mutation`

フェーズ1・2 では **required checks の定義を変更しない**。スクリプトの実装元が変わるだけで、
CI ジョブ名・トリガー・必須条件はすべて現状を維持する。

フェーズ3 の `harness.config.sh` 導入も、各スクリプトの外部 I/F（exit code）を変えないため影響なし。

---

## 6. 殿への確認事項（裁可が必要な判断点）

| # | 確認事項 | 選択肢 |
|---|---------|--------|
| A | **実施タイミング**: 切替を今の TODO 優先度 A〜C のどこに割り込ませるか | 優先度 B に挿入 / C に後回し / 別途判断 |
| B | **フェーズ分割 vs 一括**: 3 フェーズ分割で進めるか、1 PR にまとめるか | 分割（提案） / 一括 |
| C | **sync-manifest.yaml の管理**: localmd-reader に追加するファイルとして扱うか、gitignore するか | 追加してバージョン追跡（提案） / gitignore |
| D | **workflow の sync 対象化**: ci.yml / mutation.yml を harness-kit 側から sync する運用にするか | sync 対象にする / localmd-reader 側で完全管理 |

**確認事項D 補足**: sync 対象化する場合、`ci.yml` の固有追記ステップ（`check-hard-constraints`/Free-Pro APK ビルド等）は sync で上書きされないよう保護方式を事前に決定すること（例: 固有ジョブを別ファイル分離 or sync 後マージ）。

### 殿の裁可決定事項（2026-06-07）

| 判断点 | 決定 | 根拠 |
|--------|------|------|
| A 実施タイミング | 現在OPENの裁可待ちPR群（#128/#131/#132/#133）とcmd_023(B-5)/cmd_024(flaky解消)の着地を待ってから着手 | 乖離の進行（乗り遅れリスク）と衝突回避（既存PR干渉）のバランス |
| B 切替方式 | 3フェーズ分割マージ（コア9→アダプタ27+harness.config.sh→固有設定外出し） | 段階的・安全な切替・各フェーズで独立revert可能 |
| C sync-manifest管理 | sync-manifest.yamlをgit追跡（.gitignore除外なし） | origin-tracking追跡でkit更新の差分を可視化 |
| D workflow sync | 当面localmd-reader側で完全管理（固有ステップ保護方式設計後に再判断） | scripts層sync実績を積んでからworkflow層に拡大 |

---

## 7. 次のアクション（裁可後の実施 cmd 起票）

以下の cmd を順次起票します（殿裁可済み 2026-06-07）:

1. `cmd_023`: B-5実装（ブランチ戦略・作業フロー整備）
2. `cmd_024`: flaky解消（不安定テスト修正）
3. `cmd_025`: 縮小導入（フェーズ1: 汎用コア 9 ファイル置換 + sync-manifest.yaml 追加 + PR）
4. `cmd_028`: 切替実施（フェーズ2/3: アダプタ 27 ファイル置換 + harness.config.sh + 固有設定集約）

各 cmd は独立した PR として完結させ、required checks 全 pass を確認してからマージします。

---

*このドキュメントは提案のみ。実施は殿の裁可後に別 cmd として起票する。*

---

## Phase 2 着手調査（2026-06-12）

### ドリフト実測

Phase 1（PR #141・コア9本）以降、**アダプタ層27本すべてが harness-kit v0.1.3 から乖離**
（localmd-reader 側が先行）。主な乖離: `capture-theme-screenshots.sh` 117行差分（flaky解消
cmd_024）、`run-mutation-tests.sh` 93行差分（floor 80→82・除外設定）、`emulator-smoke.sh`
53行差分、`check-test-smells.sh` 13行差分ほか全本。確認方法: harness-kit @v0.1.3 と
`scripts/` の直接 diff（2026-06-12）。

### なぜ単純な消費切替ができないか

sync-propose.yml（harness-kit の週次 PR 型同期）をこのまま導入すると、**kit 側の古い内容で
localmd-reader の改善を上書きする提案 PR が立つ**（同期の向きが逆）。だから Phase 2 は
「上流還元 → タグ発行 → 消費切替」の3段で行う:

| 段 | 作業 | 場所 |
|----|------|------|
| 2a | 27本の現行版を harness-kit へ還元する PR（kit 側 CI で検査）+ CHANGELOG + **v0.2.0 タグ** | harness-kit |
| 2b | `sync-propose.yml` を `harness-sync.yml` として導入（週次 + 手動。ホワイトリスト方式で固有設定は構造上対象外） | localmd-reader |
| 2c | 初回同期 PR で一致確認（差分ゼロが期待値＝2a の検証） | localmd-reader |

### 実施状況（2026-06-13 更新）

- **2a 完了**: 真の改善のみ外科還元（kit v0.2.0）。「27本コピー」は退行を招くため不採用（消費形ドリフトは還元しない）。
- **消費の実証**: `check-file-sizes.sh`（kit v0.2.1）・`run-mutation-tests.sh`（kit v0.3.0・ロジック注入口 `BUILDCONFIG_FIELDS`/`EXTRA_MAIN_SOURCES`）を git-ROOT + `harness.config.sh` 方式で実消費。CI mutation 緑で behavior 保存を確認。
- **2b 完了（本変更）**: `.github/workflows/harness-sync.yml` を導入（kit の sync-propose.yml v0.3.1 テンプレート）。**同期対象は `consumed_scripts`（opt-in）のみ**——kit の全ホワイトリストを撒く旧挙動は部分消費を壊すため kit 側で修正済み（kit v0.3.1）。未採用スクリプト（自前 emulator-smoke 等）と `harness.config.sh` は触らない。
- **2c（差分ゼロ確認）**: ローカルでスコープ同期をシミュレートし、消費2本が pinned バージョンで差分ゼロ・他ファイル不変を確認。kit 新リリース時は consumed_scripts だけを更新する PR が起票され、localmd の CI でゲートされる。

### 教訓（同期機構の運用ルール）

ドリフト窓が9日で全27本に及んだ。**だから** 2b 導入後は「consumed_scripts を変更する PR は
同週内に kit への還元 PR を対にする」を運用ルールとする（さもないと週次同期が毎回逆向き提案になる）。

**実装で裏付け（2026-06-14・懸念2対応）**: この運用ルールは人の規律だけに依存させない。
- ルール本文を [`AGENTS.md`](../../AGENTS.md)「consumed_scripts を編集するとき（必須運用ルール）」に明文化。
- `scripts/check-kit-drift.sh` が diff-zero 不変条件（consumed_scripts == pin した kit）を再検査し、
  `.github/workflows/harness-drift-check.yml`（**非ブロッキング advisory**・毎PR/週次）が破れを
  Step Summary と警告アノテーションに可視化する。
- **なぜ非ブロッキングか**: ドリフトは「マージ不可」ではなく「還元 or 差し戻しの判断が要る」状態。
  必須チェックには載せず、見落としを防ぐ可視化に徹する。強制したくなれば同スクリプトに `--strict`
  を付けて赤化できる（opt-in）。検知対象は現状 consumed 7本。アダプタ27本のうち未消費の
  divergent スクリプトは「意図的に乖離」なので対象外（consumed に昇格した時点で自動的に被検査になる）。
