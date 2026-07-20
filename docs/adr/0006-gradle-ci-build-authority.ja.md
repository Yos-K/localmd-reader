# ADR-0006: Gradle CIをAndroidビルドの正とする

状態: 採用

## 決定事項

GitHub Actions上のGradle Wrapperビルドを、Android variant、lint、テスト、Play成果物の正とする。
Termuxのシェルビルドは高速なローカル補助経路として残すが、その成功をGradle CIゲートの代わりにしない。

## 検討した選択肢

- Termux独自のD8/AAPT経路をビルドの正とする。
- Android Studioの手動ビルドに依存する。
- ローカル経路を持たずCIだけでビルドする。
- 再現可能なGradle CIと軽量なTermuxフィードバックを併用する。

## 選択理由

GradleはAndroidが支援する依存・variantモデルと一致し、再現可能なリリース経路を提供する。
Termux経路は、利用する依存を古いツールが処理できる場合に端末上の高速な確認を提供できるため。

## 選択しなかった理由

TermuxのD8は一部の現代的なAndroidX bytecodeで失敗し、リリース挙動ともずれ得る。
手動IDEビルドは監査しにくい。CIだけではローカルのフィードバックが遅く、端末開発が通信に依存する。

## 決定を見直す契機

Termuxでvariant互換の信頼できる現代的Androidツールチェーンが正式利用できる場合、またはGradleに代わり
CI、署名、lint、Play配布を再現可能に扱うビルドシステムが実証された場合に見直す。

