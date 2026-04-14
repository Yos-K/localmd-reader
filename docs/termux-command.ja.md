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

このコマンドは MdLite Reader を直接起動します。複数ファイルは順にタブとして
開きます。

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

このコマンドは Termux 側で Markdown 本文を読み取り、MdLite Reader に直接渡します。
これにより、広いストレージ権限を要求せずに Termux 配下のファイルを開けます。

Android の Intent extra には実用上のサイズ制限があります。アプリの 2 MB 制限に
近い大きなファイルは、アプリ内のファイル選択または Android のファイルマネージャ
から開いてください。
