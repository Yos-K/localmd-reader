# Play リリースノート ハードコード解消 設計提案書

このドキュメントは `scripts/play-upload-closed-test.py` の `release_notes()` 関数に
ハードコードされた Play Console 向けリリースノートを外部ファイルに切り出すための
設計提案書（草稿）です。実装は殿の裁可後に別 cmd で行います。

---

## 1. 背景と課題

### 現状のハードコード

`scripts/play-upload-closed-test.py` lines 28–47 に以下が存在する（根拠: 同ファイル実測）:

```python
def release_notes():
    return [
        {"language": "en-US", "text": "Initial closed test release of LocalMD Reader v0.1.0. ..."},
        {"language": "ja-JP", "text": "LocalMD Reader v0.1.0 の初回クローズドテストリリースです。..."},
    ]
```

### なぜ問題か

| 問題点 | 影響 |
|--------|------|
| v0.1.0 が文字列として埋め込まれている | バージョンアップのたびに Python ソースを直接編集しなければならない |
| 本文が Python 識別子の外にある | レビューしにくい・誤字がコードレビューでしか検出されない |
| 二重管理 | `docs/release/release-notes-v<VER>.md` および `.ja.md`（長文・人間向け。`check-release-notes.sh` L21-22 で両方を確認）と別に Play 用短文を管理している |
| stale 検知だけで根本解消されていない | `check-release-notes.sh` は `v<VERSION>` が関数本体にあるか確認するだけ（issue #33） |

---

## 2. 提案：locale 別ファイル方式

### 2-1. ディレクトリ構成

```
play-store/
  release-notes/
    en-US/
      whatsnew-v0.1.0.txt   ← 既存相当（移行時に作成）
      whatsnew-v0.2.0.txt   ← 次バージョン時に追加
    ja-JP/
      whatsnew-v0.1.0.txt
      whatsnew-v0.2.0.txt
```

- `play-store/` は既存ディレクトリ（根拠: `ls play-store/` 実測）
- ファイル名にバージョン番号を含める → 過去バージョンの文言が残り、差分が追いやすい
- locale コードは Play Console で使用される BCP 47 形式（現行コード `"language": "en-US"` に合わせる）

### 2-2. `play-upload-closed-test.py` の変更点

**変更箇所: `release_notes()` 関数**（現状: 28-47行、変更後: 同位置）

```python
# 変更前（ハードコード）
def release_notes():
    return [
        {"language": "en-US", "text": "... v0.1.0 ..."},
        {"language": "ja-JP", "text": "... v0.1.0 ..."},
    ]

# 変更後（ファイル読み込み）
def release_notes(version_name: str, repo_root: pathlib.Path) -> list:
    notes_dir = repo_root / "play-store" / "release-notes"
    result = []
    for locale_dir in sorted(notes_dir.iterdir()):
        if not locale_dir.is_dir():
            continue
        note_file = locale_dir / f"whatsnew-v{version_name}.txt"
        if not note_file.is_file():
            raise SystemExit(
                f"Missing Play release notes: {note_file}\n"
                f"Create it under play-store/release-notes/{locale_dir.name}/"
            )
        text = note_file.read_text(encoding="utf-8").strip()
        _validate_char_limit(text, note_file)
        result.append({"language": locale_dir.name, "text": text})
    return result
```

- `version_name` は `VERSION` ファイルから読み取る（`version-env.sh` と同じソース）
- `repo_root` は `pathlib.Path(__file__).parent.parent` で解決

### 2-3. 500 字制限の検証方法

Play Console の制限は **文字数** 500（バイト数でない）。`wc -c` では多バイト文字を誤計上するため、
Python の `len(text)` で検証する。

```python
def _validate_char_limit(text: str, path: pathlib.Path, limit: int = 500) -> None:
    if len(text) > limit:
        raise SystemExit(
            f"{path}: {len(text)} chars exceeds Play Console limit of {limit}"
        )
```

### 2-4. `check-release-notes.sh` との整合

現行の stale 検知（`release_notes()` 関数本体に `v<VERSION>` があるか）は不要になる。
代わりに以下の 2 チェックに置き換える:

