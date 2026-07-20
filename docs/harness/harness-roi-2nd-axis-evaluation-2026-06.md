# ハーネスROI評価 — 第2軸（維持負荷シグナル）適用レポート 2026-06-14

> **評価軸の定義**: [harness-roi-framework.md §4.5](./harness-roi-framework.md)（維持負荷シグナルと `consolidate-candidate`）
> **初回評価（第1軸=CIコスト）**: [harness-roi-evaluation-2026-06.md](./harness-roi-evaluation-2026-06.md)
> **評価日**: 2026-06-14 / 対象: localmd-reader のハーネス全層

## 1. なぜこの評価をするのか

初回評価は第1軸（CIコスト×頻度）だけで判定し、**removal-candidate=0**（安価ゆえ誰も消せない）に終わった。
これは「層の純増の追認」になりやすい（framework §4.5 の問題提起）。**だから**、CI秒と直交する
第2軸『維持負荷シグナル』——人間/AIが継続的に払う注意・規律・読解——を実測し、安価でも負荷の高い層を
`consolidate-candidate`（撤去ではなく**縮小・統合・自動化**の助言）として洗い出す。学習目的PJのため
**起票のみ・縮小の実施は人が承認**する（免責は削除実行にのみ及ぶ）。

## 2. 3シグナルの実測（憶測なし・出典はコマンド/ファイル）

| シグナル | 測定値 | 取得方法（再現コマンド） |
|---|---|---|
| 人的維持時間: handover 更新 | **63 コミット** | `git log --grep=handover --since=2026-03-01 \| wc -l` |
| 人的維持時間: doc 同期(glossary/rule/currency) | **61 コミット** | `git log -i --grep='glossary\|rule-doc\|currency'` |
| 人的維持時間: mutation floor 調整 | 30 コミット | `git log -i --grep=mutation` |
| 人的維持時間: harness-kit/sync 関連 | kit 22 / sync 12 コミット | `git log -i --grep=harness-kit` / `--grep=sync` |
| 認知負荷: ワークフロー総数 | **13 本** | `ls .github/workflows/*.yml \| wc -l` |
| 認知負荷: 手動 emulator/視覚系ワークフロー | **6 本** | device-smoke / gesture-smoke / smoke-render-l5 / theme-screenshots / visual-regression-check-limited / exploration-emulator（全 workflow_dispatch） |
| 認知負荷: check-*.sh 数 / docs/harness md 数 | 13 / 18 | `ls scripts/check-*.sh` / `ls docs/harness/*.md` |
| 認知負荷: 「正典」を主張する文書数 | **12 ファイル** | `grep -rl 正典 AGENTS.md docs/` |
| ドリフト誘発: AGENTS.md の必須運用ルール / トレーラ要求 | 3 / 3 | `grep -c 運用ルール AGENTS.md` / `Impact:` |
| ドリフト誘発: 実インシデント記録 | #80 auto-merge レース・9日27本ドリフト | `grep -rl '#80'` / `'27本'` |

**読み方の注意（過大判定の自戒）**: 高チャーン＝即ムダではない。handover 63 は 63 セッション分の継続性を
提供しており価値もある。第2軸は「人の手が頻繁に要る／重複／規律依存」を可視化するだけで、**ムダかは
重複・規律の有無で判断**する。以下はその条件を満たす層に絞って起票する。

## 3. consolidate-candidate 判定（4件）

### C1. doc 同期ガード2種の統合 — `check-glossary-currency` × `check-rule-doc-currency`

- **なぜ（実測）**: 両者は**同一形状**——どちらも `git diff` ベースで「Xを変えたのにdoc Yを更新しないと fail、
  ただしトレーラで免除」（`scripts/check-rule-doc-currency.sh` L77 / `check-glossary-currency.sh` 確認）。
  トレーラが `Glossary-Impact:` と `Rule-Docs-Impact:` の2系統あり、寄稿者は**2つの規約を覚える**必要がある。
  doc 同期チャーンは 61 コミット。
- **だから（統合）**: 2ガードを1つの currency エンジン（対象ファイル群→要更新doc群→共通トレーラ）に統合し、
  トレーラを1系統（例 `Docs-Impact: none (...)`）に集約する。検知能力は維持しつつ認知負荷を半減。
- **やりすぎライン**: ガードを削除しない（doc stale の実害は初回評価で確認済み＝予防価値は残す）。

### C2. emulator/視覚ハーネスの入口集約 — 手動6ワークフロー

- **なぜ（実測）**: device-smoke / gesture-smoke / smoke-render-l5 / theme-screenshots /
  visual-regression-check-limited / exploration-emulator の **6本がすべて workflow_dispatch（手動）** で
  emulator 起動を共有し、「アプリが実機で起動/描画/操作できるか」という近接した関心を別々の入口に持つ。
  維持者は6つの手動エントリを記憶する必要がある（認知負荷）。
