---
name: gatecrate-evaluate
description: |
  gatecrate ハーネスの二階ループ（ハーネス自己評価）を回す。CI実行履歴とゲート生存証明から
  各ハーネス層を「keep / strengthen / consolidate / downgrade / remove」に判定し、
  剪定・統合候補を根拠つきで提示する。「ハーネスのROI評価」「効いていないゲートを剪定」
  「このCIゲートは要るか」「ハーネスの棚卸し」「harness roi」で起動。計測スクリプト
  （collect-gate-history.sh / probe-gate-liveness.sh）が集めた実績を docs/harness-roi-evaluation.md の
  2軸・removal3条件・5判定にかけ、exit code に出ない判断（「発火ゼロ＝無駄」の罠回避等）をエージェントが担う。
  Do NOT use for: ハーネスの構築（gatecrate-setup を使う）。新ゲートの実装。安全制約
  （シークレット/禁止権限）の自動削除（メトリクスで自動撤去してはならない・人間が承認する）。
argument-hint: "[target-project-path]"
allowed-tools: Read, Grep, Glob, Bash
metadata:
  author: Yos-K
  version: 1.0.0
  requires: gatecrate v0.9.0 or later
---

# gatecrate-evaluate — ハーネス層のROI評価（二階ループ）

このスキルは、対象プロジェクトの**ハーネス自体が効いているかを計測し、効かない/過剰な層を
剪定する二階ループ**をエージェントに回させる。一階ループ（テスト/ゲートがコードを評価し直させる）
は CI が自動で回すが、「そのゲートは本当に効いているか・過剰でないか」は誰も測らない。放置すると
制約は単調増加し、死荷重（コストだけ残り効いていない層）が本当に効く層への投資を圧迫する。

方法論の正典は [`docs/harness-roi-evaluation.md`](../../../docs/harness-roi-evaluation.md)。本スキルは
その**薄い実行層**であり、方法論を再記述せず「どの順で・何を集め・どう判定を出すか」を与える。

## 中心原則 — 決定論は機械が、判断はエージェントが

| 層 | 担当 | 何を |
|---|---|---|
| 計測スクリプト | 決定論（CI/シェル） | 発火実績・CI秒・生存証明を**事実として収集**（判断を挟まない） |
| 本スキル（persona） | 判断（エージェント） | 2軸・3条件論理積・5判定で**剪定/統合候補を提示**。exit code に出ない判断を供給 |
| 人間 | 承認 | `removal` / `consolidate` は提案のみ。撤去・統合は人間が PR で実行 |

**NEVER（最重要の罠）**:
- **「発火ゼロ＝無駄」で予防層を消すこと**（反事実の罠）。予防ゲート（シークレット/行数/禁止権限）は
  発火ゼロが正常。生存証明プローブが ALIVE なら効いている。removal は3条件の**論理積**で、安価な層は
  条件①で落ちて必ず keep される。
- **安全制約（シークレット・禁止権限）をメトリクスで自動撤去すること**。
- floor やゲートを下げて緑にすること（剪定と劣化の混同）。

## 前提

- 対象が git リポジトリで、CI 実行履歴があること（履歴が薄い＝判断材料不足なら「unconfirmed」と明記）。
- `gh`（GitHub CLI）認証済み（`gh auth status`）。無ければ発火・コスト軸は `unconfirmed (needs gh)` とする。
- gatecrate の計測スクリプトが手元にあること（`$KIT/core/scripts/`）。

## 手順（roi-evaluation.md の Procedure ①〜⑦ に対応）

### Phase 1 — 証拠収集（事実のみ。憶測禁止）

```sh
# ①② 発火実績・CIコスト: 各ゲート(=CIステップ)の runs / fires / fire_rate / CI秒
# localmd は生の step名に Checkout / Set up Java 等が混ざるので、論理ゲートへ束ねる
# scripts/gate-groups.tsv を --group-map で渡す（未マップ step は生の行のまま残る）。
sh "$KIT/core/scripts/collect-gate-history.sh" --limit 50 \
   --group-map scripts/gate-groups.tsv > /tmp/gate-history.tsv
cat /tmp/gate-history.tsv

# ③ 生存証明: 予防型ゲートに合成違反を注入し ALIVE/DEAD を判定
sh "$KIT/core/scripts/probe-gate-liveness.sh"   # DEAD があれば最優先で報告（黙って壊れたゲート）

# axis-2 維持負荷シグナル（CI秒に出ない・git/構成ベース。本軸は数値で測れるものだけ集める）
git log --oneline -- docs/ | wc -l                       # doc churn の総量
ls .github/workflows/*.yml 2>/dev/null | wc -l            # ワークフロー数（認知負荷の代理）
grep -rl 'source-of-truth\|正典\|canon' docs/ | wc -l     # 「真実の源」ドキュメントの散在度
```

