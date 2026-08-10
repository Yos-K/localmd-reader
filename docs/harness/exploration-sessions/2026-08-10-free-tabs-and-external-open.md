# 探索セッション: フリー版の外部起動とタブ復元 (2026-08-10)

## チャーター

| 項目 | 内容 |
|------|------|
| 対象 | Google Play版フリーアプリの複数文書受信、タブ切替、終了後復元 |
| 観点 | 外部から複数文書を開いた利用経路が、再起動とタブ終了を含めて完結するか |
| 環境 | SCG28 / `io.github.yosk.mdlite` 0.2.14 (35) / target API 36 |
| 接続 | 無線ADB。接続先や認証情報は記録しない |

## probe と観測

| # | probe | 期待 | 観測 | 判定 |
|---|---|---|---|---|
| P1 | `OPEN_TEXTS` Intentで2文書を一度に送る | 2文書が別タブで開き、最後の文書を表示する | `one.md`と`two.md`が開き、`Two`を表示した | 規則どおり |
| P2 | 先頭タブを選択する | 選択文書の本文へ切り替わる | `one.md`を選ぶと`One`を表示した | 規則どおり |
| P3 | 先頭タブ選択中にforce-stopして再起動する | 両タブと選択中の文書を復元する | 2タブと`One`を復元した | 規則どおり |
| P4 | 先頭タブを閉じ、force-stopして再起動する | 閉じたタブは復活せず、残った文書を復元する | `two.md`だけを復元し、非同期描画後に`Two`を表示した | 規則どおり |
| P5 | `scripts/mdlite-open.sh`から同じ2文書を送る | P1と同じ結果になる | `termux-am` 0.8.0が`--esa`を配送せず、Welcomeのままだった | 規則と異なる |
| P6 | 1文書1Intentで2文書を順次送る | 両方がタブになる | 1件目でTermuxが背面へ移り、Androidが2件目の起動を抑止した | 規則と異なる |
| P7 | 単一Base64バッチIntentで2文書を送る | 両方が指定順のタブになり、最後を表示する | Free debug実機でWelcome、`smoke-one.md`、`smoke-two.md`となり、最後の本文を表示した | 修正後に規則どおり |

## 振り分け

- P1からP4は外部複数受信とタブセッションの肯定的証拠として残す。
- P5は`am to-intent-uri`でも文字列配列extraが欠落することを確認した。P6で1文書1Intentも不成立と判明した。
  ADR-0025に基づき、全文書を1個のBase64バッチ文字列extraで送る経路へ変更した。
- `test-mdlite-open-termux-compatibility.sh`と`TermuxOpenIntentStructureTest`へ回帰条件を蒸留した。

## 次のチャーター候補

- Termuxの`am start`とADB shellの`am start`で、配送されたactionとextrasを比較する。
- スクリプトが起動成功だけでなく、対象タブが開いたことを検証可能な終了状態を持てるか検討する。
- 外部共有の`ACTION_SEND_MULTIPLE`でも同じタブ状態遷移になることを確認する。

## 価値評価

- probes: 7
- findings: 2
- triage-issue: 0
- triage-glossary: 0
- triage-assert: 6
- time-minutes: 20
