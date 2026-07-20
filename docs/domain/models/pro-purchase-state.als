// 仕様モデル: 権限クラスタ ProPurchaseState（購入状態の永続化写像と権限導出）
//
// このファイルは docs/domain/domain-glossary.md の L1（fromPersistenceCode は
// 未知コードを unknown に倒す fail-closed）と L2-7（purchased のときだけ Pro、
// 他は全て Free）を Alloy 6 で形式化した「検査可能な仕様」である。
// 目的: (1) ルール群の無矛盾性の検査 (2) 仕様変更時に既存の保証が壊れる
// シナリオを反例として提示する（設計段階のハーネス）。
//
// 対応する実装（モデルの写し先であり検査対象ではない）:
// - src/main/java/io/github/yosk/mdlite/domain/ProPurchaseState.java
//   （persistenceCode / fromPersistenceCode / entitlement / isPending /
//     isUnavailableOrUnknown）
// - src/main/java/io/github/yosk/mdlite/domain/FeatureEntitlement.java（free / pro）
// - src/main/java/io/github/yosk/mdlite/domain/ProPurchaseCacheEntry.java
//   （restore: fromPersistenceCode を通る復元境界）
// - src/main/java/io/github/yosk/mdlite/domain/BillingPurchaseSnapshot.java
//   （proPurchaseState: (state, acknowledged) → 購入状態。PUR1 = 承認済みのみ
//     確定購入とする安全側変換。下記「PUR1: Play Billing 境界」節で形式化）
//
// 等価性の注記: ProPurchaseState は equals() を持たない（参照等価のみ）。
// 本モデルの PurchaseState の各 atom は「value フィールドが等しい状態の同値類」を
// 表す。よってラウンドトリップ保証は「同一インスタンスへの復元」ではなく
// 「同値状態への復元（機能的等価）」の保証である。
//
// 動的モデル不採用の注記: ProPurchaseState は不変の値オブジェクトであり遷移
// メソッドを持たない（購入フローの進行は BillingPurchaseSnapshot 側の責務）。
// よって本モデルは静的検査のみで足り、Session / var / always は用いない。
//
// 実行: scripts/check-domain-model.sh（Alloy CLI の exec で全コマンドを検査）

module proPurchaseState

// ---- 語彙（用語集「語の定義」に対応） ----

// 5状態（ProPurchaseState の private int value 1..5 に対応）
enum PurchaseState { Purchased, NotPurchased, Pending, Unknown, BillingUnavailable }

enum Entitlement { Free, Pro }

// 永続化コード。Known 5 種は実装の定数文字列に対応し、Unrecognized は
// 「既知のどのコードとも一致しない任意の文字列（空文字を含む）」を1つの
// atom に畳み込んだ抽象である
enum PersistenceCode {
  PurchasedCode, NotPurchasedCode, PendingCode, UnknownCode,
  BillingUnavailableCode, UnrecognizedCode
}

// ---- persistenceCode(): 状態 → 永続化コード ----
// 実装: ProPurchaseState.persistenceCode()（5状態それぞれが異なる定数を返す）

fun persistenceCodeRel: PurchaseState -> PersistenceCode {
  Purchased -> PurchasedCode + NotPurchased -> NotPurchasedCode
    + Pending -> PendingCode + Unknown -> UnknownCode
    + BillingUnavailable -> BillingUnavailableCode
}

fun persistenceCode[s: PurchaseState]: one PersistenceCode { s.(persistenceCodeRel) }

// ---- fromPersistenceCode(): 永続化コード → 状態 ----
// 実装: ProPurchaseState.fromPersistenceCode(code)。
// purchased / not_purchased / pending / billing_unavailable は明示分岐、
// それ以外（"unknown" を含む全コード）はデフォルトで unknown() に倒す。
// UnknownCode -> Unknown も明示分岐ではなくデフォルト経由だが、結果は同じ

fun fromPersistenceCodeRel: PersistenceCode -> PurchaseState {
  PurchasedCode -> Purchased + NotPurchasedCode -> NotPurchased
    + PendingCode -> Pending + BillingUnavailableCode -> BillingUnavailable
    + UnknownCode -> Unknown + UnrecognizedCode -> Unknown
}

fun fromPersistenceCode[c: PersistenceCode]: one PurchaseState { c.(fromPersistenceCodeRel) }

// ---- entitlement(): 状態 → 権限（L2-7） ----
// 実装: ProPurchaseState.entitlement()（purchased のみ pro、他は free）

fun entitlement[s: PurchaseState]: one Entitlement {
  s = Purchased implies Pro else Free
}

// ---- isPending / isUnavailableOrUnknown の定義域 ----
// 実装: ProPurchaseState.isPending()（PENDING のみ true）、
// ProPurchaseState.isUnavailableOrUnknown()（BILLING_UNAVAILABLE / UNKNOWN で true）

fun pendingStates: set PurchaseState { Pending }
fun unavailableOrUnknownStates: set PurchaseState { BillingUnavailable + Unknown }
fun proStates: set PurchaseState { { s: PurchaseState | entitlement[s] = Pro } }

// ==== 静的な保証（ルール同士の整合性） ====

// 保証1: persistenceCode は単射（異なる状態が同じコードに保存されない）
//（破ると: キャッシュ復元時に別の購入状態へ化け、L2-7 の権限導出が狂う）
assert PersistenceCodeIsInjective {
  all s1, s2: PurchaseState |
    persistenceCode[s1] = persistenceCode[s2] implies s1 = s2
}
check PersistenceCodeIsInjective expect 0

