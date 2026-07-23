# ADR-0020: Keep MainActivity as the Android Composition Boundary

Status: Accepted

## Decision

Keep `MainActivity` responsible for Android lifecycle integration and object
composition. Move always-valid state transitions and cross-feature decisions to
platform-independent model or viewer types, persistence behind repository
ports, and repeated Android view construction to focused presentation components.

Place code by abstraction rather than call order: cross-capability models in
`model`, reading behavior in `viewer`, storage concepts in `file`, adapters in
`infrastructure`, and Android rendering and lifecycle wiring in `presentation`.
`MainActivity` may coordinate these components but must not reimplement their rules.

## Alternatives Considered

- Keep all screen behavior in `MainActivity` because Android callbacks start there.
- Split the Activity only into lifecycle-oriented helper classes.
- Introduce a framework-heavy presentation architecture and rewrite the screen.
- Extract behavior incrementally according to its abstraction and ownership.

## Why This Decision

The Activity accumulated rendering, persistence, tab restoration, pinning, and
view-construction rules. This made regressions possible when one entry point
forgot behavior owned only by the view. Platform-independent owners make rules
directly testable, while focused Android renderers reduce view-construction
noise without hiding domain decisions in UI helpers. Incremental extraction
keeps the released application verifiable throughout the refactoring.

## Why Alternatives Were Rejected

An Activity-centered design preserves temporal coupling and Android-only tests.
Lifecycle-oriented folders classify code by execution timing rather than
meaning. A framework rewrite would create broad release risk without improving
the domain model proportionally.

## Reconsider When

Reconsider if Android lifecycle wiring becomes small and stable enough for a
different composition root, if multiple screens need the same presentation
state owner, or if measured coupling shows that the current abstraction
boundaries create more translation than they remove.
