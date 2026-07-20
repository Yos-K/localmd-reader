# ハーネスROI評価フレームワーク

> **前提原則ノート**（本文書はこれらの「どう測りどう判定するか」を定義する運用手順）
> - [ハーネスへの投資をどう考えるか](https://github.com/Yos-K/app-development-notes/blob/main/notes/harness-investment.md) — なぜ層を選ぶか・必須コアの考え方
> - [ハーネス層の有効性評価とライフサイクル](https://github.com/Yos-K/app-development-notes/blob/main/notes/harness-effectiveness-review.md) — 検知型/予防型の区別・撤去判断の構造
> - 関連設計: `multi-agent-shogun:docs/harness-portability/design.md` §2.2（プロファイル選択器との接続）

## 1. 目的

**なぜ**: ハーネスは張るほど維持費が積み上がる。追加した瞬間は価値が明確に見えても、
時間が経つと効いている層とコストだけ残って効いていない層に分かれる。

**だから**: 投資と効果を実測で比較し、各層に `keep / strengthen / downgrade-to-advisory /
consolidate-candidate / removal-candidate` の5区分を機械的に適用する手順を定義する。判断の主観を排し、
根拠の取れない項目は「未確認」と明記する。

**2軸で評価する理由（§4.5 で定義）**: 撤去判断（§4）は「CI コスト×頻度」を分母にするため、安価な層は
構造上 removal-candidate になり得ない。さらに学習目的 PJ では削除実行もしない。**この2つが重なると、
"安価で消さない" 層は永久に縮小対象から外れ、評価が層の純増を追認するだけになる**（実際 2026-06 初回評価は
removal-candidate=0）。だから CI 秒とは直交する**第2軸「維持負荷シグナル」（§4.5）**を併用し、安価でも
「ドリフト誘発・認知負荷・重複」を抱える層に `consolidate-candidate`（縮小・統合の助言）を出せるようにする。
学習目的の免責は**削除実行**にのみ及び、この助言の起票は妨げない。

## 2. 評価軸

### 分母: 維持コスト

構築費はサンクコストのため分母に含めない。評価対象は**継続的に発生するコスト**のみ。

| コスト要素 | 測定方法 |
|---|---|
| CI 実行時間 × 発火頻度 | `gh run list --workflow=<name>.yml --limit 100 --json conclusion,startedAt,updatedAt` で分布・頻度を集計（subtask_004a 実測） |
| flaky 対応コスト | 同上の `conclusion=failure` を手動起因 / flaky 起因に分類（subtask_004a 実測） |
| false positive 対応コスト | 実 PR でオーバーライドした件数（gh PR レビュー履歴） |
| 用語集・ルール文書の同期コスト | glossary/rule-doc の発火履歴 + doc stale 実例数（subtask_004a 実測） |
| Codex レビュー往復コスト | CI 秒でなくレビュー往復工数で計測（他層と異なる計り方を適用） |

### 分子: 効果

| 効果要素 | 測定方法 |
|---|---|
| 発火実績（検知型） | 実 PR で落ちて本物の是正につながった件数（subtask_004b 実測） |
| 検知の独自性 | 他層と重複していない失敗モードを捕まえているか（subtask_004b 実測） |
| 予防型の生存証明 | 合成違反プローブで対応ゲートが落ちるか確認（subtask_004c 実測） |

## 3. 検知型と予防型の評価軸の違い

**検知型** — 既にあるバグを捕まえる層（unit / medium / mutation / smoke / Codex 等）。
評価軸: 発火実績 × 独自性 ÷ コスト。

**予防型** — ある種の間違いを抑止する層（fitness 系: 禁止権限・secrets・INTERNET 不在等）。

- **発火ゼロが正常**。落ちない理由は「効いていて誰も通せないから」。
- 評価は2経路:
  1. 守っている前提リスクがまだ実在するか（製品判断）
  2. 合成違反プローブで落ちることを確認（生存証明）
- push 前 preflight で吸収される場合は CI 履歴に発火が残らない。
  観測できない抑止はプローブ（004c）で代替する（反事実問題の実例として文書に記載）。

## 4. 反事実問題: 発火ゼロ ≠ 無価値

「一度も落ちない = 無駄」と誤判定すると最重要な予防層を消す事故が起きる
（[ライフサイクル原則 §最大の罠](https://github.com/Yos-K/app-development-notes/blob/main/notes/harness-effectiveness-review.md)）。

### removal-candidate の積判定 3 条件（3つ全部揃って初めて候補）

| 条件 | 内容 |
|---|---|
| ① 高コスト | CI 時間 × 頻度が大きい、または同期コストが大きい |
| ② 独自性ゼロ | 他層と同じものしか検出しない（検知型）/ 守るリスクが消滅・完全重複（予防型） |
| ③ 発火ゼロ or 守るリスク消滅 | 検知型で一度も捕まえていない / 予防型で前提リスクが消えた |

**積が成立しないケース（keep が原則）**:

- 安価な衛生チェック（grep 程度）: コスト≒0 のため条件①が成立しない → 発火ゼロでも keep
- 条件②のみ: 独自性がなくても安価なら消すメリットがない
- 学習目的 PJ: 判定は記録のみ・削除実行しない
  （[投資原則 §目的を分ける](https://github.com/Yos-K/app-development-notes/blob/main/notes/harness-investment.md)）

## 4.5 第2軸: 維持負荷シグナル（安価でも縮小を検討する軸）

**なぜ第2軸が要るか**: §4 の撤去積判定は分母が「CI コスト×頻度」なので、grep 程度の安価な層は
条件①が永久に不成立になり keep に固定される。だがコストが CI 秒に現れない**別種の維持負荷**——
人間/AI が継続的に払う注意・規律・読解——を抱える層は確かにある。これを可視化しないと、層は
増える一方で「全部 keep」に行き着く（追認）。だから CI 秒と直交する3シグナルを別途測る。

| シグナル | 何を測るか | 測定方法（実測のみ・憶測禁止） |
|---|---|---|
| ドリフト誘発リスク | 正しさが**人の手作業規律**に依存し、外すと壊れる層か | 「○○したら必ず△△する」式の運用ルールが AGENTS.md 等に必要か。実ドリフト発生履歴（例: harness-kit アダプタ27本が9日で全乖離）の件数 |
| 認知負荷 / 重複 | 読者が同時に把握すべき層・規則の数を増やすか。失敗モードが他層と重複するか | §3 の独自性測定の裏返し（重複が高い＝負荷だけ増える）。ルール文書の相互参照本数 |
| 維持の人的時間（絶対量） | CI 秒でなく**人が触る時間**。同期 PR レビュー・floor 調整・flaky 追跡の実時間 | 関連 PR の往復回数・doc 同期コミット数（subtask_004a の同期コスト測定を流用） |

**判定**: 3シグナルのいずれかが高く、かつ §4 の removal積判定が不成立（＝安価ゆえ消せない）の層は
`consolidate-candidate` とする。**対応は撤去ではなく縮小・統合・自動化**: 例)重複層の片方を advisory 化、
手作業規律を CI ゲートに置換、二重実装の統合。学習目的 PJ でも**この助言の起票・記録は行う**（免責は
削除実行のみ）。縮小を実施するかは人が判断する。

## 5. 判定 5 区分の基準

| 区分 | 適用基準 | 対応 |
|---|---|---|
| **keep** | 独自性あり、または安価 ×維持負荷も低い | 現状維持 |
| **strengthen** | 発火実績あり × カバー漏れ判明 | テスト追加・閾値引き上げ等 |
| **consolidate-candidate** | 安価で removal積不成立だが §4.5 の維持負荷シグナルが高い | 縮小・統合・自動化を助言（撤去ではない）。学習PJでも起票する |
| **downgrade-to-advisory** | 高コスト × 低独自性だが価値残存 | `required` から非必須へ降格。即撤去しない |
| **removal-candidate** | 積判定 3 条件全成立 | AI は提案のみ。撤去は人が PR で承認 |

予防型の安全制約はメトリクスで自動撤去しない。外す判断は製品側の判断でのみ行う
（[評価原則 §AI は提案](https://github.com/Yos-K/app-development-notes/blob/main/notes/harness-effectiveness-review.md)）。
`consolidate-candidate` も同様に、起票は機械的に行うが縮小の実施は人が承認する。

## 6. 規模別適正ラダー

入力 (`profile.yaml` 5 軸) → 出力（導入推奨ティア）。
`multi-agent-shogun:docs/harness-portability/design.md` §2.2 のプロファイル選択器と
同一スキーマで定義し、`ladder(profile) → tier` がインストーラのプロファイル選択器に直結する。

```yaml
# profile.yaml — 導入先プロジェクトが宣言する特性
size: small | medium | large
lifetime: throwaway | year | multi-year
change_freq: low | high
state_complexity: low | high        # Alloy 層の要否に直結
failure_impact: low | high          # 課金・個人情報の有無等
```

| ティア | 含まれる層 | 想定プロファイル |
|---|---|---|
| **minimal** | 衛生チェック + unit small + ビルド再現 + PR 保護 | failure_impact=low × size=small |
| **standard** | minimal + medium・mutation advisory・test-smell・balance | ロジック有・lifetime=year+ |
| **full** | standard + mutation required・Alloy・smoke L1-L5・screenshot・PBT 網羅 | state_complexity=high × failure_impact=high（課金/個人情報）or 学習目的 |

**AI エージェント駆動の場合は 1 段厚くする。**
プロンプトの約束は確率的にしか守られないが機械的なゲートは決定的に守られる
（[投資原則 §AI エージェントが書くなら](https://github.com/Yos-K/app-development-notes/blob/main/notes/harness-investment.md)）。

> **未決定**: ラダーと harness-kit `profiles/` の正式整合は subtask_007h 完了後に実施
> （v0.1.1 候補）。それまでは上表の暫定3プロファイルで代替する。

## 7. 評価手順

```
① 発火実績を収集（subtask_004b 実測表を参照）
② CI コスト・頻度を収集（subtask_004a 実測表を参照）
③ 合成違反プローブを実施（予防型のみ）（subtask_004c 実測ログを参照）
④ 検知型: (発火数 × 独自性) / (CI時間 × 頻度) を算出
   予防型: プローブ生存確認 + 前提リスクの実在確認
⑤ §4 の積判定 3 条件を評価し、不成立の層には §4.5 の維持負荷シグナルを併せて測り、§5 の 5 区分を適用
⑥ 各判定に「なぜ→だから」と一次資料リンクを付記
   根拠が取れない項目は「未確認（004a/b/c の実測待ち）」と明記
⑦ 軍師が初回評価レポート（subtask_004e）として判定表を作成
⑧ removal-candidate は PR で人が承認してから撤去
```

**憶測記載禁止**: コードを読まずに判定を埋めない。1 サンプルから全体を一律推定しない。
根拠が得られない項目は空欄でなく「未確認（確認には○○の調査が必要）」と明記する。

---

*初回評価レポート: subtask_004e 完了後に `harness-roi-evaluation-2026-06.md` へリンクを追記*
