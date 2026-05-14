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