// 保証2: 全5状態がラウンドトリップで復元できる
//（fromPersistenceCode[persistenceCode[s]] = s。billingUnavailable も専用
//  コード billing_unavailable を持つため unknown には倒れない。
//  破ると: 保存→復元で購入状態が失われ、Pro が失効または誤付与される）
assert RoundTripRestoresEveryState {
  all s: PurchaseState | fromPersistenceCode[persistenceCode[s]] = s
}
check RoundTripRestoresEveryState expect 0

// 保証3: Pro を与えるのは purchased だけ（L2-7 の形式化）
//（破ると: pending / unknown / billing_unavailable で誤って Pro を解放し、
//  収益保護・安全側既定が崩れる）
assert OnlyPurchasedGrantsPro {
  all s: PurchaseState | entitlement[s] = Pro iff s = Purchased
}
check OnlyPurchasedGrantsPro expect 0

// 保証4: isPending / isUnavailableOrUnknown / Pro 権限は互いに排他
//（pending かつ unavailable、Pro かつ pending のような重なりが無いこと。
//  破ると: 呼び出し側の状態分岐が二重に成立し、UI 表示や権限判定が矛盾する）
assert FlagsArePairwiseExclusive {
  no (pendingStates & unavailableOrUnknownStates)
  no (pendingStates & proStates)
  no (unavailableOrUnknownStates & proStates)
}
check FlagsArePairwiseExclusive expect 0

// 保証5: 未知コードは unknown に倒れ、その権限は Free（L1 fail-closed の形式化）
//（破ると: 永続データの破損・将来の未知コードで Pro が誤付与される）
assert UnrecognizedCodeFailsSafeToFree {
  fromPersistenceCode[UnrecognizedCode] = Unknown
  entitlement[fromPersistenceCode[UnrecognizedCode]] = Free
}
check UnrecognizedCodeFailsSafeToFree expect 0

// 保証6: 復元はどの状態にも到達できる（fromPersistenceCode は全射）
//（破ると: 永続化はできるのに復元では現れない「死に状態」が生まれる）
assert EveryStateIsRestorable {
  all s: PurchaseState | some c: PersistenceCode | fromPersistenceCode[c] = s
}
check EveryStateIsRestorable expect 0

// ==== 既知の制約（仕様上の限界を反例として文書化） ====

// ==== PUR1: Play Billing 境界（(state, acknowledged) → 購入状態） ====
// 実装: src/main/java/io/github/yosk/mdlite/domain/BillingPurchaseSnapshot.java
//   proPurchaseState()。PURCHASED かつ acknowledged のときだけ purchased、
//   PURCHASED(未承認)/PENDING は pending、BILLING_UNAVAILABLE は billingUnavailable、
//   他は notPurchased。これとハブ「purchased のみ Pro」が二段で
//   「購入済かつ承認済のみ Pro」を保証する（収益保護・安全側既定）。
// 探索 2026-06-13（purchase セッション P1）で観測した最重要の安全不変条件を形式化する。

// Play Billing スナップショットの生状態（4 ファクトリに対応）
enum BillingState { BPurchased, BPending, BNotPurchased, BUnavailable }
// 承認フラグ（acknowledged: boolean）
enum Ack { Acked, Unacked }

fun billingToPurchaseState[b: BillingState, a: Ack]: one PurchaseState {
  (b = BPurchased and a = Acked) implies Purchased
  else (b = BPurchased or b = BPending) implies Pending
  else b = BUnavailable implies BillingUnavailable
  else NotPurchased
}

// 保証7: Pro に到達できるのは「承認済みの Play Billing 購入」だけ（PUR1 + L2-7 を端から端まで）
//（破ると: 未承認・保留・課金不可の購入で誤って Pro を解放し、収益保護が崩れる）
assert ProRequiresAcknowledgedBillingPurchase {
  all b: BillingState, a: Ack |
    entitlement[billingToPurchaseState[b, a]] = Pro
      implies (b = BPurchased and a = Acked)
}
check ProRequiresAcknowledgedBillingPurchase expect 0

// 保証8: 未承認の購入は決して purchased にならない（PUR1 の安全側変換そのもの）
//（破ると: 「PURCHASED だが未 acknowledged」を確定購入扱いし、承認待ちの購入で Pro を誤付与する）
assert UnacknowledgedPurchaseNeverConfirmed {
  all a: Ack |
    a = Unacked implies billingToPurchaseState[BPurchased, a] != Purchased
}
check UnacknowledgedPurchaseNeverConfirmed expect 0

// 保証9: 承認済みの Play Billing 購入は必ず Pro に到達する（収益の正経路 = liveness）
//（保証7は「Pro ⇒ 承認済み購入」の一方向含意のみで安全方向だけを守る。逆向きが無いと
//  BPurchased+Acked を Pending/NotPurchased に写す退行——課金客が Pro にならない——を検出できない。
//  保証7と本保証で PUR1 を iff に閉じ、安全方向と収益の正経路の両方を形式検査に載せる）
assert AcknowledgedBillingPurchaseGrantsPro {
  billingToPurchaseState[BPurchased, Acked] = Purchased
  entitlement[billingToPurchaseState[BPurchased, Acked]] = Pro
}
check AcknowledgedBillingPurchaseGrantsPro expect 0

// ==== 既知の制約（仕様上の限界を反例として文書化） ====

// 既知1: fromPersistenceCode は単射ではない（反例を1つ期待）。
// "unknown" と未知コードはどちらも unknown に畳まれるため、復元後に
// 「元のコードが本当に "unknown" だったのか、破損データだったのか」は
// 区別できない。これは L1 fail-closed 設計の意図された帰結であり、
// 復元側で逆引きが必要になる仕様変更はこの前提を壊す
assert FromPersistenceCodeIsInjective {
  all c1, c2: PersistenceCode |
    fromPersistenceCode[c1] = fromPersistenceCode[c2] implies c1 = c2
}
check FromPersistenceCodeIsInjective expect 1