各ゲートを**層として列挙**する。層 = 1つの検出/予防の単位（例: mutation, unit-test, secrets-gate,
line-limit, emulator-smoke, doc-sync）。ゲートを検出型／予防型に分類する（軸の当て方が違う）。

### Phase 2 — Axis 1（撤去・分母＝CIコスト×頻度）

各層に `(fires × uniqueness) ÷ (CI秒 × 頻度)` を当て、**removal は3条件の論理積**を確認する。

| # | 条件 | 判定の見方 |
|---|---|---|
| ① 高コスト | CI秒×頻度が大きい / doc同期コストが大きい | `total_seconds` が小さい層はここで落ちる→**keep 確定** |
| ② 独自性ゼロ | 他層が既に検出する／守る対象リスクが消滅 | 重複検出か、リスク前提が今も生きているか |
| ③ 発火ゼロ or リスク消滅 | 検出型が何も捕まえていない／予防型の前提が消えた | 予防型は「発火ゼロ」だけで③を満たさない（生存証明と前提存続で判断） |

3つすべて真の層だけが `removal-candidate`。安価な grep ゲートは①で落ちるので発火ゼロでも残す。

### Phase 3 — Axis 2（統合・分母＝維持負荷）

撤去で落ちた（＝安価で消せない）層について、CI秒に出ない**人間の継続的注意コスト**を測る。

| シグナル | 測り方 |
|---|---|
| ドリフト誘発リスク | 「Xしたら必ずYも」という手作業規律の数・実際のドリフト事故履歴 |
| 認知負荷／重複 | 同時に把握すべき層・ルール数、他層との失敗モード重複、ワークフロー/正典docの数 |
| 人間の維持時間 | 同期/閾値/flaky PR の往復回数、doc同期コミット数（**CI秒ではない**） |

いずれかが高く、かつ axis-1 で撤去不可な層は `consolidate-candidate`。是正は**統合/自動化であって撤去ではない**。

**過大判定ガード**: 高 churn ≠ 無駄。頻繁に触る層は毎回価値を出している可能性がある。**重複か手作業規律依存**
が負荷源の層だけを挙げ、keep する安価・低負荷の層を明示列挙する（axis-2 が「全部に旗を立てる」へ反転しない）。

### Phase 4 — 5判定とレポート

各層に5判定（keep / strengthen / consolidate-candidate / downgrade-to-advisory / removal-candidate）を当て、
**「なぜ→だから」と一次ソースのリンク**を必ず添える。根拠が取れない項目は `unconfirmed (needs X)`（空欄/憶測禁止）。

```
## ハーネスROI評価 YYYY-MM-DD（対象: <repo>・履歴 N runs）

### 生存証明（最優先）
- DEAD: <なし or ゲート名と原因>   ← DEAD は剪定以前に「壊れたゲートの修理」

### 層ごとの判定
| 層 | 型 | fires/runs | CI秒 | 軸1 | 軸2 | 判定 | なぜ→だから（一次ソース） |
|---|---|---|---|---|---|---|---|
| mutation | 検出 | 3/40 | 14.0 | 独自・高価値 | — | keep | 検出力を担保（…リンク） |
| secrets-gate | 予防 | 0/52 | 0.3 | ①で落ち keep | 低 | keep | 発火0は正常・ALIVE（probe）。安価ゆえ removal不可 |
| doc-sync-A / doc-sync-B | 予防 | 0/52 | 0.5 | ①で落ち keep | 高(重複) | consolidate-candidate | 2層が同一失敗を守る→1エンジンへ統合 |

### 提案（人間の承認が要る）
- consolidate: <…>　/　removal: <…（3条件すべて引用）…>
- 安全制約はメトリクスで自動撤去しない（列挙して keep 明示）
```

### Phase 5 — 提案を起票（実行はしない）

`removal-candidate` / `consolidate-candidate` は**提案として起票**するに留め、撤去/統合の実行は人間が PR で行う。
日付つきレポートとして残し、profiles 選択へフィードバックする（roi-evaluation.md「Connection to profiles」）。

## 出力契約

- 全判定に一次ソース（CI run リンク / スクリプト出力 / コミット）を添える。憶測は `unconfirmed` と書く。
- DEAD ゲートがあれば剪定提案より前に「壊れたゲートの修理」として最優先で報告する。
- keep する安価・低負荷の層を明示列挙し、評価が「削る方向」へ偏らないようにする。
