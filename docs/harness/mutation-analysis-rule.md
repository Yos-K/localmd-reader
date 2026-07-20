# ミューテーション結果の分析・改善ルール

## このドキュメントの目的

ミューテーションテストは「スコアを測るだけ」では価値がない。**CI が毎回スコアと弱点を出し、それを AI（または人）が分析してテストを強化し、しきい値（floor）を上げていくループ**を回して初めてハーネスとして機能する。本ルールはその分析手順・改善判断・禁止事項を定める。

関連: 実行 `scripts/run-mutation-tests.sh` ／ CI `.github/workflows/mutation.yml` ／ 優先度 `claude-harness-engineering-todo.md` 項目5 ／ 品質基準の三層検証（手動欠陥挿入 → ミューテーション → PBT）の中段。

## 全体像 — 駆動の輪

```
PR → CI(mutation.yml) が実行
   → mutation-report(HTML + mutations.xml) を artifact 化
   → Step Summary に「SURVIVED ホットスポット」を AI 可読で出力   ← 信号(sensor)
        ↓
   AI/人が分析（status で triage → 価値で優先順位 → 例テスト追加 or 除外判断）
        ↓
   再計測して KILLED 化を確認 → floor を ratchet（上げるのみ）
        ↓
   判断を記録（テスト・用語集へ反映）→ 次の PR へ
```

「測る → 分析する → 直す → 基準を上げる」が閉じて初めて、ミューテーションが品質を押し上げる。**分析と改善を伴わない計測は実施しない。**

## スコアの読み方: 計測面（除外）の明示（MUST）

**なぜ重要か**: 本リポの floor（2026-06-14 時点 82%）は **全クラスを母数にしたナイーブな 82% ではない**。
ロジック以外を意図的に計測面から外したうえでの 82% であり、除外を知らずに「mutation 82%」とだけ
語ると、カバレッジを過大評価させる（＝報告が事実より良く見える）。だから対外的に数値を引用する
ときは、必ず本節の除外前提をセットで示す。

計測面と除外の出典は `harness.config.sh`（`run-mutation-tests.sh` がソースする）:

| 区分 | 設定 | 内容と理由 |
|------|------|-----------|
| 対象クラス | `TARGET_CLASSES` | `domain.* / viewer.* / file.* / infrastructure.*` のみ。`presentation.*`（Android UI）は対象外＝**UI 層は母数に入らない** |
| 除外クラス | `EXCLUDED_CLASSES` | `*Test/*Tests/*Property/*Properties` に加え `ViewerText*`（i18n 文言テーブル、~180 NO_COVERAGE の文言 return）を除外。文言一致テストは脆く低価値（優先順位ルール3） |
| 除外テスト | `EXCLUDED_TESTS` | `architecture.*`（構造アサートで mutant を殺せない）と `*Property/*Properties`（PBT は1変異あたり多試行で PITest 実行時間が爆発）を kill 集合から除外 |

**だからこの 82% は**:「`presentation` を除く domain/viewer/file/infrastructure のロジック面を、PBT・構造テスト・
i18n 文言を母数から外して測った検出力」である。各除外は上表のとおり個別に正当化済みで silent 除外は無い
（NEVER の「silent な除外」を参照）。除外面を**広げ直す**（例: presentation を対象化）と数値の意味が変わるため、
floor の比較は常に同一計測面の上でのみ行う。

## status の読み方と対応（MUST）

`mutations.xml` の各 `<mutation status='...'>` を次のように扱う。

| status | 意味 | 対応 |
|---|---|---|
| `KILLED` / `TIMED_OUT` | コードを壊したらテストが落ちた＝検出できた | 対応不要 |
| **`SURVIVED`** | テストはあるが変化を捕まえない＝**アサーション不足** | **最優先で対応**（例テストで殺す） |
| **`NO_COVERAGE`** | その行を通る例テストが無い | テスト追加。低価値なら除外判断（理由を記録） |
| `RUN_ERROR` / `MEMORY_ERROR` | minion の環境ノイズ | 頻発時のみ環境調整。**floor を下げて回避してはならない** |

