# Pro Development Context

This document resets the working context for Pro feature development after the
closed testing recruitment phase reached 12 testers.

## Current Product State

LocalMD Reader v0.1.0 is a lightweight Android Markdown viewer for local files.
The Free app already includes:

- Local `.md` and `.markdown` file opening.
- Android file manager and Termux opening flows.
- Safe local Markdown rendering in a locked-down WebView.
- Light and dark themes.
- Pinch font-size adjustment.
- Tabs, tab restoration, and recent files.
- Table rendering with horizontal scrolling.
- English and Japanese UI text.
- No ads, tracking, login, cloud sync, or `INTERNET` permission.

The app is in Google Play closed testing. Store assets and the launcher icon
have been refreshed. The current uploaded closed-testing build at the time of
this context reset is versionCode 7.

## Existing Pro Foundation

The codebase already contains explicit Free/Pro domain boundaries:

- `FeatureEntitlement`
- `FeatureEntitlements`
- `ViewerFeature`
- `ProFeatureCatalog`
- `ProFeatureDescriptor`
- `ProFeaturePresentation`
- `ProFeaturePresentationItem`

The current closed-testing release entitlement remains Free. Pro implementation
must not scatter raw booleans through UI code. Feature access should go through
domain objects.

The Markdown renderer already preserves safe fenced-code language names as
`language-...` classes. This is the foundation for syntax highlighting.

## Pro Feature Principles

- Pro improves reading comfort and power-user workflows.
- Pro must not weaken privacy, offline use, or the no-ads policy.
- Pro features should remain local-first.
- Pro checks must stay outside the critical path for opening and reading a
  Markdown file.
- If Pro entitlement cannot be checked, the viewer must remain usable as Free.
- Free must keep a complete basic Markdown reading experience.

## Initial Pro Feature Order

1. Syntax highlighting for fenced code blocks.
2. Additional reading themes.
3. Custom gesture shortcuts.
4. Mermaid diagram rendering.

This order is chosen because syntax highlighting builds on the existing renderer
language-class foundation and has the smallest impact on file access, storage,
and UI navigation.

## First Implementation Slice

The first development slice is a local syntax-highlighting pipeline:

- Add a renderer-level option for code highlighting.
- Keep the default Free behavior as plain escaped code blocks.
- Add a small local highlighter for an initial language set.
- Start with Java-like keywords because the app is currently implemented in
  Java and existing tests already use Java fenced code.
- Preserve security: code content must remain escaped, and highlighting must not
  allow raw HTML injection.
- Add CSS classes in the page builder for highlighted spans.

The first slice does not need Play Billing. It can be gated behind a domain
configuration or entitlement-aware rendering mode while the actual purchase
integration is deferred.

## Deferred Pro Work

The following items are intentionally deferred until the first highlighting
slice is stable:

- Play Billing integration.
- Offline entitlement cache.
- Pro purchase UI.
- Theme catalog UI.
- Gesture shortcut settings UI.
- Mermaid renderer selection.

## Testing Rules

Implementation continues with TDD:

- Tests must describe the user-visible specification in their names.
- Boundary values must be covered where relevant.
- Property-style or generated-input tests should be added for renderer safety
  when the behavior accepts arbitrary Markdown text.
- Free behavior and Pro behavior must both be tested.
- Safety tests must assert that raw HTML and unsafe attributes are not emitted.

## Release Note

Pro feature code can be developed in the public repository. Secrets, signing
keys, service account JSON, and Play Console credentials must remain outside the
repository.