| 現行チェック | 変更後 |
|-------------|--------|
| `docs/release/release-notes-v<VER>.md` および `.ja.md` 存在確認（`check-release-notes.sh` L21-22 で両言語版を確認） | 変更なし（そのまま継続） |
| `release_notes()` 関数本体に `v<VER>` があるか | `play-store/release-notes/<locale>/whatsnew-v<VER>.txt` の存在確認に変更 |
| (なし) | 各 .txt の文字数 ≤ 500 を `awk '{...}'` or `python3 -c` で検証 |

```sh
# check-release-notes.sh 変更イメージ（既存チェック3を置き換え）
for locale in en-US ja-JP; do
  note_file="$ROOT/play-store/release-notes/$locale/whatsnew-v$VERSION_NAME.txt"
  if [ ! -f "$note_file" ]; then
    fail "missing $note_file"
  fi
  # python3 が使える環境ではマルチバイト文字も正確にカウントできる
  char_count=$(python3 -c "import sys; print(len(open(sys.argv[1]).read().strip()))" "$note_file")
  # python3 非依存の代替: awk で文字数を合計（ASCII 環境向け、Termux 互換）
  # char_count=$(awk '{n+=length($0)} END{print n}' "$note_file")
  if [ "$char_count" -gt 500 ]; then
    fail "$note_file: $char_count chars exceeds 500 char limit"
  fi
done
```

---

## 3. 代替案比較

### 案 ① locale 別ファイル方式（本提案）

| 観点 | 評価 |
|------|------|
| バージョンアップ時の作業 | `.txt` を追加するだけ。Python 編集不要 |
| 500 字制限の検知 | スクリプト / Python で自動検証できる |
| メンテナンス性 | 文言変更が diff に素直に出る |
| 移行コスト | `play-store/release-notes/` 作成 + 既存本文の移行 + Python 修正 + sh 修正 |
| リスク | ファイル名ルールの認知不足による「ファイル追加忘れ」（check-release-notes.sh でカバー可） |

### 案 ② 現行の handover 記載方式（変更なし）

| 観点 | 評価 |
|------|------|
| バージョンアップ時の作業 | Python ファイルを直接編集。バージョン文字列の書き換え忘れリスク |
| 500 字制限の検知 | 手動確認のみ（check-release-notes.sh は文字数未検証） |
| メンテナンス性 | 本文がコード内に埋め込まれており見えにくい |
| 移行コスト | ゼロ（現状維持） |
| リスク | 毎リリース Python 直接編集が必要。ヒューマンエラー率が高い |

**推奨: 案 ①**。移行コストは小さく、案 ② の手動編集ミスリスクは毎リリース発生する。

---

## 4. 移行手順の概要（実装は裁可後）

1. `play-store/release-notes/en-US/whatsnew-v0.1.0.txt` を作成（現行英語本文をそのまま移植）
2. `play-store/release-notes/ja-JP/whatsnew-v0.1.0.txt` を作成（現行日本語本文を移植）
3. `scripts/play-upload-closed-test.py` の `release_notes()` をファイル読み込み方式に書き換え
4. `scripts/check-release-notes.sh` のチェック 3 をファイル存在 + 文字数確認に置き換え
5. `scripts/pr-preflight.sh` / fitness テスト影響がないか確認（check-hard-constraints.sh 対象外）
6. PR 作成 → CI 確認 → マージ

---

## 5. 殿への確認事項

| No. | 確認内容 | 影響度 | 判断に必要な材料 |
|-----|----------|--------|----------------|
| Q1 | `whatsnew-v<ver>.txt` のバージョン付きファイル名か、バージョンなし `whatsnew.txt`（上書き）か | 設計 | 履歴保持の要否 |
| Q2 | 対応ロケールを en-US / ja-JP に限定してよいか（他言語追加の予定はあるか） | スコープ | 今後のリリース計画 |
| Q3 | `check-release-notes.sh` の文字数チェックに `python3` を使ってよいか（Termux 環境に依存） | 実装 | Termux python3 の利用可否 |
| Q4 | 本提案の方向性で実装を進めてよいか（裁可） | 着手可否 | 本提案書 |

---

## 殿の裁可決定事項（2026-06-07）

| 問 | 決定 |
|---|---|
| Q1 ファイル名 | `whatsnew.txt` 固定（バージョン付与なし） |
| Q2 対応ロケール | en-US / ja-JP の 2 言語のみ（追加予定なし） |
| Q3 文字数チェック | sh+sed+grep 構成に統一（python3 非依存・Termux 互換） |
| Q4 実装方式 | 提案①（locale 別ファイル方式）で実装 go |