## mutator → 追加すべき例テスト（どう直すか）

SURVIVED は「どの変異が生き残ったか（mutator）」を見れば、不足しているテストの種類が分かる。PITest 既定 mutator のうち本リポで観測する主なもの:

| mutator | 何を書き換えるか | 殺すための例テスト |
|---|---|---|
| `ConditionalsBoundaryMutator` | `<` ↔ `<=` などの**境界** | 境界値（最小 / 最大 / ±1）を突くテスト |
| `NegateConditionalsMutator` | 条件の真偽**反転** | 分岐の true 側・false 側を**別々に**検証 |
| `MathMutator` | `+` ↔ `-` などの**演算** | **計算結果の具体値**をアサート |
| `IncrementsMutator` | `++` / `--` | カウンタ / ループ結果の値を検証 |
| `EmptyReturns` / `PrimitiveReturns` / `BooleanTrue/FalseReturns` | 戻り値を既定値に置換 | **戻り値そのもの**をアサート（呼ぶだけにしない） |
| `VoidMethodCallMutator` | 副作用呼び出しを**削除** | 副作用（保存・追記など）の**結果**を検証 |

## 優先順位 — 価値で判断する（MUST）

すべての SURVIVED を等価に潰そうとしない。価値の高い順に対応する。

1. **ロジック / 分岐 / 計算クラスの SURVIVED**（例: Markdown レンダラ、ジェスチャ幾何、ポリシー、値オブジェクト）＝最高価値。例テストで殺す。
2. ロジッククラスの `NO_COVERAGE` ＝例テストを追加。
3. **i18n 文言・定数データ（例: `ViewerText` の文言テーブル）＝低価値**。文言一致テストは脆く価値が低い → `--excludedClasses` で**対象から外し、理由をコメント／本ルールに記録**（黙って削らない）。
4. **等価ミューテーション**（意味的に同一で原理的に殺せない）＝記録のうえ除外し、スコアの責に帰さない。

## やってはいけない（NEVER）

- **floor を下げて緑にする**。緑にできないときはテストを足すか、除外を正当化して記録する。基準を緩めて通すのは禁止。
- **メトリクスを上げるためだけのテスト**（アサーションが無い／トートロジー／実装をなぞるだけ）。テストは仕様を表す（テスト名＝性質）こと。仕様を表さないテストで mutant を殺すのは禁止。
- **silent な除外**。クラス・mutator を除外したら、必ずスクリプトのコメントか本ルールに「何を・なぜ」を残す（黙って範囲を狭めない）。
- **PBT を mutation の常用テストに混ぜる**（1 変異あたり多数試行の再実行で爆発する）。PBT は毎 PR のユニットジョブで別途実行する。

## ratchet — floor の引き上げ

- 現状: **floor 82%**（正典は `harness.config.sh` の `MUTATION_THRESHOLD`。本文の数値が config と
  食い違ったら config を正とする）。計測面は上の「スコアの読み方」節のとおり（presentation・i18n・PBT 除外）。
  本番コードのベースライン **~83%**（テスト強度 ~89%）。
