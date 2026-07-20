# Gradle 移行ガード

このドキュメントは、script-built（Termux向けシェルスクリプト）から
Gradle-built への移行判断に必要な情報を提供する。
開発者がどのビルド経路を信頼すべきかを明確にし、
Termux 上の Gradle 失敗を正しく分類・対処できることを目的とする。

---

## 1. 現在の認可ビルド経路

**script-built が Play 配信の唯一の認可経路。**

| 経路 | 用途 | Play 配信 |
|------|------|-----------|
| `scripts/build-release-aab.sh`（script-built） | リリース AAB 生成 | **有効（唯一の認可経路）** |
| `./gradlew :app:bundleFreeRelease`（Gradle-built） | ビルド検証（CI `gradle-build` ジョブ） | **無効**（`play-release.yml` の `Reject Gradle Play upload` step で明示拒否済み） |

### なぜ script-built が authoritative か

Gradle-built AAB のメタデータが script-built AAB と一致することを  
`aapt2 dump` または `bundletool dump` で実証していないため。  
一致確認が完了するまで、script-built を唯一の信頼できるリリース経路として維持する。  
（根拠: `play-release.yml` L97-100、`claude-harness-engineering-backlog.md` の "Gradle Migration Guardrails" セクション）

---

## 2. AAB メタデータ比較

### 比較の現状

**未実施（環境未整備）。**

理由:
- ローカルに `aapt2` コマンドが存在しない（macOS 上）
- `bundletool` がシステム PATH に存在しない（Gradle キャッシュ内 jar のみ）
- script-built は `BUNDLETOOL_JAR` / `MDLITE_RELEASE_KEYSTORE` 等の環境変数が必要でローカル実行不可

### 比較手順（環境が整ったときに実施）

```bash
# ① Gradle-built AAB を CI artifact からダウンロード（認可経路と同じビルド環境）
gh run download <run_id> -n <artifact_name> --repo Yos-K/localmd-reader

# ② script-built AAB の取得方法（2択）
#   推奨: CI の play-release.yml (script 経路) が生成した artifact を使う
#   → ローカルでの環境差を排除できる
#   代替: Termux + キーストア環境で以下を実行（CI と同じ ANDROID_PLATFORM を必ず指定）
ANDROID_PLATFORM=android-35 \
  BUNDLETOOL_JAR=<path> MDLITE_RELEASE_KEYSTORE=<path> \
  MDLITE_RELEASE_KEY_ALIAS=<alias> sh scripts/build-release-aab.sh
# ※ ANDROID_PLATFORM を省略すると android-33 にフォールバックし、CI AAB との比較が無効になる

# ③ 比較: ApplicationId / versionCode / versionName / minSdk / targetSdk
SCRIPT_AAB=<script-built.aab>
GRADLE_AAB=<gradle-built.aab>

for BUNDLE in "$SCRIPT_AAB" "$GRADLE_AAB"; do
  echo "=== $BUNDLE ==="
  java -jar $BUNDLETOOL_JAR dump manifest --bundle="$BUNDLE" --xpath=/manifest/@package
  java -jar $BUNDLETOOL_JAR dump manifest --bundle="$BUNDLE" --xpath=/manifest/@android:versionCode
  java -jar $BUNDLETOOL_JAR dump manifest --bundle="$BUNDLE" --xpath=/manifest/@android:versionName
  java -jar $BUNDLETOOL_JAR dump manifest --bundle="$BUNDLE" --xpath=/manifest/uses-sdk/@android:minSdkVersion
  java -jar $BUNDLETOOL_JAR dump manifest --bundle="$BUNDLE" --xpath=/manifest/uses-sdk/@android:targetSdkVersion
done
```

比較が完了したら、差分（署名以外）をこのドキュメントの「比較結果」節に追記する。

---

## 3. Termux Gradle 失敗 分類診断 runbook

Termux で `./gradlew ...` が失敗しても、  
**`sh test.sh` が pass し CI `gradle-build` が pass していれば PR ブロッカーではない。**

失敗を以下の4カテゴリに分類して対処する。

### ① license 問題

**症状**: `SDK packages that require Android license agreement` / `Failed to install the following SDK packages`

**確認コマンド**:
```bash
$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses
```

**対処**:
- `yes | sdkmanager --licenses` を実行して全ライセンスに同意する
- Termux 上では SDK のインストールパスが macOS/Linux と異なることがある
- 同意後も失敗する場合は ② に進む

### ② SDK インストール問題

