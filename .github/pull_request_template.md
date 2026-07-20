## Summary

<!-- なぜこの変更が必要か。1-3 bullet で。 -->

-

## Changes

<!-- 何を変更したか。差分の概要。 -->

-

## Test plan

<!-- どう検証したか。手動確認 / 自動テスト両方を記載。未実施項目はチェックを外す。 -->

- [ ] `sh scripts/run-unit-tests.sh` パス
- [ ] `sh scripts/check-file-sizes.sh` パス
- [ ] CI Gradle build パス
- [ ] (該当する場合) 手動動作確認

## Hard Constraints チェック

<!-- AGENTS.md の Hard Constraints に違反していないか確認。詳細は AGENTS.md 参照。 -->

- [ ] `AndroidManifest.xml` に `INTERNET` permission を追加していない
- [ ] WebView の JavaScript 有効化を変更していない
- [ ] 新規ファイルは 300 行以内、または `scripts/fitness-exceptions.txt` に正当な理由付きで例外登録

## 設計上の判断 / トレードオフ

<!-- 自明でない設計判断、棄却した代替案、将来への先送りなど。なければ削除。 -->

---

🤖 (AIエージェントが作成した場合は Co-Authored-By を末尾に追加してください)