- 直近: **方向ジェスチャ `DirectionalGesturePath` の幾何閾値を例テストで固定**。最小サイズ境界（高さ支配で size を厳密に 72 にできる＝3点パスは始点・終点が保存される性質を利用）・原点から離れた小さなシェブロンの棄却・脚が短すぎる棄却（水平/垂直）・base ドリフトの棄却を追加。`Bounds`（size/width/height）の survivor を全 KILL＋親クラスの survivor 45→40。81%→82%、floor を緑 −3pt の **79%** へ。
  - **残存の記録（deferred、silent禁止のため）**: `DirectionalGesturePath` に残る survivor は主に4検出器（`isLeft/Right/Up/DownChevron`）の **apex 深さ・leg span 比率の境界/演算 mutant**。これらは閾値ちょうどの幾何が必要だが、`fromPoints` が 32 点へ弧長リサンプリングするため apex 座標が近似になり、**境界ちょうどを突くと脆いテスト**（実装をなぞる/数値合わせ）になりやすい＝ルールの「メトリクス目的のテスト禁止」に抵触する。よって**意味のある仕様（最小サイズ・脚）に絞って固定し、純境界 mutant は deferred**。`minXIndex` 等の `<`→`<=` は一意極値のとき**等価**（原理的に殺せない＝優先順位ルール4）。
  - **base-drift 述語は実質等価（優先順位ルール4、Codex レビューで判明）**: 各シェブロンは apex が端にあるため幾何的に `apexDepth + baseDrift == span` が常に成り立ち、比率も `0.55 + 0.45 == 1.0` で相補的。よって **base-drift 棄却は apex-depth 棄却に論理的に包含**され、base が歪んだ形状は必ず先に apex-depth で弾かれる。base-drift mutant（`isLeft/Right/Up/DownChevron` の `Math.abs(start-end) <= span*0.45`）を分離して殺すにはナイフエッジ入力が必要で脆い＝**等価扱いで deferred**。当初の「base ドリフト棄却」テストはこの包含のため base-drift でなく apex-depth を突いており、誤解を招くので削除した（指摘: PR #117 Codex）。**併せて production に潜む示唆**: base-drift 述語は apex-depth と冗長な可能性があり、将来の設計改善（デッド述語の整理）候補（本PRの範囲外）。
- 旧経緯: (a) 相対リンク描画（`appendMarkdownLinkIfPresent` / `isSafeRelativeLinkUrl`）に空URL・先頭コロン・root絶対パス・未終端・リンク後続テキストの境界例テストを追加、(b) **i18n 文字列テーブル `ViewerText*`** を `--excludedClasses` で除外（優先順位ルール3）。この2点で 75%→81%（NO_COVERAGE 339→158）、floor 70→78。`ViewerText*` 除外理由は `EXCLUDED_CLASSES` のコメントにも明記（`fromLanguage()` ロジックは別途テスト済みで実質低下なし）。
- さらに旧: コードハイライタ群（Css/Markup/Keyword/Java）へ例テストを追加して 72%→74% にした際に floor を 65→68→70 へ段階的に引き上げ。
- 改善 PR ごとに floor を「**最新の緑スコア − 小 buffer**（端数切り捨て）」へ**上げる**。決して下げない（`MUTATION_THRESHOLD` 既定値を更新）。
- 80% 到達後も、minion ノイズによる揺れの回帰防止のため「緑スコア − 数 pt」を floor として維持する。

## 分析コマンド（AI が実行する）

CI の artifact、またはローカル実行（`sh scripts/run-mutation-tests.sh`、純 JVM・~80 秒）後の `build/mutation/report/mutations.xml` に対して:

```sh
X=build/mutation/report/mutations.xml
# SURVIVED をクラス別に集計（多い順＝弱いテストの優先順位）
grep "status='SURVIVED'" "$X" \
  | sed -E "s@.*<mutatedClass>([^<]+)</mutatedClass>.*@\1@" \
  | sort | uniq -c | sort -rn | head
# 特定クラスの SURVIVED 詳細（mutator / method / line を見て不足テストを判断）
grep "status='SURVIVED'" "$X" | grep '<MutatedClassName>'
# NO_COVERAGE をクラス別に集計（例テスト不在）
grep "status='NO_COVERAGE'" "$X" \
  | sed -E "s@.*<mutatedClass>([^<]+)</mutatedClass>.*@\1@" \
  | sort | uniq -c | sort -rn | head
```

