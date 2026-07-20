# Play Billing Design

This document defines the initial Pro purchase design for LocalMD Reader.

## Product

- Product id: `localmd_reader_pro`
- Type: one-time product / managed product
- Initial price target: 99 JPY
- Purpose: support-oriented Pro unlock with useful advanced reader features

## Entitlement Rule

Only a completed purchased state grants Pro entitlement.

| Purchase state | Entitlement |
| --- | --- |
| Purchased | Pro |
| Not purchased | Free |
| Pending | Free |
| Unknown | Free |
| Billing unavailable | Free |

Unknown, network failure, billing unavailable, cancelled, pending, or otherwise
untrusted states must fail safe as Free.

## Cache Rule

The app may cache the last known purchased state for local usability, but:

- Do not store purchase tokens or receipts.
- Do not log purchase tokens, receipts, account identifiers, or order details.
- Treat cache as an optimization, not the source of truth.
- If the Billing client reports a reliable non-purchased state, lock Pro again.

## UI Rule

- `Pro features` shows available or locked features.
- The purchase entry point belongs in `Pro features`.
- Purchase success unlocks Pro features without requiring an app restart when
  practical.
- Cancellation, pending, billing unavailable, or unknown state leaves the app in
  Free mode with a short user-facing message.

## Test Strategy

Implement in this order:

1. Purchase state model tests.
2. Product id and one-time product tests.
3. Entitlement conversion tests.
4. Billing adapter tests around mapped states.
5. Manual licensed-tester verification in Google Play.

Billing implementation must stay outside the core domain. The core domain only
knows `ProProduct`, `BillingPurchaseSnapshot`, `ProPurchaseState`,
`ProPurchaseEntitlementSource`, and `FeatureEntitlement`.

`BillingPurchaseSnapshot` is a small domain-safe snapshot of Google Play Billing
state. It must not contain purchase tokens, receipts, order ids, account ids, or
other personal data.
