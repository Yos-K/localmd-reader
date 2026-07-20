# コード品質メトリクス測定ハーネス

## このドキュメントの目的

AIエージェントによる改修コストを下げるため、コードの複雑度と結合度を**数値で測定し、
モデル改善（リファクタリング）の優先順位を決められる**ようにする。読者はここを見れば、
各指標が何を測り、なぜAI改修コストに効き、どの値から行動すべきかを把握できる。

測定コマンド（いずれも advisory・ゲートではない・SDK不要）:

```sh
sh scripts/measure-complexity.sh   # 循環的複雑度・認知的複雑度・偶有的複雑性の代理・重複
sh scripts/measure-coupling.sh    # Ca/Ce/不安定度・SDP違反・Balanced Coupling・共変更
```

## なぜ測るのか（AI改修コストとの対応）

| 指標 | AI改修コストへの効き方 |
|------|----------------------|
| 循環的複雑度（CC） | 分岐数 = テストすべき経路数・推論の組合せ数。高いほど変更の安全確認が高価 |
| 認知的複雑度 | ネスト・フロー断絶 = コンテキスト理解の負荷。高いほど「読んで正しく直す」コストが増える |
| 偶有的複雑性（代理） | ドメインの本質に寄与しない複雑さ。**挙動を変えずに削減できる**＝純粋なコスト |
| 結合度（Balanced Coupling） | 変更の爆発半径。「強く依存している先がよく変わる」ほど1変更あたりの波及作業が増える |

## 指標の定義

### 循環的複雑度・認知的複雑度（PMD で直接測定）

PMD の `CyclomaticComplexity` / `CognitiveComplexity`（SonarSource 定義）ルールを
reportLevel=1 で全メソッドに適用し、分布を得る（`scripts/quality/complexity-ruleset.xml`）。

| バンド | CC（fitness S-01 準拠） | 認知的複雑度（Sonar 既定15起点） |
|--------|------------------------|--------------------------------|
| GREEN | ≤10 | ≤15 |
| YELLOW | 11–20 | 16–25 |
| RED | >20 | >25 |

### 偶有的複雑性（直接測定不能のため代理指標で近似）

偶有的複雑性（Brooks: 本質でなく解決手段に由来する複雑さ）は機械測定できない。
**だから**、次の2つの代理で近似し、限界を明記して使う:

1. **認知的複雑度 − 循環的複雑度の差分（メソッド単位）**
   - なぜ代理になるか: ドメイン本来の分岐は両指標に同程度現れるが、深いネスト・
     フロー断絶・構造ノイズは認知的複雑度だけを押し上げる。差分が大きいメソッドは
     **Extract Method 等の挙動を変えないリファクタで削減できる見込みが高い**＝偶有的。
   - 限界: 本質的に複雑なアルゴリズム（状態機械等）でも差分が出ることがある。
     差分は「削減候補の順位付け」であり「偶有の証明」ではない。
2. **重複（PMD CPD, 100トークン以上）**
   - 重複はドメインが要求しない純粋な偶有（多点修正コスト＝AIのshotgun surgery）。

### 結合度（vladikk Balanced Coupling の縮約実装）

vladikk/modularity の「結合の負荷 = 強度 × 距離 × 変動性」を、外部ツールなしで
測定できる形に縮約する（git + awk のみ。コンパイル不要）:

| 成分 | 本実装での測定方法 |
|------|------------------|
| 強度 | パッケージ間の import 数（`edges.tsv`） |
| 距離 | パッケージ境界の内外（本リポジトリは5パッケージのフラット構造のため、別パッケージ＝距離あり、として2値で扱う） |
| 変動性 | 直近90日の git 変更回数（被依存側） |

出力は4種:
- **Ca/Ce/不安定度 I**（fitness D-01〜D-03）。注意: レイヤードアーキテクチャでは
  domain の I≈0・presentation の I≈1 は**健全**（D-03 の「両端=RED」バンドは
  中間層にのみ適用する）
- **SDP違反**: 自分より不安定なパッケージへの依存（安定依存の原則違反）。これが実害シグナル
- **Balanced Coupling 上位辺**: 強度×変動性の積。改修波及コストの期待値順
- **共変更ペア**: import に現れないのに同一コミットで頻繁に共変更されるファイル対
  （隠れた時間的結合。閾値5回/90日）

## ベースライン実測（2026-06-12・main）

### 複雑度（対象1,461メソッド）

- CC: GREEN 1,453 / YELLOW 6 / **RED 2** ／ 認知的: GREEN 1,456 / YELLOW 4 / **RED 1**
- 最大ホットスポット: `JavaSimpleMarkdownRenderer.render` — **認知的64・CC33・差分31**
  （全社最大。mutation の SURVIVED 集中クラスとも一致し、複数シグナルが同一地点を指す）
- 偶有性代理の上位: 同 `render`（差分31）、`renderInline`（差分9）、
  `MermaidDiagramBlocks.fromMarkdown`（差分7）
