# ADR-0008: Trace High-Risk Interaction Commands through Model, Code, and Tests

Status: Accepted

## Decision

Register high-risk interaction commands in a machine-readable contract that
links each state transition to one implementation entry point and one behavior
test. Mark implementation entry points with `interaction-command`, validate
file and locator existence in local preflight and required CI, and introduce
the contracts incrementally when an interaction is added, changed, or found
defective through exploration.

## Alternatives Considered

- Treat prose ADRs as sufficient interaction evidence.
- Check only the internal consistency of the state model.
- Require contracts for every historical command immediately.
- Add traceability incrementally for high-risk and changed commands.

## Why This Decision

A correct state graph does not prove that Android touch handling implements the
modeled behavior. Conversely, an implementation marker without a transition or
behavior test exposes an undocumented and unprotected interaction. Locator-level
traceability detects these drifts quickly without requiring an emulator in the
fitness job, while the linked medium test verifies runtime behavior in CI.

## Why Alternatives Were Rejected

Prose cannot block drift. Model-only checks allowed a scroll container to exist
without proving that a vertical swipe reaches lower actions. Registering every
historical command at once would encourage weak placeholder tests and obscure
the highest-risk paths instead of improving them.

## Reconsider When

Reconsider incremental scope when all interaction commands have meaningful
behavior tests, when semantic code analysis can derive bindings without source
markers, or when measured maintenance cost exceeds the regressions prevented.
