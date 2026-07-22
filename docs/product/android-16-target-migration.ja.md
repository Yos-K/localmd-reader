# Android 16 ターゲット移行

## 方針

LocalMD Readerは、Google Playの期限である2026年8月31日より前にAndroid 16
（API 36）を対象とする。公開物の正はGradle CIであり、Play向けBundleをAPI 36で
コンパイルして生成する。ターゲットAPIのfitness checkにより、Gradle、Manifest、
CI、Playリリース設定の不一致を防ぐ。

Termuxの確認APKは明示的な例外とする。Termux版`aapt2`はAPI 35/36のplatform
resourceを読めないため、ローカルスクリプトはAPI 33でコンパイルするが、Manifestの
`targetSdkVersion=36`は維持する。このAPKは動作確認用であり、公開物にはしない。

## Android 16 挙動変更の確認

- Edge-to-edge: 対応済み。`MainActivity`が上下の`WindowInsets`を反映し、opt-outを宣言していない。
- Predictive back: 既定動作を使用する。`onBackPressed`のoverrideや`KEYCODE_BACK`の消費はない。
- Adaptive layouts: 画面方向、resize、aspect ratioを制限していない。API 36 device smokeで起動と主要操作を確認する。
- Fixed-rate scheduling: `scheduleAtFixedRate`を使用していないため対象外。
- Health、Bluetooth、MediaStore、local network: 対象となる権限やAPIを使用していない。
  readerは意図的に`INTERNET`権限を持たない。
- Safer Intents: Android 16ではopt-inである。既存のVIEW、SEND、SEND_MULTIPLE、PROCESS_TEXT経路はテストを維持する。

## 検証

- `scripts/test-target-api-policy.sh`
- `sh test.sh`
- RobolectricのAPI 36実行に必要なJava 21上でのGradle Free/Pro Preview unit test
- Gradle lint、debug APK、Free release AAB
- Android 16 device smoke

参照:

- [Google Playの対象API要件](https://support.google.com/googleplay/android-developer/answer/11926878)
- [target API 36に対するAndroid 16の挙動変更](https://developer.android.com/about/versions/16/behavior-changes-16)
- [Android 16 SDKの設定](https://developer.android.com/about/versions/16/setup-sdk)