- **だから（統合）**: 1つの emulator ハーネス・ワークフロー（`mode: smoke|render|gesture|theme|visual|explore`
  の入力）に集約し、共通の起動/証跡ステップを1か所に。各 L5 アサートは mode 別 job として残す。
- **やりすぎライン**: 各検査の**アサート内容は統合しない**（描画・ジェスチャ・テーマは別の失敗モード）。
  入口とセットアップだけ集約する。

### C3. harness-kit 消費装置の重量バランス再考

- **なぜ（実測）**: consumed_scripts は **7本**。それを支える装置は harness-sync.yml + harness-drift-check.yml +
  check-kit-drift.sh + AGENTS.md 運用ルール（「consumed を変えたら同週内に kit 還元」）+ sync-manifest。
  kit/sync 関連チャーン 34 コミット、9日で27本ドリフトの実害履歴。**ドリフト誘発シグナルが全層で最も高い**
  （正しさが手作業規律に依存。初回評価 §9 で被験候補に明記済み）。
- **だから（自動化 or 縮小）**: 二択を人が判断する。(a)**自動化**: 消費スクリプト変更時に kit 還元 PR を
  自動起票する仕組みを足し、手作業規律を機械化する。(b)**縮小**: 7本の消費価値が装置の維持費に見合うか
  再評価し、見合わなければ消費本数を絞る（自前管理に戻す）。**今すぐ装置を増やさない**ことが第1。
- **やりすぎライン**: drift-check（今回新設・非ブロッキング）は撤去しない。可視化の価値は確認済み。
- **判断（2026-06-14・オーナー決定）: 据え置き（装置を増やさない）。** 根拠: harness-kit 側の自動化が
  この時期に前進し（kit#34 依存の自動解決・kit#35 CI ゲート付き同期 PR(PAT)・sync-check の opt-in 採用集合モデル・
  drift-check のテスト化）、kit→consumer 方向の手作業はほぼ解消。consumed は8本（衛生3・version3・
  check-file-sizes・run-mutation-tests・android-kotlin-compile）で mutation gate 等が高価値ゆえ (b)縮小は
  ドリフト再発リスクが上回る。(a)還元自動化は「今すぐ装置を増やさない」原則に反し、還元は汎用化判断を伴い
  安全な機械化が困難。よって**現状維持**: ペアリング規則 + drift-check advisory を tripwire として残し、
  新規装置は足さない。次回 ROI 評価で消費本数を再測する。C3 はこの判断記録をもって close。

### C4. 「正典」文書の分散 — 12ファイルが source of truth を主張

- **なぜ（実測）**: `正典` を名乗る文書が 12 ファイル、docs/harness 内の相互リンクが 40 本。
  どれが何の正典かを引くのに認知負荷がかかる（共通規約「記号の単独使用禁止」と同根の、参照解決コスト）。
- **だから（統合）**: docs/harness にハブ1枚（「どの規則はどの文書が正典か」の単一索引）を置き、
  各文書の冒頭で正典範囲を1行宣言。新規正典を増やす前にハブ更新を必須化する。
- **やりすぎライン**: 既存文書の統廃合は別タスク（レビュー負荷大）。索引の新設に留める。

## 4. consolidate-candidate にしない層（バランスのための明示）

逆方向の追認（全部を縮小候補にする）を避けるため、安価×低負荷で**素直に keep** の層を明示する。

| 層 | なぜ縮小候補でないか |
|---|---|
| check-no-committed-secrets / check-hard-constraints / check-file-sizes / check-conventional-title | 単機能 grep・規律依存なし・重複なし・チャーン極小。第2軸3シグナルすべて低 → keep |
| merge-integrity | #80 という実害に対する検知層・コスト≒0・他層と重複しない失敗モード → keep |
| unit small / ArchUnit | 毎PR・独自性最大・人的維持時間は通常開発の一部で「ハーネス固有の追加負荷」ではない → keep |

## 5. 起票するアクション（人が承認）

| ID | 区分 | アクション | 影響度 | 判断に必要な材料 |
|---|---|---|---|---|
| C1 | consolidate | doc 同期2ガードを1エンジン+1トレーラへ統合 | 中（寄稿フロー簡素化） | 両スクリプトの対象集合の差分が統合可能か |
| C2 | consolidate | emulator 6ワークフローを mode 付き1本へ集約 | 中（CI定義の簡素化） | 各 workflow の共通セットアップ差分 |
| C3 | consolidate | kit 消費装置を自動化 or 消費本数を縮小 | 高（同期方針の根幹） | 7本の消費価値 vs 装置維持費の定量比較 |
| C4 | consolidate | 「正典」ハブ索引の新設 | 低（doc 認知負荷） | 12正典文書の責務一覧 |

**次回評価のトリガー**: C1〜C4 のいずれかを実施したら、その層の第2軸シグナルを再測定して効果を確認する。
未実施でも、ワークフロー数が15を超えた／正典文書が15を超えた時点で第2軸を再適用する。
