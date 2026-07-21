# Architecture Decision Records

ADRは、複数の妥当な実装案があり、コードやPR履歴だけから再判断すると損失が大きい決定を残す。

## ルール

- 英語ADRを正典とし、各ADRに `.ja.md` を用意する。
- ADRは決定の記録である。決定を変える場合は履歴を黙って書き換えず、状態変更または後継ADRを追加する。
- すべての `feat:` / `fix:` コミットに `ADR-Review:` トレーラを1つ付ける。
- ユーザーフィードバックにより構造を変える場合、実装を受け入れる前にADRを新規作成、更新、または置換する。

必須節は、決定事項、検討した選択肢、選択理由、選択しなかった理由、決定を見直す契機とする。

## 一覧

| ADR | 決定 | 状態 |
|---|---|---|
| [0001](0001-offline-security-boundary.ja.md) | オフライン・セキュリティ境界 | 採用 |
| [0002](0002-free-pro-product-boundary.ja.md) | Free/Proの製品境界 | 採用 |
| [0003](0003-markdown-library-navigation.ja.md) | メニュー内の継続的なMarkdownライブラリ | 採用 |
| [0004](0004-android-storage-access-framework.ja.md) | Android Storage Access Framework | 採用 |
| [0005](0005-isolated-mermaid-rendering.ja.md) | 分離したローカルMermaid描画 | 採用 |
| [0006](0006-gradle-ci-build-authority.ja.md) | Gradle CIをビルドの正とする | 採用 |
| [0007](0007-blocking-adr-review-gate.ja.md) | ADR確認の必須ゲート | 採用 |
| [0008](0008-interaction-command-traceability.ja.md) | 操作モデル・コード・テストの追跡 | 採用 |
| [0009](0009-interaction-state-above-views.ja.md) | Android Viewより上位での操作状態管理 | 採用 |
| [0010](0010-document-list-dialog-session.ja.md) | 文書一覧ダイアログのセッション管理 | 採用 |
| [0011](0011-inline-menu-disclosure-state.ja.md) | メニュー内折り畳み状態の管理 | 採用 |
| [0012](0012-derived-heading-navigation.ja.md) | 番兵状態を使わない見出し移動 | 採用 |
| [0013](0013-document-tab-session-completion.ja.md) | 文書タブのセッション遷移完了 | 採用 |
| [0014](0014-public-source-private-release-repositories.ja.md) | 公開ソースと非公開リリースの分離 | 採用 |
| [0015](0015-document-rendering-coordinator.ja.md) | プラットフォーム非依存の描画セッション所有 | 採用 |
| [0016](0016-open-document-tab-session-owner.ja.md) | プラットフォーム非依存の開いている文書タブ所有 | 採用 |