**症状**: `SDK location not found` / `platforms/android-35 not installed` / JDK バージョン不一致

**確認コマンド**:
```bash
echo $ANDROID_HOME
ls $ANDROID_HOME/platforms/
java -version
```

**対処**:
- `$ANDROID_HOME` を正しく設定する（Termux: `~/AndroidDev/sdk` 等）
- `sdkmanager "platforms;android-35" "build-tools;35.0.0"` で該当 SDK をインストールする
- JDK は 17 以上を使用する（CI: temurin 17）

### ③ aapt2 アーキテクチャ問題

**症状**: `aapt2: error while loading shared libraries` / `Exec format error` / `cannot execute binary file`

**背景**: Maven から取得される `aapt2` バイナリは x86_64 向けであり、  
ARM ベースの Termux 環境では実行できない（`claude-harness-engineering-backlog.md` L229-231 実観測）。

**確認コマンド**:
```bash
file $ANDROID_HOME/build-tools/35.0.0/aapt2
uname -m
```

**対処**:
- Termux が提供する `aapt2` バイナリを使用するよう、  
  **ローカルの** `~/.gradle/gradle.properties` に以下を追加する:
  ```properties
  android.aapt2FromMavenOverride=/data/data/com.termux/files/usr/bin/aapt2
  ```
- この設定はリポジトリに含めない（CI の GitHub Actions runners が壊れるため）

### ④ resource-linking / platform 問題

**症状**: aapt2 アーキを解決後も `android.jar` のロードで失敗する / `failed to load android.jar`

**背景**: Android 35 の `android.jar` をロードする resource-linking 段階で  
Termux のツールチェーンとの互換性問題が発生することがある（`claude-harness-engineering-backlog.md` L234-236 実観測）。

**確認コマンド**:
```bash
ls $ANDROID_HOME/platforms/android-35/android.jar
```

**対処**:
- `android.jar` が存在しない場合は再インストール: `sdkmanager "platforms;android-35"`
- それでも失敗する場合、これは Termux 固有の環境問題。  
  **`sh test.sh` が pass し CI が緑であればリリース・PR に影響なし。**
  CI の `gradle-build` が authoritative であると判断する。

---

## 4. Android SDK platform キャッシュ 導入可否判断

### 現状（実測値）

CI `gradle-build` ジョブの構成（`.github/workflows/ci.yml` L140-180 確認）:
- `gradle/actions/setup-gradle@v5`: Gradle ビルドキャッシュを保持
- `sdkmanager "platforms;android-35" "build-tools;35.0.0"`: **毎回ダウンロード**
- `.android-deps`: `actions/cache@v4` でキャッシュ済み

`gradle-build` の平均実行時間: 約116s（本日20件の実測、うち `platforms;android-35` DLを含む）  
`gradle-build` ジョブの timeout-minutes: 20分

### 判断: **見送り（現時点）**

理由:
- SDK platform（`platforms;android-35`、約70MB）のキャッシュ設定コスト vs CI 短縮効果を試算すると、  
  現状116sのジョブでキャッシュで20〜30s短縮できたとしても ROI は低い
- `gradle/actions/setup-gradle@v5` が Gradle build cache を保持しており、  
  コンパイル差分が主なボトルネックではない
- キャッシュキーの管理（platform バージョン変更時のキャッシュ破棄）が設定コストを増す

**再検討のトリガー**: gradle-build ジョブが 5分以上かかるようになった場合、  
または PR 頻度が 1日 40件を超えてコスト問題になった場合。

---

## 5. 次のアクション（移行判断のために必要なこと）

| アクション | 担当 | 状態 |
|-----------|------|------|
| script-built AAB の CI artifact 取得と保管手順を確立 | harness エンジニア | 未着手 |
| `bundletool dump` による両 AAB のメタデータ比較実施 | harness エンジニア | **未実施（環境未整備）** |
| 比較結果をこのドキュメントの「比較結果」節に追記 | harness エンジニア | 未着手 |
| 一致確認後、`play-release.yml` の Gradle upload 拒否を解除するかオーナーと判断 | オーナー（殿） | 待機中 |

**移行完了の判断基準（`claude-harness-engineering-backlog.md` Acceptance より）**:
- どのビルド経路が Play アップロードの認可経路かをチームが説明できる ✅（このドキュメント）
- Gradle 採用により CI 信頼性が向上し、Termux 開発が阻害されない ✅（CI 緑、Termux は `sh test.sh` で動作）
