# 正典インデックス（どの規則はどの文書が正典か）

## このドキュメントの目的

`正典`（source of truth）を主張する文書が複数あり、「どの規則がどこで決まっているか」を引くのに
認知負荷がかかっていた（第2軸 ROI 評価 C4）。本ファイルは**単一の索引**で、規則・領域ごとに
「正典はどれか」を1か所で引けるようにする。**新しい正典を増やすときは、まずこの表に行を足す**こと。

これは索引であって規則の本体ではない。各規則の内容は、表の「正典」列がさす文書を参照する。
既存文書の統廃合はしない（C4 のやりすぎライン）。

## 正典マッピング

| 規則・領域 | 正典（source of truth） | 補足 |
|---|---|---|
| ドメイン用語・不変条件（L1-L3） | [`docs/domain/domain-glossary.md`](../domain/domain-glossary.md) ほか glossary 群 | AGENTS と コードの用語はここに解決する。観測挙動の用語化は下記ゲートを通す |
| 開発・ハーネス規則（CI/CD・preflight・ブランチ保護・テスト戦略の要点） | [`AGENTS.md`](../../AGENTS.md) | エージェント中立の開発ルール正典。詳細手順は `docs/harness/*` に分散（下記） |
| エージェント設定 | [`AGENTS.md`](../../AGENTS.md) | `.claude/`・`.codex/` 等への複製はしない（[ecc-reference-evaluation.md](./ecc-reference-evaluation.md)） |
| Free/Pro 境界ポリシー | [`docs/product/free-pro-feature-policy.md`](../product/free-pro-feature-policy.md)（+ `.ja.md`） | 「現行の正典」。`pro-development-context.md` は履歴文脈で**正典ではない** |
| アーキテクチャ/パッケージ方針 | [`docs/product/architecture-package-policy.md`](../product/architecture-package-policy.md) | 層・パッケージ境界の規則 |
| 重要な設計判断と見直し条件 | [`docs/adr/README.md`](../adr/README.md)（+ `.ja.md`） | 各ADRが決定・代替案・理由・見直し契機を保持。コミット確認は `check-adr-review.sh` |
| mutation floor の**実値** | [`harness.config.sh`](../../harness.config.sh) の `MUTATION_THRESHOLD` | 数値の正典は config。解釈・運用手順は [mutation-analysis-rule.md](./mutation-analysis-rule.md) |
| 観測挙動の正典化ゲート（意図/欠陥の分類＝オーナー裁可） | [`docs/harness/domain-knowledge-loop.md`](./domain-knowledge-loop.md) | 探索の運用は [exploratory-testing.md](./exploratory-testing.md) |
| テスト戦略（small/medium/large・比率） | [`docs/harness/test-strategy.md`](./test-strategy.md) | 集計は `scripts/test-balance-report.sh` |
| doc 同期の正典マッピング（3レーン: glossary/harness/policy） | [`scripts/check-docs-currency.sh`](../../scripts/check-docs-currency.sh) | どの変更がどの doc 更新を要求するかの実装。AGENTS にレーン要約 |
| ハーネス ROI 評価フレーム（2軸・判定区分） | [`docs/harness/harness-roi-framework.md`](./harness-roi-framework.md) | 直近の適用は `harness-roi-evaluation-2026-06.md` / `harness-roi-2nd-axis-evaluation-2026-06.md` |
| ハーネス全体設計 | [`docs/harness/agent-harness-design.md`](./agent-harness-design.md) | ハーネスの目的・構造の概観 |
| リリース手順（Play Console 提出） | [`docs/release/play-console-submission.md`](../release/play-console-submission.md) | CI/CD 詳細は [github-actions-cicd.md](./github-actions-cicd.md) |

## 運用ルール

- **新しい正典を作る前に、この表へ1行追加する。** 索引に無い「正典」は、実質的に発見不能（探す側が
  どこを見ればよいか分からない）＝認知負荷の源。
- 表の「正典」列は**1領域につき1文書**を基本とする。同一規則について複数文書が正典を主張しない。
- 履歴・文脈・評価レポートは正典ではない（時点の記録）。正典は「現在の規則が決まっている場所」。
