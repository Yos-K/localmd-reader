# ADR-0002: Free Is Complete; Pro Improves Convenience

Status: Accepted

## Decision

Free provides a complete local Markdown reader. Pro is a one-time convenience
upgrade for frequent use. Entitlement-dependent behavior is represented by
explicit domain types rather than scattered build or UI booleans.

## Alternatives Considered

- Put essential reading capabilities behind the purchase.
- Publish independent Free and Pro applications with separate code paths.
- Branch directly on build flags throughout presentation code.
- Keep one product model with typed entitlement and feature policies.

## Why This Decision

Users can evaluate and rely on the core reader before purchase. One model avoids
variant drift, and explicit types keep billing concerns outside the reading path.

## Why Alternatives Were Rejected

Weakening Free would make Pro a remedy for intentional friction. Separate apps
duplicate behavior and migration work. Distributed booleans permit invalid or
inconsistent combinations and make feature boundaries difficult to audit.

## Reconsider When

Reconsider if store rules invalidate the one-time model, operating costs require
a service-backed feature, or evidence shows that the boundary no longer supports
continued maintenance without weakening Free.