`run-mutation-tests.sh` は実行後に同じ triage（SURVIVED 上位・NO_COVERAGE 上位）を標準出力と `build/mutation/report/triage.txt` に出す。CI はこれを Step Summary に転記するので、**スコアが落ちた PR では「次に直すべきクラス」が結果画面に直接出る**。

## 改善 PR の流れ（推奨）

1. triage 上位の SURVIVED クラスを 1〜2 個選ぶ（価値の高いロジックから）。
2. mutator から不足テストの種類を判断し、**仕様を表す例テスト**を追加（境界・分岐両側・具体値・副作用結果）。
3. `sh scripts/run-mutation-tests.sh` で当該 mutant が KILLED になったことを確認。
4. 全体スコアが上がったら `MUTATION_THRESHOLD` の既定値（floor）を引き上げる。
5. 低価値で除外した場合は `--excludedClasses` と本ルールに理由を記録。
6. 1 PR は小さく（触れたクラス＋テスト＋必要なら floor 更新）。app 挙動・`INTERNET` は変更しない。

## 等価ミューテーションの記録台帳

ルール4（記録のうえ除外し、スコアの責に帰さない）に基づく記録。**除外はしない**（将来の
API 変更で観測可能になりうるため計測は継続する）。再評価のトリガー: 該当クラスの公開 API が変わったとき、
**および証明が依存している定数が変わったとき**（例: ドリフト比0.45×深さ比0.55の相補性は「和=1.0」という
定数の組に依存しており、どちらかを変えた瞬間にドリフト判定は意味を持ち始め、証明が無効になる）。

| 記録日 | 対象 | 内容 | 等価と判断した根拠 |
|--------|------|------|------------------|
| 2026-06-12 | `DirectionalGesturePath` ドリフト行（`width*0.45`）の mult→div・boundary | ドリフト条件の変異 | **幾何学的証明**: 深さ比0.55とドリフト比0.45が相補（和=1.0）かつ `max(start,end) ≤ 外形幅` のため、「ドリフト違反かつ深さ充足」の入力が存在しない。ドリフト判定は結果を変えられない（変異も観測不能） |
| 2026-06-12 | `DirectionalGesturePath` 深さ行の sub→add（`min−apexX`→`min+apexX`） | 深さ計算の変異 | 同上の相補性より「真の深さが不足かつドリフト充足」が存在せず、変異が結果を変える入力がない |
| 2026-06-12 | `DirectionalGesturePath` 比率境界の `>=`→`>` / `<=`→`<` | 境界の変異 | float 演算上の厳密等値（`w*0.45f` 等）に一致する入力が実用座標で構成不能（0.45f の2進近似により整数座標では等値に到達しない） |
| 2026-06-12 | `DirectionalGesturePath` の `apexIsInside`・min/maxIndex 同値タイブレーク | 端点apex・同値極値の変異 | apex が端点の場合は脚スパンが0となり legs 判定が必ず偽（変異の効果が下流で吸収される）。同値極値の先頭/末尾選択は結果に影響しない |
| 2026-06-12 | `ViewerThemeStyle` の輝度演算 13件（bit AND→OR・shift・double演算・linearChannel 分岐） | WCAG 輝度計算の変異 | 公開 API は固定7パレットの `hasDarkBackground()`（背景輝度<文字輝度の順序比較）のみで、全パレットで順序が変異に対して頑健（既存7テーマのアサーション下で生存＝順序不変を実証）。輝度の絶対値を観測する公開 API が無い。コントラスト検査（ViewerThemeContrastTest）はテスト側で独自に輝度を計算しており本実装を経由しない |
| 2026-06-12 | `DirectionalGesturePath$SampledPoints` の補間演算 | 再標本化の変異 | 双子実装 `CustomGestureShape$SampledPoints` 側で `storedValue()` 経由の等間隔配置テストにより同型変異を殺している。本体側は trigger() の真偽値経由でしか観測できず精密にピンできない。**恒久対応は二重実装の統合**（code-quality-metrics.md の改善候補2） |
