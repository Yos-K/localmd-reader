# ADR-0014: 公開ソースと非公開リリースのリポジトリを分離する

状態: 採用

## 決定事項

`Yos-K/localmd-reader`を公開ソースリポジトリ、`Yos-K/localmd-reader-release`を
非公開リリースリポジトリとする。公開側のCIは署名情報やPlay認証情報を使用しない。署名済みbuildと
Playへのuploadは、GitHub Environment secretsとGoogle Cloud Workload Identityの信頼を保持する
非公開リポジトリだけで実行する。

ローカルcheckoutでは公開remoteを`origin`、非公開remoteを`release`と呼ぶ。リリース操作は
非公開リポジトリを明示的に対象としなければならない。

## 検討した選択肢

- 既存リポジトリを全履歴branch・PRとともに公開する。
- すべての履歴refを書き換えて既存リポジトリへforce pushする。
- 新しい公開リポジトリへrelease secretsをコピーする。
- 検査済みソースだけを公開し、既存リポジトリを非公開リリース境界として保持する。

## 選択理由

過去の開発refには公開すべきでないauthor emailがあり、リリースリポジトリにはAPIから再取得できない
GitHub secretsがある。境界を分離すると、過去refを公開せず、署名情報とPlay認証情報を公開workflowへ
渡さずに、既存のリリース設定を維持できる。

## 選択しなかった理由

旧リポジトリの公開は不要な開発履歴を露出する。GitHub上のすべてのPR refを確実に書き換えることはできない。
GitHub APIからsecret値を読み戻すことはできず、GitHubが暗号化していてもrelease credentialを公開側の
automation境界へ置くべきではない。

## 決定を見直す契機

release署名を専用の外部serviceへ移した場合、公開リポジトリで別途reviewされたtrusted publishingを
利用できる場合、または二つのリポジトリを同期する費用が分離の利益を上回った場合に見直す。
