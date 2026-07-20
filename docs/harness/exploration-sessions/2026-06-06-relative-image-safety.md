# 探索セッション: 相対画像レンダリングの安全境界 (2026-06-06)

issue #100 の試行セッション#2（レビュー駆動）。運用ルールは [`../exploratory-testing.md`](../exploratory-testing.md) を参照。

## チャーター

| 項目 | 内容 |
|------|------|
| 対象 | レンダリングクラスタ（`domain-glossary-rendering.md` の R5 相対画像）＝ PR #99 の安全境界 |
| 観点 | 負の空間・敵対的入力（XSS スキーム・属性注入・パストラバーサル・エンコード回避） |
| 動機 | PR #99 が WebView インターセプトで**ローカルファイルを開く**新経路を追加する。reader WebView は file/content access を意図的に無効化しているため、この経路の安全境界が唯一の防御線になる |
| タイムボックス | probe 26本（レンダラ12 + リゾルバ14） |

実行環境: 純 JVM（`JavaSimpleMarkdownRenderer` と `LocalRelativeImageResource` を直接駆動。`BuildConfig` はスタブ）。

## probe と観測

### レンダラの画像URL安全判定（`isSafeImageUrl`）

| # | 入力 | 観測 | 判定 |
|---|------|------|------|
| R1 | `![x](javascript:alert(1))` | alt にフォールバック（`:` で弾く） | 規則どおり |
| R2 | `![x](file:///etc/passwd)` | alt フォールバック | 規則どおり |
| R3 | `![x](/etc/passwd.png)` ルート相対 | alt フォールバック | 規則どおり |
| R4 | `![x](//evil/x.png)` プロトコル相対 | alt フォールバック | 規則どおり |
| R5 | `![x](http://evil/x.png)` リモート | alt フォールバック | 規則どおり |
| R6 | `![x](data:image/svg...)` | alt フォールバック | 規則どおり |
| R7 | `![図1](images/fig1.png)` 正常 | `<img src="images/fig1.png" alt="図1">` | 規則どおり |
| R9 | `![x](a.png" onerror="alert(1))` 属性注入 | `"` が `&quot;` にエスケープされ属性ブレイクアウト不成立 | 規則どおり（防御成功） |
| R10 | `![">＜script＞](a.png)` alt注入 | alt が二重エスケープされ無害化 | 規則どおり |
| R11 | `![x](JAVASCRIPT:...)` 大文字 | `lower` で小文字化して弾く | 規則どおり |

### WebView 側のファイル解決（`LocalRelativeImageResource.resolve`、traversal 防御）

| # | 入力 | 観測 | 判定 |
|---|------|------|------|
| L1 | 正常 `img.png` | available（docset 内） | 規則どおり |
| L2 | `../secret.png` | **unavailable**（canonicalPath で docset 外を弾く） | 規則どおり |
| L3 | `%2e%2e%2fsecret.png` | unavailable（デコード後に `..` 検出） | 規則どおり |
| L4 | `%252e%252e%252f...` 二重エンコード | available だが path は `docset/%2e%2e%2fsecret.png`（**docset 内**に閉じる・実在しない） | 規則どおり（無害） |
| L6 | `sub/../../secret.png` | **unavailable** | 規則どおり |
| L7 | 別 host `evil.example` | unavailable（host 検証） | 規則どおり |
| L8 | `http://`（非 https） | unavailable | 規則どおり |
| L9 | `content://` 文書 | unavailable（scheme=file のみ） | 規則どおり |
| L12 | `//etc/passwd.png` | available だが path は `docset/etc/passwd.png`（**docset 内**） | 規則どおり（無害） |
| L13 | NUL 混入 `img%00.png` | unavailable | 規則どおり |
| L14 | クエリ `img.png?x=../../` | path は `img.png`（rawPath にクエリ含まず） | 規則どおり |

## 振り分けの結果

- **① issue**: なし（**安全境界は探索で破れなかった**。これは肯定的証拠＝PR #99 のセキュリティ設計は堅牢）。
  二段防御（レンダラがスキームを弾く + リゾルバが canonicalPath で docset 外を弾く）が独立に機能。
- **② glossary 追記（実施済み）**: `..` を含む相対パスは、レンダラは img 化するが**リゾルバが docset 外として弾く**。
  つまり `![](../images/x.png)`（親階層の画像参照、一般的な docset レイアウト）は**表示されない**。R5 に明文化が無い＝**沈黙領域**だった。
  オーナー確認の結果「**親階層も許可したい**」（traversal を完全禁止する現状は意図ではなく、安全な範囲で親階層を許可する方針）。
  → R5 の L3 に**現状の制約と #106（親階層サポートの設計）**を追記。バグ/意図の二分でなく「制約は事実、拡張は別 issue」で記録。
- **③ テスト**: PR #99 に既存のレンダラ・リゾルバのユニットテストあり。本探索のケース（二重エンコード L4・属性注入 R9）は
  カバー外のため、確認が取れれば回帰テストとして追加候補。

## 次のチャーター候補

- viewer「開く判定」: #97 の暗黙規則（OPEN_TEXTS 事後条件）はまだ用語集に無い
- レンダリング相互作用: 相対リンク R4 と相対画像 R5 が同じ `isSafeRelativeLinkUrl` を共有する設計の妥当性

## 価値評価（機械可読・集計は scripts/exploration-status.sh）

数値はすべて本文の probe 表・振り分け節から導出（2026-06-11 に様式追加時へ遡及記入）。

- probes: 21
- findings: 1
- triage-issue: 0
- triage-glossary: 1
- triage-assert: 0
- time-minutes: 未計測（様式導入前のセッション）

findings の内訳: 「規則が沈黙」= 親階層相対パスの拒否（R5 の沈黙領域、#106 起票につながった）。
残り20 probeは「規則どおり」＝安全境界の堅牢性を示す肯定的証拠（発見ゼロでも価値がある実例）。
triage-assert は「候補2件（L4 二重エンコード・R9 属性注入）が未実施」のため 0。
