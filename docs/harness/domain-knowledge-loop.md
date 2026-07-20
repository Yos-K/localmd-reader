# ドメイン知識深化ループ

## このドキュメントの目的

「まだ言語化されていない挙動」を発見し、裁可を経て検査可能な仕様（Alloy assert＋テスト）に
昇格させるループの定義と運用ルールを示す。

読者はここを見れば「どうやって暗黙知を形式知化するか」「どのクラスタに適用するか」「1巡で
何を計測して常設化を判断するか」を把握できる。

様式（proposed-rule・トレーサビリティ表）の記入例は
[domain-knowledge-loop-formats.md](./domain-knowledge-loop-formats.md) を参照。

---

## なぜこのループが必要か

**なぜ**: 既存ハーネスは「書かれた仕様」の回帰検査（Alloy / テスト / fitness）には強いが、
「まだ言語化されていない挙動」を発見する仕組みがない。挙動はコードの中に暗黙に存在し、
テーマクランプの「窓」のように、観測されるまで意図か欠陥か誰も判断していない。

**だから**: 探索で暗黙挙動を観測し、裁可を経て glossary ルール＋検査可能な仕様
（Alloy assert＋テスト）に昇格させるループを設ける。ドメイン知識が増えるほど検査網が
濃くなる正帰還を作る。
（根拠: 軍師レポート subtask_002a `loop_structure.why_needed`）

---

## 5段階の定義

各段の成果物が次段の入力となる。**段飛ばし禁止**（特に分類ゲート（S2）を飛ばして仕様化（S3）に進むこと）。

```
探索（S1）→ 分類ゲート（S2）→ 仕様化（S3）→ 二重反映（S4）→ コード反映（S5）
```

### 段階1（S1）: 探索（Explore）

| 項目 | 内容 |
|------|------|
| 入力 | 対象クラスタ（適用範囲は後述） |
| 出力 | 観測記録（シナリオID・操作列・観測挙動・再現シード） |
| 探索エンジン（第一候補） | ステートフル PBT（jqwik action-based・純JVM）。**探索用 PBT は発見の蒸留（反例の例テスト化）が完了したら退役させる**——堆積するとピラミッドが崩れるため（ライフサイクルは [exploratory-testing.md](./exploratory-testing.md)「探索成果物のライフサイクル」） |

**探索エンジンの根拠**:
- `test-strategy.md` の「速くて決定的なものを多く・頻繁に」原則に準拠
- `domain` 層は Android 非依存の純粋層で JVM 単体で実行可能
- large tier（エミュレータ）は flaky 前提・必須チェック禁止と `test-strategy.md` で明記済み

エミュレータ探索は「描画が絡む発見」のみに限定し、smoke ladder L5 実装後とする。

### 段階2（S2）: 分類ゲート（Classify）

| 項目 | 内容 |
|------|------|
| 入力 | 観測記録 |
| 出力 | proposed-rule（裁可待ち）→ 殿の裁可 → approved-intent または approved-defect |
| 裁可者 | **殿**（移譲不可。詳細は「分類ゲートの詳細」節） |

### 段階3（S3）: 仕様化（Specify）

| 項目 | 内容 |
|------|------|
| 入力 | 承認済みルール（approved-intent または approved-defect） |
| 出力 | glossary への L1/L2/L3 ルール追記（ルールID付与）＋ミニ言語記述 |

仕様記述ミニ言語（invariant・policy 等）の記法サマリは [domain-knowledge-loop-formats.md](./domain-knowledge-loop-formats.md) §様式1 に 4 種のインライン定義がある。

### 段階4（S4）: 二重反映（Reflect×2）

| 項目 | 内容 |
|------|------|
| 入力 | glossary ルール |
| 出力（設計層） | `.als` ファイルへ assert＋check 追加 |
| 出力（実装層） | 例テストまたは PBT property の追加 |

1ルール＝2反映が原則（詳細は「二重反映の設計指針」節）。

### 段階5（S5）: コード反映（Implement）

| 項目 | 内容 |
|------|------|
| 入力 | approved-defect のルール＋Red テスト |
| 出力（欠陥の場合） | コード修正（Red→Green） |
| 出力（意図の場合） | コード変更なし（テストは現挙動を性質として固定するのみ） |

---

## 分類ゲートの詳細

**なぜゲートが必要か**: 観測挙動をそのまま正典化すると characterization test の罠に落ち、
バグが仕様になる。前例: テーマクランプの「窓」（Free 化後も Pro テーマが表示され続ける窓があり、
`#108` で issue 化された）。もし観測時に「窓がある」を性質として固定していたら欠陥が永続化していた。
（根拠: 軍師レポート `classification_gate.why`）

**だから**: 観測挙動を「意図 / 欠陥」に分類するのは**殿の裁可事項**（移譲不可）とする。

### AI の役割（分類ゲートでできること・できないこと）

| できること | できないこと |
|-----------|------------|
| 観測の記録 | 「意図 / 欠陥」の最終判断 |
| 既存 glossary・.als との突合による矛盾検出 | — |
| proposed-rule の起票 | — |
| 裁可材料の提示（intent / defect 両論の帰結） | — |

**理由**: AI は設計意図を知り得ない（意図はコードに書かれていない）。

### proposed-rule のライフサイクル

```
proposed → (殿の裁可) → approved-intent
                      → approved-defect
                      → rejected
```

