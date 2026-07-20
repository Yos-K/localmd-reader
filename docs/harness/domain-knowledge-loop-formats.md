# ドメイン知識深化ループ — 様式集

## このドキュメントの目的

ドメイン知識深化ループ（[domain-knowledge-loop.md](./domain-knowledge-loop.md)）で
使用する2種類の様式（proposed-rule・トレーサビリティ表）の定義と記入例を示す。
読者はこの様式に従って記入することで、ループ各段の成果物を機械的に確認できる。

---

## 様式1: proposed-rule（分類ゲート S2 の成果物）

### フィールド定義

| フィールド | 必須 | 内容 |
|----------|------|------|
| `rule_id` | ✓ | `DR-{YYYY}-{NNN}` 形式（例: DR-2026-001） |
| `scenario_id` | ✓ | 探索シナリオID（S1 の出力と紐付け） |
| `observed_behavior` | ✓ | 観測した挙動を事実として記述。憶測禁止 |
| `evidence` | ✓ | 再現シード・操作列・ログ等の根拠 |
| `glossary_conflict` | ✓ | `あり` / `なし` / `未確認`（確認には○○が必要）|
| `als_conflict` | ✓ | 既存 assert との矛盾: `あり` / `なし` / `未確認` |
| `status` | ✓ | `proposed` / `approved-intent` / `approved-defect` / `rejected` |
| `approver` | 裁可時 | 殿のみ（移譲不可） |
| `approved_at` | 裁可時 | ISO 8601 日付 |
| `spec` | 裁可後 | 仕様記述ミニ言語による記述（下記参照） |
| `reject_reason` | 却下時 | 却下の理由 |

### 仕様記述ミニ言語 — 使用する記法

```
invariant "..."   — 不変条件（購入状態と権限の関係など）
policy "..." when Event — ポリシー（イベントを契機とした自動処理）
value Name { ... } — 値オブジェクト定義
error Name "..."  — エラー定義
```

記法サマリ（上記 4 種）はこのファイルに完結している。完全構文はプロジェクトの言語仕様書を参照。

### 記入例（proposed 状態）

```yaml
rule_id: DR-2026-001
scenario_id: SC-purchase-001
observed_behavior: >
  未知コード 'abc' を persistRoundTrip で persist し restore すると
  ProPurchaseState.UNKNOWN が返る（fail-closed）。
  persist→restore 往復で不正コードが UNKNOWN に縮退する。
evidence:
  seed: "jqwik seed=-3141592"
  action_sequence: [persistRoundTrip(code='abc'), restore]
  repro_command: "sh test.sh  # または ./gradlew :app:testFreeDebugUnitTest --tests '*ProPurchaseStatePropertyTest'（jqwik seed 固定の正確なオプションはパイロット 1 巡目で確定）"
glossary_conflict: なし
als_conflict: なし（pro-purchase-state.als L124 UnrecognizedCodeFailsSafeToFree が fail-closed 挙動を既に承認）
status: proposed
```

### 記入例（approved-intent 状態）

```yaml
rule_id: DR-2026-001
# ... （上記フィールドに加えて）
status: approved-intent
approver: 殿
approved_at: "2026-06-08"
spec: |
  invariant "ProPurchaseState.PURCHASED のみが Pro 権限を付与する"
  invariant "persist→restore 往復は状態を保持する（UNKNOWN→UNKNOWN も含む）"
  # L2 品質: 不変条件が定義されているためテスト導出可能
```

---

## 様式2: トレーサビリティ表（S4 二重反映の管理）

### 表の構造

```
シナリオID  →  ルールID  →  Alloy assert 名  →  テスト名
```

**目的**: 1ルール＝2反映（設計層 assert＋実装層テスト）の両着地を機械的に確認する。
どちらかが欠けていれば、その行を「未着地」と即座に判断できる。

### フィールド定義

| 列 | 内容 |
|----|------|
| シナリオID | 探索時に付与（`SC-{cluster}-{NNN}`） |
| ルールID | proposed-rule の `rule_id` |
| 承認種別 | `intent` / `defect` |
| Alloy assert 名 | `.als` ファイル内の assert 名。未着地は `—` |
| .als ファイル | assert が属するファイルのパス |
| テスト名 | JUnit/jqwik のテストメソッド名。未着地は `—` |
| テストファイル | テストが属するファイルのパス |
| ステータス | `両着地` / `assert のみ` / `テストのみ` / `テストのみ（設計層不要）` / `未着地` |
| コメント | 任意。`テストのみ（設計層不要）` の場合は不要理由を必ず記載 |

### 記入例

| シナリオID | ルールID | 承認種別 | Alloy assert 名 | .als ファイル | テスト名 | テストファイル | ステータス | コメント |
|-----------|---------|---------|----------------|-------------|---------|--------------|---------|---------|
| SC-purchase-001 | DR-2026-001 | intent | PersistRestorePreservesState | pro-purchase-state.als | `persistRestorePreservesState` | ProPurchaseStatePropertyTest.kt | 両着地 | |
| SC-purchase-002 | DR-2026-002 | defect | — | — | `snapshotAcknowledgedGrantsPro` | ProPurchaseStatePropertyTest.kt | テストのみ（設計層不要） | 単純な値チェック規則のため assert 不要 |
| SC-purchase-003 | DR-2026-003 | intent | OnlyPurchasedGrantsPro | pro-purchase-state.als | — | — | assert のみ | |

### 管理ルール

1. **S4 完了の定義**: ステータス列に「未着地」または「assert のみ」がない状態
2. **未着地の扱い**: dashboard 🚨要対応 に掲載（Action Required Rule 準拠）
3. **行の追加タイミング**: proposed-rule が approved になった時点で行を追加し、
   ステータスを「未着地」で仮登録する
4. **コメント列**: 設計上 assert が不要な場合（単純規則は例テストで十分）は
   ステータスを `テストのみ（設計層不要）` とし、理由をコメント列に記載

---

## 保管場所と更新タイミング

| 様式 | 保管場所 | 更新タイミング |
|------|---------|--------------|
| proposed-rule | `docs/harness/exploration-sessions/{YYYY-MM-DD}/proposed-rules.yaml` | S2 分類ゲートで起票・裁可受領時 |
| トレーサビリティ表 | `docs/harness/exploration-sessions/{YYYY-MM-DD}/traceability.md` | S4 二重反映の各着地時 |

セッション単位でディレクトリを分けることで、パイロット1巡の発見数・裁可結果を
そのまま計測データとして使用できる（常設化判断の入力）。

---

## 関連ドキュメント

| ドキュメント | 内容 |
|------------|------|
| [domain-knowledge-loop.md](./domain-knowledge-loop.md) | ループ定義・5段階・適用範囲 |
| [exploratory-testing.md](./exploratory-testing.md) | 探索的テストの運用ルール・チャーター様式 |
