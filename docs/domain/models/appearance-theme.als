// 仕様モデル: 外観クラスタ T1（使えるテーマと切替の巡回は権限から導く）
//
// このファイルは docs/domain/domain-glossary-appearance.md の L2 ルール T1 と
// 関連する L3 動作ルールを Alloy 6 で形式化した「検査可能な仕様」である。
// 目的: (1) ルール群の無矛盾性の検査 (2) 仕様変更時に既存の保証が壊れる
// シナリオを反例として提示する（設計段階のハーネス）。
//
// 対応する実装（モデルの写し先であり検査対象ではない）:
// - src/main/java/io/github/yosk/mdlite/viewer/ViewerTheme.java
// - src/main/java/io/github/yosk/mdlite/presentation/ViewerSettingsStore.java（load 境界）
// - src/main/java/io/github/yosk/mdlite/presentation/MainActivity.java
//   （reloadFeatureEntitlement: セッション中の権限再読込）
//
// 実行: scripts/check-domain-model.sh（Alloy CLI の exec で全コマンドを検査）

module appearanceTheme

// ---- 語彙（用語集「語の定義」に対応） ----

enum Theme { Light, Dark, Amoled, Gradient, Aurora, Mist, Dusk }
enum Entitlement { Free, Pro }

// dark 系の列挙（L3: toggled の判定は輝度でなく列挙 isDark による）
fun darkThemes: set Theme { Dark + Amoled }

// ---- T1: 権限が使えるテーマ集合を決める ----
// Free は light/dark を完整提供、Pro は全 7 種（用語集 T1「破ると」の定義そのもの）

fun available[e: Entitlement]: set Theme {
  e = Free implies Light + Dark else Theme
}

// ---- 切替の巡回（L3: ViewerTheme.next / toggled） ----

// Free の切替: dark 系 → light、それ以外 → dark（toggled）
fun toggledRel: Theme -> Theme {
  (darkThemes -> Light) + ((Theme - darkThemes) -> Dark)
}

// Pro の巡回: 実装 ViewerTheme.next の固定順
fun proOrder: Theme -> Theme {
  Light -> Dark + Dark -> Amoled + Amoled -> Gradient
    + Gradient -> Aurora + Aurora -> Mist + Mist -> Dusk + Dusk -> Light
}

fun nextRel[e: Entitlement]: Theme -> Theme {
  e = Free implies toggledRel else proOrder
}

fun next[e: Entitlement, t: Theme]: one Theme { t.(nextRel[e]) }

// ---- 読み込み境界（L3: ViewerSettingsStore.loadViewerTheme） ----
// 保存テーマが権限で使えるなら維持、使えないなら light に倒す
// （「dark のみ維持」は dark ∈ available[Free] なのでこの式に含まれる）

fun load[e: Entitlement, stored: Theme]: one Theme {
  stored in available[e] implies stored else Light
}

// ==== 静的な保証（ルール同士の整合性） ====

// 保証1: どのテーマから切り替えても、行き先は権限で使えるテーマに入る
//（使えないテーマに居る状態からでも 1 操作で使える側へ戻る = 防御的挙動）
assert NextStaysWithinAvailable {
  all e: Entitlement, t: Theme | next[e, t] in available[e]
}
check NextStaysWithinAvailable expect 0

// 保証2: 使えるテーマから巡回を続ければ、使える全テーマに到達できる
//（Pro なのに一部テーマへ切替で届かない、を防ぐ。「破ると: Pro なのに
//  light/dark しか出ない」の巡回版）
assert CycleReachesAllAvailable {
  all e: Entitlement, t: Theme |
    t in available[e] implies available[e] in t.^(nextRel[e])
}
check CycleReachesAllAvailable expect 0

// 保証3: 読み込み境界を通れば T1 が成立する
assert LoadEstablishesT1 {
  all e: Entitlement, s: Theme | load[e, s] in available[e]
}
check LoadEstablishesT1 expect 0

// 保証4: 権限で使えるテーマの保存値は読み込みで失われない
//（L1「保存・復元でユーザーのテーマ選択を失わない」の T1 との整合）
assert LoadKeepsAllowedChoice {
  all e: Entitlement, s: Theme | s in available[e] implies load[e, s] = s
}
check LoadKeepsAllowedChoice expect 0

// ==== 動作モデル（セッション中の権限変化を含む） ====

one sig Session {
  var current: one Theme,
  var entitlement: one Entitlement
}

// 起動直後: 読み込み境界を通っているので T1 成立から始まる
pred init { Session.current in available[Session.entitlement] }

// ユーザーがテーマを切り替える（権限は変わらない）
pred userCyclesTheme {
  Session.current' = next[Session.entitlement, Session.current]
  Session.entitlement' = Session.entitlement
}

// 課金スナップショット反映（MainActivity.reloadFeatureEntitlement）:
// 権限は任意に変わりうる（購入で Pro 化 / 失効検出で Free 化）が、
// 現在テーマは新しい権限で再クランプされる
pred entitlementRefreshed {
  Session.current' = load[Session.entitlement', Session.current]
}

// アプリ再起動: 権限を読み直し、保存テーマを読み込み境界で絞って復元
pred appRestarted {
  Session.current' = load[Session.entitlement', Session.current]
}

fact behaviour {
  init
  always (userCyclesTheme or entitlementRefreshed or appRestarted)
}

// 検査1: セッション中も T1 は常に成立するか？
// 期待: 反例なし（権限再読込境界でも再クランプするため、Pro テーマ使用中に
// 失効が反映されても「Free なのに Pro テーマが表示されている」窓は開かない）。
assert T1HoldsThroughoutSession {
  always Session.current in available[Session.entitlement]
}
check T1HoldsThroughoutSession for 1 but 1..6 steps expect 0

// 検査2: ユーザーの切替後も必ず T1 が成立する
assert OneUserActionRestoresT1 {
  always (userCyclesTheme implies after Session.current in available[Session.entitlement])
}
check OneUserActionRestoresT1 for 1 but 1..6 steps expect 0

// 検査3: 再起動すれば必ず T1 に復帰する
assert RestartRestoresT1 {
  always (appRestarted implies after Session.current in available[Session.entitlement])
}
check RestartRestoresT1 for 1 but 1..6 steps expect 0
