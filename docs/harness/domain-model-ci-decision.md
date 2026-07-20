# 判断記録: ドメインモデル検査（Alloy）の CI 昇格

## このドキュメントの目的

`docs/domain/models/*.als`（Alloy 仕様モデル）の検査を GitHub Actions に
昇格するか否かの判断と、その根拠・設計を記録する。読者はこの文書から
「なぜこの CI ジョブが存在するのか」「なぜこの形なのか」を理解できる。

**結論: 追加する。** ワークフロー: `.github/workflows/domain-model-check.yml`
（助言的運用 = branch protection の必須チェックには登録しない）。

---

## 1. 判断: なぜ追加するのか

### なぜ

- 仕様モデルは設計段階のハーネスであり、ルール変更が既存保証を壊しても
  コードレベルのテストでは検出できない（`scripts/check-domain-model.sh`
  先頭コメント L4-12: "A spec change that breaks an existing guarantee …
  is invisible to review and to code-level tests until the code is written"）。
- モデルは3つある（`appearance-theme.als`、`navigation-anchors.als`、`pro-purchase-state.als`）。
  さらに他クラスタ（gestures / navigation / rendering 等の glossary 既存領域）
  への横展開が予定されており、「編集した人がローカルで手動実行する」運用は
  実行忘れに対する防御がない。
- 実行コストは小さい: Alloy jar は約 21MB・SHA-256 ピン留め
  （`check-domain-model.sh` L27-28）、解探索は有限の小さい状態空間
  （ローカル実測: pro-purchase-state.als 7 check が数秒で完了 —
  `queue/tmp/001d_model_check_result.md` 2026-06-07 実行記録）。
- flaky リスクは低い: ソルバは決定的で、非決定要素はネットワーク
  （jar 取得）のみ。取得失敗時はスクリプト自身が advisory skip
  （exit 0、L52-55）するため、CI を偽赤で塞ぐことがない。

### だから

`docs/domain/models/**` 変更時のみ発火する軽量ジョブを追加し、
仕様の保証劣化を PR 上で機械的に可視化する。2026-06-06 決定事項に従い
**助言的運用（非必須チェック）から開始**する。

---

## 2. 既存 CI の事実（調査結果）

| ワークフロー | 発火条件 | 性質 | 出典 |
|-------------|---------|------|------|
| ci.yml | 全 PR + push(main) | fitness(5分・スクリプト検査) / test / gradle-build | ci.yml L3-7 |
| mutation.yml | 全 PR（重い解析は logic 層変更時のみ git diff で判定） | **required** のため `paths:` 発火を避け「即 success 報告」方式 | mutation.yml L5-9 コメント |
| theme-screenshots.yml | workflow_dispatch のみ | レビュー資料生成（マージゲートでない） | 同 L5-7 コメント |
| device-smoke.yml | workflow_dispatch のみ | スモーク試験 | 同 L3-4 |

**読み取れる先例**: required check に `paths:` 発火を使うと「変更が
該当しない PR で check が pending のまま固まる」ため避けられている
（mutation.yml L5-9）。**逆に、非必須チェックなら `paths:` 限定発火で
この問題は起きない**（check が現れないだけでマージは塞がれない）。
本ジョブは助言的運用ゆえ `paths:` 発火を採用できる。

---

## 3. 設計判断の各論

### 3.1 `continue-on-error: true` を使わない

- **なぜ**: スクリプトが非ゼロで終了するのは (a) 反例の予期しない
  検出/消失（L82 `exec` + `expect N`）、(b) jar チェックサム不一致
  （L57-61）の 2 経路のみで、**どちらも本物の異常**である。
  java 不在・ネットワーク失敗という環境起因の skip は、スクリプト自身が
  既に exit 0 で吸収している（L32-35, L52-55）。
  `continue-on-error: true` を付けると本物の反例検出まで緑表示になり、
  「スキップ/失敗を成功と誤認しない」という本タスクの要求と矛盾する。
- **だから**: ジョブは正直に赤くする。助言的運用は「branch protection の
  必須チェックに登録しない」ことで実現する（赤でもマージは塞がれない。
  登録しなければデフォルトで非必須 — リポジトリ設定の変更は不要）。

### 3.2 発火条件

