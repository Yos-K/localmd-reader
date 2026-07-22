# ADR-0018: Compose Every Visible String from One Language-Specific View Type

Status: Accepted

## Decision

Every string rendered by a viewer is supplied by the selected `ViewerText`
subtype. Structured copy such as the Pro feature catalog is owned as a complete
language-specific value by `EnglishViewerText` or `JapaneseViewerText`; it is
not assembled from a localized shell and language-neutral components that
contain display strings. Presentation code receives one `ViewerText` and cannot
select a second language source independently.

## Alternatives Considered

- Keep feature descriptions as English constants in the domain catalog.
- Translate individual strings in the dialog at rendering time.
- Use Android resource identifiers throughout the domain model.
- Make each language-specific viewer own its complete structured display copy.

## Why This Decision

Selecting Japanese means every visible string must be Japanese. Requiring every
`ViewerText` subtype to provide the complete catalog makes missing language
implementations a compile-time failure and keeps language selection at one
composition boundary.

## Why Alternatives Were Rejected

English domain constants produce mixed-language screens. Per-string conditions
scatter language decisions and permit omissions. Android resource identifiers
would couple the platform-independent model to Android. Complete language view
types preserve one source of display text without weakening domain feature
identity.

## Reconsider When

Reconsider if supported languages grow enough to require generated catalogs,
Android resources can provide the same completeness guarantee without entering
the domain model, or an external translation pipeline validates every structured
copy entry at build time.
