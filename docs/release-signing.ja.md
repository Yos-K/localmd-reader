# リリース署名

この文書は MdLite Reader の本番署名方針を定義します。

## 方針

- 本番署名鍵はリポジトリにコミットしません。
- 本番署名鍵はプロジェクトディレクトリ外に置きます。
- 署名パスワードは tracked file、シェル履歴、ログ、ドキュメントに残しません。
- リリース署名前に `./test.sh` を成功させます。
- リリース APK 成果物は `scripts/check-release-basics.sh` を通します。
- リリース AAB 成果物は `bundletool validate` を通します。
- 初回公開リリース前にリリース鍵をバックアップします。

## リリース keystore の作成

keystore はリポジトリ外に作成します。

```sh
scripts/create-release-keystore.sh
```

デフォルトの作成先:

```text
~/AndroidDev/keys/mdlite-reader-release.jks
```

デフォルトの key alias:

```text
mdlite-release
```

別のパスや alias を使う場合:

```sh
export MDLITE_RELEASE_KEYSTORE="$HOME/AndroidDev/keys/mdlite-reader-release.jks"
export MDLITE_RELEASE_KEY_ALIAS="mdlite-release"
scripts/create-release-keystore.sh
```

## 署名済みリリース APK の作成

先に自動チェックを実行します。

```sh
./test.sh
```

その後、本番鍵で署名します。

```sh
export MDLITE_RELEASE_KEYSTORE="$HOME/AndroidDev/keys/mdlite-reader-release.jks"
export MDLITE_RELEASE_KEY_ALIAS="mdlite-release"
printf "Keystore password: "
stty -echo
read -r MDLITE_RELEASE_STORE_PASS
stty echo
printf "\nKey password: "
stty -echo
read -r MDLITE_RELEASE_KEY_PASS
stty echo
printf "\n"
export MDLITE_RELEASE_STORE_PASS
export MDLITE_RELEASE_KEY_PASS
scripts/build-release-apk.sh
unset MDLITE_RELEASE_STORE_PASS
unset MDLITE_RELEASE_KEY_PASS
```

デフォルトの出力先:

```text
build/release/mdlite-reader-0.1.0-release.apk
```

## 署名済みリリース AAB の作成

Google Play 向けのリリースビルドでは Android App Bundle を使います。

MdLite Reader では `bundletool` をリポジトリ外に置きます。ローカルの tools
ディレクトリに配置し、`BUNDLETOOL_JAR` でそのファイルを指定します。

先に自動チェックを実行します。

```sh
./test.sh
```

その後、署名済み AAB を作成します。

```sh
export BUNDLETOOL_JAR="$HOME/AndroidDev/tools/bundletool.jar"
export MDLITE_RELEASE_KEYSTORE="$HOME/AndroidDev/keys/mdlite-reader-release.jks"
export MDLITE_RELEASE_KEY_ALIAS="mdlite-release"
scripts/build-release-aab.sh
```

必要な場合、`jarsigner` が keystore password と key password を入力要求します。
これらのパスワードをコマンド引数として渡さないでください。

非対話で実行する場合は、環境変数経由でパスワードを渡します。

```sh
export BUNDLETOOL_JAR="$HOME/AndroidDev/tools/bundletool.jar"
export MDLITE_RELEASE_KEYSTORE="$HOME/AndroidDev/keys/mdlite-reader-release.jks"
export MDLITE_RELEASE_KEY_ALIAS="mdlite-release"
printf "Keystore password: "
stty -echo
read -r MDLITE_RELEASE_STORE_PASS
stty echo
printf "\nKey password: "
stty -echo
read -r MDLITE_RELEASE_KEY_PASS
stty echo
printf "\n"
export MDLITE_RELEASE_STORE_PASS
export MDLITE_RELEASE_KEY_PASS
scripts/build-release-aab.sh
unset MDLITE_RELEASE_STORE_PASS
unset MDLITE_RELEASE_KEY_PASS
```

`jarsigner` が自己署名証明書やタイムスタンプなしの警告を出すことがあります。
Android のリリース署名では許容し、bundle 構造の確認は `bundletool validate` の
成功で判断します。

デフォルトの出力先:

```text
build/release/mdlite-reader-0.1.0-release.aab
```

リリース staging 先:

```text
~/AndroidDev/releases/mdlite-reader/v0.1.0/mdlite-reader-0.1.0-release.aab
```

checksum ファイル:

```text
~/AndroidDev/releases/mdlite-reader/v0.1.0/SHA256SUMS
```

## Play Store について

APK スクリプトはローカルでのリリース相当のインストール確認に使います。
Play Console が Android App Bundle を要求する場合は、AAB スクリプトで生成した
成果物をアップロードします。
