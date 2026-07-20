# 外部ハーネス参照評価: ECC（affaan-m/ECC）

## このドキュメントの目的

オーナー指示（2026-06-11）に基づき、マルチAIハーネス [ECC](https://github.com/affaan-m/ECC)
（"The agent harness performance optimization system"）の構成要素を localmd-reader の
既存ハーネスと突き合わせ、**追加実施すべきもの／意図的に採用しないもの**を根拠つきで判定する。

情報源: 2026-06-11 時点の同リポジトリ README の取得内容。バージョン固定の調査ではないため、
個々の数値（エージェント64・スキル261等）は参考値として扱う。

判定原則は既存のメタハーネス原則に従う:
**発火見込み・コストの実測なしにゲートを常設しない**（[harness-roi-framework.md](./harness-roi-framework.md)）。

## 判定サマリ

| ECC の要素 | 判定 | 一言理由 |
|-----------|------|---------|
| Hooks（イベント駆動の自動検査） | **採用候補（要裁可）** | 唯一の実質ギャップ。ただしエージェント中立な git hook 形が本PJに適合 |
| Continuous Learning（Instinct抽出→/evolve） | 採用不要 | 同型を探索ループ＋用語集還流で実現済み。裁可ゲートがある分こちらが安全 |
| AgentShield（102の汎用静的解析ルール） | 不採用 | 製品固有リスクは既存ゲートが被覆。汎用大量ルールの一括導入はROI原則違反 |
| マルチハーネス設定（.claude/.cursor/.codex/...） | 不採用 | AGENTS.md がエージェント中立の正典。複製設定は drift の温床 |
| Memory persistence（セッション間記憶） | 採用済み相当 | MCP長期記憶＋handover/CURRENT.md で充足 |
| Agents/Skills/Rules/Commands の大規模カタログ | 不採用 | 個人PJの規模に対し過剰。必要スキルはユーザー環境側で管理済み |
| トークン最適化・モデルルーティング | 対象外 | 開発支出の最適化はリポジトリのハーネス責務でない |

## 採用候補の詳細: イベント駆動フック（検知のさらなる左寄せ）

**なぜギャップか**: 現在の検知の最左端は `pr-preflight.sh`（手動実行）。コミットの瞬間に
自動で走る層は無く、preflight を忘れたまま push すると CI で初めて落ちる。実際に
スメル検出のローカル見落とし→CI初検知が過去に発生している（2026-06-05 セッション、
当時の対策は preflight への統合＝手動実行の改善に留まる）。ECC の Hooks は
編集・コミットといった**イベントで検査を自動発火**させ、この「忘れ」を構造的に消す。

**ただし ECC の形のままは適さない**: ECC のフックは Claude Code の設定
（PreToolUse/PostToolUse 等）に住む。本リポジトリは Claude Code と Codex の両方が
作業するため（`AGENTS.md` 冒頭）、特定エージェント専用フックでは片側しか守れない。

**だから（提案）**: エージェント中立な **git pre-commit フック**として導入する。

```
.githooks/pre-commit:
  sh scripts/check-no-committed-secrets.sh   # 最優先: 事故が不可逆（公開リポジトリ）
  sh scripts/check-file-sizes.sh             # 軽量・数秒
有効化: git config core.hooksPath .githooks（setup系スクリプトに追加）
```

- 対象を「速い・決定的・偽陽性ゼロ」の2チェックに絞る（コミット体感を壊すと無効化されて終わる）
- glossary/rule-doc currency は差分ベースで PR 文脈が要るため pre-commit に入れない
- **採用判断はオーナー裁可待ち**: 開発フロー（IDE / Termux 両方）へ介入するため。
  裁可後の実装は小さい（フック2行＋setup スクリプト1行＋ AGENTS.md 追記）

## 採用しない判断の根拠（要素別）

### Continuous Learning（Instinct 自動抽出）

ECC は使用パターンから知見（Instinct）を自動抽出しスキルへ統合する。本PJは同じ目的を
**探索ループ→用語集→（裁可）→Alloy/テスト**の経路で実現済み（[exploratory-testing.md](./exploratory-testing.md)
継続ループ節・[domain-knowledge-loop.md](./domain-knowledge-loop.md)）。
決定的な違いは**分類ゲート（オーナー裁可）の有無**: 観測挙動の自動正典化は
「バグが仕様になる」characterization の罠を踏む（#108 テーマクランプ窓の前例）。
自動抽出の速度より裁可の安全を取るのが本PJの設計判断であり、ECC 形への乗り換えは退行になる。

### AgentShield（汎用静的解析102ルール）

本PJの守るべき不変条件は製品固有性が高く（INTERNET 不在・reader WebView JS 無効・
秘密鍵不在）、専用ゲートが既に存在し**合成違反プローブで生存証明済み**
（[harness-roi-evaluation-2026-06.md](./harness-roi-evaluation-2026-06.md) §5）。
汎用102ルールの一括導入は「発火見込み未計測のゲート大量設置」であり、
維持費と偽陽性がROI評価の removal-candidate を量産する見込みが高い。
個別ルールで必要が生じたら（例: 本番コード Thread.sleep 検知、ROI評価 §7）、
発火見込みを測ってから1本ずつ足す。

### マルチハーネス設定ディレクトリ / 大規模カタログ / メモリ

- 設定の正典は `AGENTS.md` 1か所（エージェント中立）。`.claude/`・`.codex/` 等への複製は
  用語集同期で確立した「正典は1つ」の原則に反し、drift 検査層を新設するコストだけ増える
- スキル・ルールのカタログはユーザー環境（`~/.claude/`）で管理されており、リポジトリ側に
  重複させる理由がない
- セッション間で維持すべき決定はADRとGitHub Issueへ残し、個別エージェントのhandover文書を正典にしない

## まとめ: 何をするか

| 行動 | 状態 |
|------|------|
| pre-commit フック導入の裁可をオーナーに諮る（上記提案） | **裁可済み（2026-06-11「導入可」）** |
| 最小実装（secrets＋file-sizes の2本）を別PRで | **実装済み**: `.githooks/pre-commit` + `start-work.sh` での自動有効化 + AGENTS.md 記載 |
| AgentShield 型の個別ルールは必要発生時にROI計測してから1本ずつ | 発火見込みが観測されたとき |
| それ以外の要素は採用しない（本書を判断記録として残す） | — |