**掲載義務**: proposed-rule は **dashboard 🚨要対応** に必ず掲載する（Action Required Rule 準拠）。
滞留が増えるとループ全体が止まるため、proposed 数と滞留期間を可視化する。

---

## 二重反映の設計指針

**なぜ二重か**: Alloy モデルだけでは model-code gap が広がる（.als 先頭コメント「モデルは写し先であり
検査対象ではない」通り、実装が乖離しても Alloy は緑のまま）。テストだけでは設計レベルの無矛盾性が
検査されない。**だから 1ルール＝2反映**（設計層 assert＋実装層テスト）とし、トレーサビリティ表で
両着地を機械的に確認できるようにする。（根拠: 軍師レポート `dual_reflection.why`）

### Alloy assert の書き方指針

- assert 直前コメントに「何の保証か・破ると何が起きるか」を書く（`appearance-theme.als` の流儀）
- `check ... expect N` を必ず明示。既知の限界は `expect 1` で文書化
  （`pro-purchase-state.als` の `FromPersistenceCodeIsInjective` が前例）
- 静的/動的の判断: 値オブジェクト・純関数なら静的。操作列・時間経過が絡むなら動的（Session＋var＋always）
- 1ファイル300行以内。超過時はクラスタ内でファイル分割

### PBT property の書き方指針

- small tier（純JVM・jqwik）に置く
- テスト名は性質（仕様）、本体は具体例（t-wada 流）
- property 名は Alloy assert 名と対応させる
  （例: `assert OnlyPurchasedGrantsPro` ↔ `@Property onlyPurchasedGrantsPro`）
- ルールIDをコメントで紐付け（例: `// L2-7`）
- 単純規則は例テストで十分（PBT 強制はしない）

---

## 適用範囲

**対象（状態・順序が絡むクラスタ限定）**:

| クラスタ | 複雑性の源泉 |
|---------|------------|
| purchase（状態×復元） | ProPurchaseState × 永続化往復 × 外部変換 |
| タブ・Recent・Pin（順序×永続化） | 操作順による順序変化・保存・復元 |
| テーマ（権限×巡回） | Pro/Free 権限状態 × テーマ選択肢の巡回 |

**対象外（ステートレスな純関数）**:

**なぜ対象外か**: 単発入出力の性質は既存の unit/PBT で十分カバーでき、本ループの探索価値の
源泉は「状態×操作順序の組合せ爆発」にある。費用対効果が成立しない領域に広げない
（メタハーネス原則）。Markdown 整形等はその典型例。

---

## パイロット計画（purchase クラスタ）

**なぜ purchase が最初か**:
1. cmd_001 で Alloy 側の既設地点（7 assert）が完成済み
2. 既存テスト 12 件が実装層の既設地点
3. 状態×復元×外部変換（ProPurchaseState × ProPurchaseCacheEntry × BillingPurchaseSnapshot）の合成があり探索価値が高い
4. 収益・安全に直結（L2-7: purchased のみが Pro を付与する）

（根拠: 軍師レポート `pilot_plan.why_purchase`）

### 探索対象（purchase クラスタ 1 巡目）

| 探索シナリオ | 検証する組合せ |
|------------|-------------|
| persist→restore 往復 | 任意コード文字列（境界値・不正値含む） |
| BillingPurchaseSnapshot → ProPurchaseState → entitlement | state×acknowledged の合成写像 |
| ProPurchaseCacheEntry.restore の境界 | millis 非負制約との相互作用 |

jqwik でのアクション: `persistRoundTrip` / `snapshotToState` / `cacheRestore` の 3 種を試行。

### 計測と常設化判断

1 巡終了後に以下を計測し、常設化の可否を判断する:

| 計測項目 | 内容 |
|---------|------|
| コスト | 人手時間・トークン消費・実行時間 |
| 発見率 | proposed-rule 起票数 ÷ 探索シナリオ数 |
| 裁可結果 | intent / defect / rejected の内訳 |

**判断基準**: 実績データなしに常設化しない（メタハーネス原則: 001e の CI 昇格と同じ進め方）。
**ループの実走は本 cmd（cmd_002）に含まれない**。設計書 PR 承認後に別 cmd で実施する。

---

## cmd_001 との接続

| 成果物 | 本ループでの役割 |
|--------|--------------|
| `pro-purchase-state.als`（152行） | パイロット（purchase）の設計層既設地点。発見ルールの assert 追加余地は 148 行 |
| `domain-model-check.yml`（001e 成果物） | S4（設計層反映）の自動検査。ループが回るたびに advisory CI が検査 |
| `ProPurchaseStateTest` 12件＋`BuildProPurchaseStatusProviderTest` 1件 | パイロットの実装層既設地点（001a §5 で確認済み） |

**横展開時の順序**: 各クラスタの `.als` 完成 → そのクラスタでループ開始。
（根拠: 軍師レポート `cmd001_connection`）

---

## 関連ドキュメント

| ドキュメント | 内容 |
|------------|------|
| [domain-knowledge-loop-formats.md](./domain-knowledge-loop-formats.md) | proposed-rule 様式・トレーサビリティ表・記入例 |
| [test-strategy.md](./test-strategy.md) | テストサイズ（small/medium/large）の定義・ピラミッド方針 |
| [exploratory-testing.md](./exploratory-testing.md) | 探索的テストの運用記録 |
