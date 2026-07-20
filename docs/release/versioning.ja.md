# バージョン管理

LocalMD Reader のリリースバージョンは、リポジトリ直下の `VERSION` で管理します。

```text
VERSION_NAME=0.1.0
VERSION_CODE=13
```

`VERSION_NAME` はユーザー向けのバージョンです。SemVer 風に扱います。

- `patch`: バグ修正、文言修正、小さなUX改善
- `minor`: ユーザーに見える機能追加

`VERSION_CODE` は Google Play 用のバージョンコードです。APK または AAB を
アップロードするたびに必ず増やします。

Play に提出するリリースでは、`VERSION_CODE` だけを上げてはいけません。
ユーザーに見えるリリース単位として `VERSION_NAME` も必ず上げます。
`scripts/release-preflight.sh` は、最新の `vX.Y.Z` タグと同じ
`VERSION_NAME` のまま提出しようとすると失敗します。

## コマンド

現在のバージョンを表示:

```sh
scripts/version-show.sh
```

`VERSION` と `src/main/AndroidManifest.xml` の整合性を確認:

```sh
scripts/version-check.sh
```

patch バージョンを上げ、`VERSION_CODE` を1増やす:

```sh
scripts/version-bump.sh patch
```

minor バージョンを上げ、patch を0に戻し、`VERSION_CODE` を1増やす:

```sh
scripts/version-bump.sh minor
```

`scripts/version-code-bump.sh` は通常の Play 提出には使いません。署名や
Play Console 側の一時的な問題で、同じユーザー向けバージョンを緊急で
作り直す必要がある場合だけ、理由をコミットやリリース記録に残したうえで
`MDLITE_ALLOW_VERSION_CODE_ONLY=true` を付けて実行します。

ビルドスクリプトは `VERSION` を読み、コンパイル前に生成Manifestへ反映します。
`./test.sh` でも `scripts/version-check.sh` を実行します。
