# デバッグAPKのインストール

Google Play からインストールしたアプリは、Google Play のアプリ署名証明書で署名
されています。ローカルの debug APK はリポジトリ内の debug keystore で署名されます。
Android は、同じパッケージ名で署名が異なる APK による更新を許可しません。

この衝突を避けるため、`build.sh` はローカル debug APK だけ開発用パッケージ名で
ビルドします。

```text
io.github.yosk.mdlite.debug
```

debug アプリの表示名:

```text
LocalMD Reader Dev
```

これにより、Play版とローカル debug 版を同じ端末に共存できます。

## ビルド

```sh
./build.sh
```

出力:

```text
app-debug.apk
```

## Free版をローカルで確認する

Free版としてローカルにインストールするAPKは、次のスクリプトで作成します。

```sh
scripts/build-free-debug-apk.sh
```

出力:

```text
/sdcard/Download/localmd-reader-free-debug.apk
```

パッケージ名:

```text
io.github.yosk.mdlite.free.debug
```

表示名:

```text
LocalMD Reader Free Dev
```

このAPKではPro機能は無効です。Google Play版やPro確認用APKと同じ端末に共存できます。

## Pro版をローカルで確認する

Pro機能を有効にした確認用APKは、次のスクリプトで作成します。

```sh
scripts/build-pro-debug-apk.sh
```

出力:

```text
/sdcard/Download/localmd-reader-pro-debug.apk
```

パッケージ名:

```text
io.github.yosk.mdlite.pro.debug
```

表示名:

```text
LocalMD Reader Pro Dev
```

## 任意の上書き

ローカル実験用に debug パッケージ名と表示名を変更できます。

```sh
MDLITE_DEBUG_PACKAGE=io.github.yosk.mdlite.local \
MDLITE_DEBUG_APP_NAME="LocalMD Reader Local" \
./build.sh
```

これらの debug パッケージ名は Google Play リリースには使いません。

リリース用スクリプトは引き続き本番パッケージ名を使います。

```text
io.github.yosk.mdlite
```
