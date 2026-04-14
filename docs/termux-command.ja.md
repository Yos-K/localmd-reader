# Termux コマンド

MdLite Reader は Termux から 1 つ以上の Markdown ファイルを指定して開けます。

リポジトリ内から実行するコマンド:

```sh
scripts/mdlite-open.sh FILE.md [FILE2.md ...]
```

例:

```sh
scripts/mdlite-open.sh README.md docs/termux-command.md
```

読み取り可能な Markdown ファイルは、それぞれタブとして開きます。すでに開いている
ファイルは重複せず、そのタブを更新してアクティブにします。

このコマンドは `termux-open` を使います。そのため初回は Android が開くアプリを
確認する場合があります。MdLite Reader を選択してください。MdLite Reader が
Markdown の既定ビューアになっていれば、複数ファイルは順にタブとして開きます。

## グローバルコマンド

普段使う場合は、ラッパーを `~/bin` に配置します。

```sh
mkdir -p ~/bin
cp scripts/mdlite-open.sh ~/bin/mdlite-reader
chmod +x ~/bin/mdlite-reader
```

その後は次のように実行できます。

```sh
mdlite-reader FILE.md [FILE2.md ...]
```

`~/bin` が `PATH` に含まれている必要があります。

## アクセス方式

このコマンドは Android アプリに生のファイルパスを渡さず、Termux のファイル共有
ブリッジを使います。これにより、広いストレージ権限を要求せずに Termux 配下の
ファイルを開けます。

Android が MdLite Reader を候補に出さないファイルは、アプリ内のファイル選択
または Android のファイルマネージャから開いてください。システムが document URI
の読み取り権限を付与します。