- 重複: 10ブロック・237行。最大は **`CustomGestureShape` ⇔ `DirectionalGesturePath` の
  SampledPoints/Bounds 二重実装**（探索セッション 2026-06-11 の観測とも一致）

### 結合度（90日窓）

- I 分布: domain 0.00 / file 0.00 / viewer 0.33 / infrastructure 0.67 / presentation 1.00
  — レイヤード設計として教科書的に健全。**SDP違反ゼロ**
- Balanced Coupling 最大辺: presentation→viewer（負荷1156 = 強度17×変動68）
- 共変更ペア: **6ペア中5ペアに MainActivity が登場**（HtmlPageBuilder 19回・
  ViewerText 14回・JavaSimpleMarkdownRenderer 8回 ほか）— 時間的結合のハブ

### ベースラインから導く改善優先順位（なぜ→だから）

1. `JavaSimpleMarkdownRenderer.render`: 認知64は全指標で突出し、差分31は抽出での削減
   見込みが大きい。**だから** mutation 残ホットスポット対応と合わせ、段階的 Extract Method
   の第一候補とする
2. SampledPoints/Bounds の二重実装: 重複237行の主因で、ジェスチャ変更のたびに2点修正
   が必要（#149 でも実際に2クラスへ同型変更を行った）。**だから** viewer 内の共有クラスへの
   統合候補とする（公開APIは不変・テストは既存が両側を固定済み）
3. MainActivity の時間的結合: 共変更ハブであることが定量化された。**だから** 既存の
   段階的分割方針（300行ルール例外の解消・#71系）の根拠データとして使い、分割の効果は
   本ハーネスの共変更ペア数の減少で検証する

## 運用

- **トリガー**: ①大きめのリファクタ・機能追加の前後（効果測定）②ROI評価サイクル時
  ③ boy-scout 判断時（触ったファイルのメソッドが YELLOW/RED なら一歩改善を検討）
- **ゲート化はしない（当面）**: メタハーネス原則「発火実績・コスト実測なしにゲートを
  常設しない」に従い、advisory として2〜3サイクル運用→傾向データが取れたら
  ratchet 化（RED件数の床）を判断する。`--strict`（measure-complexity）が将来の
  ゲート化の受け口
- **モデル改善の検証**: リファクタPRの前後で同コマンドを実行し、対象メソッドの
  認知的複雑度・差分・共変更ペア数の変化をPR本文に記録する

## 運用実績

### サイクル1（2026-06-13・SampledPoints/Bounds 統合）

ベースライン（2026-06-12）の改善優先順位2「SampledPoints/Bounds の二重実装」を実施し、本ハーネスで前後計測した（測定ハーネスの初回運用サイクル）。

- **なぜこの対象か**: 重複237行の最大ペア（`CustomGestureShape` ⇔ `DirectionalGesturePath`）で、ジェスチャ変更のたび2クラスへ同型修正が必要だった（#149 で実際に発生）。mutation の等価ミュータント（DirectionalGesturePath SURV36 は観測可能性起因＝重複に起因）の**根本対応**にもなる。
- **何をしたか**: 共有 package-private `GestureBounds` / `GestureSampledPoints`（viewer）へ抽出。公開APIは不変。退化（移動距離0）時は lenient コピーに統一（CustomGestureShape 側の throw は `MIN_SIZE_DP` ガードに先行されるため到達不能な dead code だった）。
- **前後計測（measure-complexity / CPD）**:

  | 指標 | Before | After |
  |------|--------|-------|
  | 重複ブロック | 10 | **8** |
  | 重複行（1出現あたり） | 237 | **157**（−80） |
  | CGS/DGP の重複出現 | あり（最大ペア） | **消失** |
  | CustomGestureShape 行数 | 198 | **108** |
  | DirectionalGesturePath 行数 | 289 | **187** |

- **回帰検証**: JVM ユニットテスト 596 件すべて green（gesture 系含む）。公開API・挙動不変。
- **保留**: 改善優先順位1「`JavaSimpleMarkdownRenderer.render`（認知64）の分解」は**レンダリング意味論に触れる**ため、専用サイクルでオーナー承認のうえ実施する（本サイクルでは計測のみ）。

## 限界（正直な注記）

- 偶有的複雑性の代理指標は順位付けの道具であり、絶対値に意味はない
- 結合の「距離」は2値に縮約しており、vladikk のモジュール深度の概念は未実装
  （パッケージ階層が深くなったら距離関数を導入する）
- import ベースの強度はリフレクション・文字列結合の依存を見ない（本リポジトリには無い）
- 共変更は「同一コミット」基準のため、コミット粒度の影響を受ける

## 関連

- 閾値の出典: `~/.claude/rules/fitness-metrics.md`（S-01/D-01〜D-03）・SonarSource Cognitive Complexity
- 結合度の概念: [vladikk/modularity](https://github.com/vladikk/modularity)（Balanced Coupling）
- ハーネス運用原則: [harness-roi-framework.md](./harness-roi-framework.md)
