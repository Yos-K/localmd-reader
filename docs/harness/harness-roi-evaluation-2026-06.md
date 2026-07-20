# ハーネスROI評価レポート — 2026年6月

> **評価フレームワーク**: [harness-roi-framework.md](./harness-roi-framework.md)（判定基準・評価軸の定義）
>
> **評価実施**: 軍師 / 評価日: 2026-06-07 / タスクID: subtask_004e
>
> **対象プロジェクト**: localmd-reader（Android アプリ、学習目的PJ）

## 1. 目的と対象

**なぜ評価するのか**: ハーネス層は追加時点では価値が明確に見えるが、時間経過とともに「効いている層」と「コストだけ残って効いていない層」に分かれる。定期評価なしに放置すると維持費が累積し、真に効果のある層への投資が圧迫される。

**だから**: keep/strengthen/removal-candidateの判定を実測データに基づいて機械的に行い、撤去・強化の判断根拠を明文化する。学習目的PJのため**判定は記録のみ・削除実行はしない**（[フレームワーク §4](./harness-roi-framework.md)）。

**評価対象**: localmd-reader に組み込まれた CI/CD ハーネス全17層（2026-06-07 時点）。

## 2. 評価方法

評価軸の定義は [harness-roi-framework.md §2-4](./harness-roi-framework.md) に準拠。本文書ではその結果のみを記録する。

| 実測サブタスク | 内容 |
|---|---|
| subtask_004a | CI実行時間・頻度計測（軍師QC修正済） |
| subtask_004b | 発火実績5事例の収集（実PR履歴） |
| subtask_004c | 合成違反プローブ6ゲートの生存証明 |
| subtask_004d | 評価フレームワーク文書の整備 |

**根拠原則**: 全判定はこれら4サブタスクの実測のみに基づく。憶測判定なし。根拠が取れない項目は `verdict=unconfirmed` と明記。

## 3. 判定結果サマリ

| 区分 | 件数 | 内訳 |
|---|---|---|
| **keep** | 15 | 現状維持（独自性あり or 安価） |
| **strengthen** | 2 | mutation testing・theme-screenshots |
| **downgrade-to-advisory** | 0 | — |
| **removal-candidate** | 0 | 積判定3条件①②③ 全成立なし |
| **合計** | 17 | 全層評価完了 |

