# ADR-0009: Own Interaction State above Android Views

Status: Accepted

## Decision

Represent user-visible interaction states and transitions with always-valid
model types above the Android view layer. Views render a model state and relay
user commands; view properties such as visibility, animation progress, text,
and nullable widget fields are not authoritative specification state.

Apply this decision first to the navigation menu (`Closed` or `Open`) and the
Markdown library panel (`Unselected`, `Expanded`, or `Collapsed`). A selected
library state always contains a valid location, complete listing, and filter
query, from which it derives the visible listing. Opening, closing, toggling,
and filtering are total operations over their valid model inputs.

## Alternatives Considered

- Infer interaction state from Android view properties.
- Keep booleans and nullable content fields in presentation classes.
- Model only data while leaving user-operation behavior in views.
- Model both data distinctions and user-operation transitions above views.

## Why This Decision

View properties describe a rendering at one instant, not the user's valid
workflow state. Treating them as the specification allowed behavior such as
menu scrolling and library expansion to disappear during UI restructuring.
Explicit variants prevent impossible combinations, preserve data across
rendering changes, and make every valid command produce a valid next state.
They also give the interaction traceability harness stable model transitions
to connect to adapters and behavior tests.

## Why Alternatives Were Rejected

Visibility and animation values can be temporarily inconsistent during a
transition. Boolean and nullable fields permit states such as an expanded tree
without a location. Modeling data without behavior still leaves commands and
their completion conditions implicit in the view, which was the source of the
regression this decision addresses.

## Reconsider When

Reconsider the concrete state decomposition when Android navigation APIs own
the same state with equivalent compile-time guarantees, when a new interaction
cannot be represented as a total transition, or when measured complexity of a
state type exceeds the regressions and invalid states it prevents.