- **なぜ**: 仕様モデルの保証は `docs/domain/models/**` の編集と、
  検査系自身（スクリプト・ワークフロー）の変更でしか変化しない。
- **だから**: `paths:` を上記 3 系統に限定する（mutation.yml が自身の
  runner とワークフローを変更検出パターンに含めるのと同じ流儀、
  mutation.yml L51-52）。push は ci.yml と同じく main のみ（ci.yml L4-7）。

### 3.3 java セットアップ

- **なぜ**: ubuntu-latest で確実に java を用意すれば、「java 不在 skip」
  経路（L32-35）が CI では発生しなくなり、skip の発生源をネットワーク
  失敗 1 経路に絞れる。既存ジョブは全て temurin 17 を使用
  （ci.yml L61-65 ほか）。
- **だから**: `actions/setup-java@v5`（temurin 17）を必ず実行する。

### 3.4 Alloy jar のキャッシュ

- **なぜ**: jar は毎回 GitHub Releases から取得（L52）。キャッシュすれば
  2 回目以降のダウンロードと、ネットワーク失敗による advisory skip の
  発生確率自体を下げられる。
- **だから**: `actions/cache` で `~/.local/share/alloy` を保存する。
  キー は `hashFiles('scripts/check-domain-model.sh')` —
  バージョンと SHA-256 はスクリプト内に定義されているため、その変更に
  追従する（mutation.yml L61-66 の jar キャッシュと同じ流儀）。

### 3.5 skip の可視化（スキップを成功と誤認しない）

- **なぜ**: スクリプトは skip 時に stderr へ "skipping (advisory check)"
  を出して exit 0 する（L33-34, L53-54）。何もしなければ CI 上は
  「緑 = 検査済み」と区別がつかない。
- **だから**: ワークフロー側で出力を捕捉し、skip 文字列を検出したら
  `::warning::` アノテーションと `$GITHUB_STEP_SUMMARY` に
  「検査は実行されていない」旨を明記する。緑のままにする
  （exit code はスクリプトのものを尊重）が、PR 画面で警告が見える。

### 3.6 スクリプトの呼び出し形式

- **なぜ**: 呼び出し環境ごとの既定shell差を避け、POSIX shellとして同じ条件で実行する必要がある。
- **だから**: 既存CIの流儀どおり`sh scripts/check-domain-model.sh`と明示して呼ぶ。

### 3.7 solverの明示とAArch64

- **なぜ**: Alloy CLIはSAT4Jを既定にするが、AArch64では利用可能なnative solverの探索時に
  `NativeCode - findPlatform unknown`をERRORとして出力する。これはSAT4Jによる検査失敗ではないが、
  未検査と誤認しやすい。
- **だから**: `-s SAT4J`を明示し、既知のnative探索警告だけを
  `native solvers unavailable; verified with SAT4J`へ分類する。Alloyの終了コードとcheck結果は変更せず、
  反例や構文エラーは従来どおり失敗させる。

---

## 4. 守った制約

- 助言的運用から開始（2026-06-06 決定事項）: 必須チェック登録なし
- mutation floor 79% に影響なし（mutation.yml は無変更）
- ブランチ保護のバイパスなし（設定変更そのものを行わない）
- アプリ挙動を変えない（追加したのはワークフローと本文書のみ）

---

## 5. 既知のリスクと監視

| リスク | 影響 | 備え |
|--------|------|------|
| jar 取得のネットワーク失敗 | その回の検査が skip される（緑のまま） | 3.5 の warning で可視化。キャッシュ（3.4）で発生確率を低減 |
| Alloy のバージョン更新 | チェックサム不一致で exit 1（赤） | 意図された fail-fast（L57-61）。スクリプト側の定数更新で対応 |
| モデル増加による実行時間増 | ジョブの遅延 | 現状 2 モデル数秒。timeout 10 分で十分。超過が見えたらモデル別並列化を検討 |

## 6. 未決定事項

| 何を決めるか | 影響度 | 判断に必要な材料 |
|--------------|--------|------------------|
| 必須チェックへの昇格時期 | 中（マージゲート化） | 助言的運用での skip 発生頻度・偽赤の実績データ（数週間分の Actions 実行履歴） |

---

*判断: subtask_001e（cmd_001）/ 根拠提供: 001d 検査記録・各ワークフローファイル実読*