removal-candidateがゼロである理由：学習目的PJの特性上、網羅的なハーネスは「学習を目的とした投資」であり（[投資原則 §目的を分ける](https://github.com/Yos-K/app-development-notes/blob/main/notes/harness-investment.md)）、コストが高くても即削除対象とはならない。それ以前に、コスト面でも積判定①（高コスト）が成立した層がなかった。

## 4. 全層の判定表

| 層名 | verdict | evidence 要約 | rationale（なぜ→だから） |
|---|---|---|---|
| fitness: check-no-committed-secrets | keep | 004c(f) PEM鍵プローブ → 検知exit1 / 004b(e) CI組込 2026-06-01 | 秘密漏洩リスクは公開リポジトリで実在。安価なgrepゆえ積①不成立 → keep |
| fitness: check-hard-constraints (INTERNET/JS) | keep | 004c(a)(b) INTERNET追加・JS有効化プローブ → 双方検知exit1 | オフライン保証・XSS防御は製品の中核約束。安価×独自性高 → 最重要級keep |
| fitness: check-conventional-title | keep | 004c(c) 不正タイトル検知・正常タイトル通過 確認 | 安価な衛生チェック（コスト≒0）→ 積①不成立でkeep |
| fitness: check-file-sizes | keep | 004c(e) 304行ファイル検知・例外スキップ確認 | 300行規律の機械強制。安価。例外機構も健全 → keep |
| fitness: check-test-smells | keep | 004c(d) Thread.sleep → Sleepy Test として検知exit1 | テスト品質劣化を安価に防ぐ → keep |
| fitness: glossary/rule-doc currency | keep | 004b(e) cmd_011でdoc stale化の実例観測 | ドメイン変更と用語集の乖離を防ぐ。同期コストはあるがdoc stale実害あり → keep |
| unit small (JUnit5+jqwik PBT) | keep | 004a 毎PR・平均116s / 569テスト / @Property 2ファイル実在 | ピラミッドの底。毎PR・高頻度だが独自性最大級 → keep |
| medium (Robolectric) | keep | src/testMedium に10ファイル実在 / 004b(a) #108 是正でmediumテスト追加 | smallが届かない永続化・Intent・WebView結合を検証 → keep |
| mutation testing (PITest+ratchet) | **strengthen** | 004b(b) ratchet 65→80% 4段階発火実績。残ホットスポット: SampledPoints 14・ViewerThemeStyle 13 | 発火実績豊富×独自性高 → keepだが残ホットスポットあり → strengthen |
| Alloy 形式検証 (domain-model-check) | keep | 004b(a) issue #108 でT1窓反例検出→修正（一次資料）/ 004a paths限定14s | 発火実績1件(#108)確認×paths限定で維持コスト極小 → keep（利益相反対応は §8） |
| smoke L1-L4 (device-smoke) | keep | 004a workflow_dispatch・手動低頻度 / 004b(d) #98 fixture不開→assert追加 | large層はflaky前提で非必須設計。手動ゆえ維持コスト小×起動/Intentの独自検知 → keep |
| smoke L5 (smoke-render-assert) | keep | cmd_009で新設（PR #121）。逆方向検知チェック実装済み | 描画アサートは他層に不在。手動低コスト。発火実績は初回run後に再評価 → keep |
| theme-screenshots | **strengthen** | 004a 平均329s・失敗率27.8% / workflow_dispatch手動 | 視覚証跡の価値はあるが失敗率が信頼を失う閾値に近い。手動ゆえremovalでなく → strengthen |
| ArchUnit (LayerDependencyTest) | keep | src/test/.../architecture/LayerDependencyTest.java実在 / unitジョブで毎PR追加コスト≒0 | レイヤ依存退行の独自検知×限界コスト≒0 → keep |
| Codex bot レビュー (外部) | keep | 004b(c) PR #117/#119で正当指摘3件・全ACCEPT（正当率100%） | 論理的冗長・等価mutantを検知（独自性高）。レビュー往復工数は別計測で許容範囲 → keep |
| preflight (pr-preflight.sh) | keep | 004c 各ゲートはpreflight呼び出しのcheck-*と同一（L53-99 6ゲート連鎖確認） | CI失敗の手戻りを未然に防ぐ。プローブ(004c)で全構成ゲートの生存確認済み → keep |
| play-release.yml (リリースワークフロー) | keep | 004a workflow_dispatch手動低頻度。失敗率62.5%だが2026-05-24試行錯誤集中 | リリース安全（署名・preflight・free-only強制）の独自層。失敗率はデータ不足でunconfirmed → 現状keep |

## 5. Key Findings（重要発見5件）

1. **層の充実と過剰投資の許容**: 17層中15層がkeep、2層がstrengthen、removal-candidateゼロ。学習目的PJとして層が充実しており、過剰投資の兆候は「あるが許容」（投資ノートの「学習目的なら網羅は投資」に合致）。

2. **予防型評価の最大の実用価値**: 予防型6ゲート全てが合成違反プローブ(004c)で生存証明済み。「発火ゼロ=無駄」という誤判定を構造的に回避できた。これが本フレームワークの最大の実用価値。

3. **theme-screenshots の要注意信号**: 失敗率27.8%が唯一の要注意シグナル。手動・低頻度ゆえ緊急性は低いが、flaky放置は「誰も赤を見なくなる」劣化の入口（投資ノート B2）。

4. **Alloy層の透明性確保**: Alloy層は利益相反厳格基準を適用しても keep が成立（#108発火実績+paths限定の極小コスト）。設計者判定の透明性を §8 に記録。

5. **検知の穴の特定**: Thread.sleepプロダクションゲートのギャップを発見。過剰投資と検知の穴の両面を評価できたことは、ROI評価の目的（§1）を双方向で果たした証拠。

## 6. Strengthen 2件の推奨アクション

### mutation testing（PITest+ratchet）

**なぜstrengthenか**: 発火実績豊富（ratchet 65→80% 4段階）で独自性も高い。だが残ホットスポット（SampledPoints 14/ViewerThemeStyle 13）が判定できていないmutantとして残っている。

**だから**: 残ホットスポットのテストカバレッジを分析し、費用対効果が見合う場合に次のratchet引き上げを別cmdで実施する。**「床上げを競技にしない」（軍師評価）**— 自動化のための自動化に陥らないよう、コスト計測を先行させること。

### theme-screenshots（視覚証跡）

**なぜstrengthenか**: 失敗率27.8%はflaky判定の閾値に近い。手動・低頻度ゆえ即撤去の対象ではないが、信頼性が低下すると「誰も結果を見なくなる」という副作用が生じる。

**だから**: flaky要因を特定し安定化する。失敗ログから非決定的要因（タイミング・環境差）を特定し、スクリーンショット取得の待機条件を調整する。安定化できなければdowngrade-to-advisoryを検討する。

---

**C-7 投資判断・縮小導入（2026-06-07 / subtask_020b→cmd_025）**

ROIフレームワーク初の実戦適用として、以下の裁可3点が確定した。

| 裁可 | 決定内容 | 根拠 |
|------|----------|------|
| ①縮小導入 | uiautomator dump 方式の領域限定比較を実装（`scripts/visual-regression-check-limited.sh`） | 低コスト・ピクセル依存なし・flaky土台に偽陽性を積まない |
| ②flaky先行 | theme-screenshots flaky 27.8%→0% 解消（cmd_024 / PR #134）を縮小導入の前提として先行 | 不安定な土台の上に視覚アサートを積むと偽陽性が重なり信頼性が崩壊する |
| ③フル比較見送り | Roborazzi/SSIM/ピクセル一致は当面見送り | ROI不成立（PR #126でも基準画像0枚変更・発火見込み乏しい・構築コスト高・影響半径小） |

縮小導入の詳細は `queue/tmp/020b_screenshot_roi_recommendation.md`（軍師評価）参照。

## 7. ギャップと対応方針

**ギャップ**: プロダクションコードの `Thread.sleep` 検知ゲートが未整備。

**根拠（004c(d)より）**: `check-test-smells.sh` はテストコードの `Thread.sleep` のみを検知する。プロダクション側の `check-no-thread-sleep.sh` は不在。

**なぜ問題か**: 本番コードの不用意なsleep（UIスレッドブロック等）を機械検知する層がない状態では、Androidのフリーズ原因となりうる実装が混入しても自動的に検出できない。

**だから**: バックログ候補として追加する。ただし**即着手ではない**。AndroidでsleepがUIブロックになる箇所は限定的なため、新ゲート設置のROIを測定してから投資判断を行う（004e軍師評価の推奨どおり）。

## 8. 利益相反開示（Alloy層）

Alloy形式検証層（`domain-model-check`）の判定について、以下の利益相反を記録する。

**開示事項**: 本層は軍師（評価者本人）が subtask_001c/001e で設計・CI昇格判断した層である。設計者が自身の層を評価するバイアスが存在する。

**排除のための措置（004e conflict_of_interest_log より）**:
1. 発火実績は実在issue #108 のみを根拠とし、「将来の有用性」等の主観的見込みを判定根拠に含めない
2. コストは004aの実測値（paths限定・14s）のみ採用
3. removal積判定を他層と同一基準で適用（①高コスト不成立を実測で確認）

**結論の根拠**: keepは「安価×実発火×独自性」の客観3点で成立しており、設計者の希望的観測に依存しない。

**正直な記録**: #108という単一発火実績に立脚しており、発火サンプルが1件と少ない。今後の発火/不発で再評価すべき層として明記する。

## 9. 次回評価のトリガー条件

| トリガー | 根拠 |
|---|---|
| theme-screenshots 失敗率が 35% を超えた場合 | 現在27.8%。信頼性喪失ラインを超えたら strengthen → downgrade-to-advisory を再判定 |
| Alloy層 #108以降の次の発火 or 6か月不発 | 単一発火実績を補強 or 再評価の判断材料 |
| smoke L5 (smoke-render-assert) 初回発火後 | 新設層のため、初回run後に発火実績ありの条件で再評価 |
| mutation testing 次ratchet実施前後 | 残ホットスポットのコスト計測結果をもとにstrengthen→keep/downgradeを判定 |
| play-release.yml 通常運用データが5件以上蓄積 | 失敗率62.5%は試行錯誤期の特異値。通常運用データで再判定 |
| 全層を第2軸（維持負荷シグナル §4.5）で再評価 | フレームワークに `consolidate-candidate` を追加（2026-06-14）。初回評価は CI コスト軸のみで removal-candidate=0 だった。安価でも維持負荷の高い層を第2軸で測り直す → **実施済み（2026-06-14）**: [harness-roi-2nd-axis-evaluation-2026-06.md](./harness-roi-2nd-axis-evaluation-2026-06.md)。consolidate-candidate 4件（doc同期2ガード統合 / emulator6本集約 / kit消費装置の重量再考 / 正典文書ハブ化）を起票 |

---

*本レポートは subtask_004e（軍師初回ROI評価）の実測データに基づく。
フレームワーク: [harness-roi-framework.md](./harness-roi-framework.md)*

---

## 追補: 新設層の初回評価（2026-06-12）

2026-06-10〜12 に新設された5層の初回判定。評価軸は本文 §2 と同じ（発火実績・プローブ生存証明・コスト実測のみ。憶測なし）。

| 層 | verdict | evidence 要約 | rationale（なぜ→だから） |
|----|---------|--------------|------------------------|
| gesture-smoke L5（PRトリガー・非必須） | keep | 導入時に自身の欠陥2件を実測検出（emulator-runner行分割 run 27285682358・ジェスチャーナビ衝突 run 27286076577）。#149 で dp 修正の実機検証に寄与。起動flake 1件 | アプリ欠陥の検出はまだ0だが、**導入過程の発見が既存smoke 2本の実害（device-smoke/render-l5 即死状態）の特定に波及**＝メタレベルで発火済み。flake は1/4 run で要観察 → keep |
| merge-integrity（マージ後検証） | keep | #144 以降の全マージで稼働・全green（レース再発0）。発火0 | 予防不能なリスク（#80実害）の検知層。「発火ゼロ=無駄」ではなく守るリスクが実在（フレームワーク Key Finding 2 と同型）。コスト≒0（マージ時のみ数秒） → keep |
| pre-commit フック | keep | 合成違反プローブ2種（301行.java・PEM鍵）で拒否を実証。実発火0。コスト実測 ~1s/commit | 検知の最左端。プローブ生存証明済み×コスト極小 → keep。実発火は次回評価で再確認 |
| 探索ループ（常設化） | **strengthen** | 全4セッション累計（フッター集計: appearance 6/5・rendering 1/1・gestures 8/4・viewer 5/4 ※viewer は PR #154）: probe 56本・**findings 20件・glossary還元 14項目**・proposed-rule 4件（**PR-1〜3 裁可済み・PR-V1 裁可待ち**）・issue 2件（#102/#147）→ #147 は修正 PR #149 マージまで到達。コスト ~20-25分/セッション | **新設層で最大の発火実績**。S1→S5 の全段が1サイクル実走済み。だから未踏2クラスタ（navigation/purchase）へ拡張し、purchase はステートフルPBTパイロットと合流させる → strengthen |
| 品質測定（complexity/coupling） | 評価保留 | ベースライン取得・改善優先順位3件導出（renderer分解・SampledPoints統合・MainActivity共変更）。導入当日 | 発火実績を測る期間がゼロのため判定しない（実績データなしに判定しない原則）。次回ROI評価で初回判定 |

### 次回評価のトリガー条件への追加

| トリガー | 根拠 |
|---|---|
| gesture-smoke の flake 率が 3/10 run を超えた場合 | 現在 1/4。信頼性低下は「誰も見ない赤」の入口 |
| merge-integrity がレースを実検知した場合 | 検知型としての初発火＝価値の実証 |
| pre-commit フックの実発火（プローブ以外） | 左寄せの実効性データ |
| 品質測定の2〜3サイクル運用後 | ratchet 化（--strict ゲート昇格）の判断材料 |
