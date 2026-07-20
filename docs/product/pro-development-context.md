# Pro Development Context

> Status: historical context snapshot.
>
> This document records the context that restarted Pro feature development after
> closed testing recruitment reached 12 testers. It is not the canonical current
> Pro roadmap. Current Free/Pro boundaries are defined by
> [`free-pro-feature-policy.md`](free-pro-feature-policy.md), domain rules are in
> [`domain-glossary.md`](../domain/domain-glossary.md), and release state is tracked by the
> active release checklist.

## Current Product State

LocalMD Reader v0.1.0 is a lightweight Android Markdown viewer for local files.
The Free app already includes:

- Local `.md` and `.markdown` file opening.
- Android file manager and Termux opening flows.
- Safe local Markdown rendering in a locked-down WebView.
- Syntax highlighting for fenced code blocks.
- Basic local Mermaid diagram rendering.
- Light and dark themes.
- Pinch font-size adjustment.
- Double-tap shortcuts.
- Tabs, tab restoration, and recent files.
- Table rendering with horizontal scrolling.
- English and Japanese UI text.
- No ads, tracking, login, cloud sync, or `INTERNET` permission.

At the time of this snapshot, the app was in Google Play closed testing, store
assets and the launcher icon had been refreshed, and the uploaded
closed-testing build was versionCode 7. Do not use this paragraph as current
release status.

## Existing Pro Foundation

The codebase already contains explicit Free/Pro domain boundaries:

- `FeatureEntitlement`
- `FeatureEntitlements`
- `ViewerFeature`
- `ProFeatureCatalog`
- `ProFeatureDescriptor`
- `ProFeaturePresentation`
- `ProFeaturePresentationItem`

At the time of this snapshot, the closed-testing release entitlement remained
Free. Current implementation still must not scatter raw booleans through UI
code. Feature access should go through domain objects.

The Markdown renderer already preserves safe fenced-code language names as
`language-...` classes. This is the foundation for syntax highlighting.
Parser migration criteria are defined in
[`markdown-parser-migration-criteria.md`](markdown-parser-migration-criteria.md).

## Pro Feature Principles

- Pro improves reading comfort and power-user workflows.
- Free must let users experience every core reading value before purchase.
- Pro should make frequent workflows faster, more flexible, or more pleasant.
- Pro must not weaken privacy, offline use, or the no-ads policy.
- Pro features should remain local-first.
- Pro checks must stay outside the critical path for opening and reading a
  Markdown file.
- If Pro entitlement cannot be checked, the viewer must remain usable as Free.
- Free must keep a complete basic Markdown reading experience.

## Initial Pro Feature Order

1. Additional reading themes.
2. Advanced gesture shortcuts beyond double tap.
3. Table of contents and heading jump.
4. Extended reading history and pinned documents.

This order keeps core reading quality in Free while giving frequent users clear
reasons to upgrade for speed and comfort.

## First Implementation Slice

The first development slice keeps the Free reader complete and moves Pro toward
comfort features:

- Keep code highlighting and basic Mermaid rendering enabled for Free.
- Keep double-tap shortcut configuration enabled for Free.
- Gate advanced gestures, additional themes, and long-document convenience
  features behind Pro.
- Preserve security: code content and diagram fallback content must remain
  escaped, and highlighting must not allow raw HTML injection.

The first slice does not need Play Billing. It can be gated behind a domain
configuration or entitlement-aware rendering mode while the actual purchase
integration is deferred.

## Deferred Pro Work

The following items are intentionally deferred until the first highlighting
slice is stable:

- Play Billing integration.
- Offline entitlement cache.
- Pro purchase UI.
- Theme catalog UI polish.
- Advanced gesture settings UI polish.
- Mermaid advanced controls.

## Testing Rules

Implementation continues with TDD:

- For implementation and refactoring, update or add tests first. Do not start
  by changing production code and then add tests afterward to justify the
  implementation.
- For new behavior, write the smallest failing specification test before the
  implementation.
- For refactoring, first confirm or add characterization tests for the behavior
  that must not change, then refactor while tests stay green.
- For bug fixes, write a failing regression test that reproduces the bug before
  applying the fix.
- Avoid test smells documented by `kawasima/savanna-maven-plugin`.
- Tests must describe the user-visible specification in their names.
- Boundary values must be covered where relevant.
- Property-style or generated-input tests should be added for renderer safety
  when the behavior accepts arbitrary Markdown text.
- Free behavior and Pro behavior must both be tested.
- Safety tests must assert that raw HTML and unsafe attributes are not emitted.
- Do not put conditional test logic (`if`, `while`, `switch`) in test bodies.
- Do not use `Thread.sleep()` in tests.
- Do not skip tests in build commands or project scripts.
- Do not create ad hoc assertion helpers in each test file. Use
  `io.github.yosk.mdlite.testing.TestAssertions` instead.
- Each assertion must have a message that explains the expected behavior.

## Release Note

Pro feature code can be developed in the public repository. Secrets, signing
keys, service account JSON, and Play Console credentials must remain outside the
repository.

## Third-Party Asset Rule

Bundled third-party code or assets must be recorded in `THIRD_PARTY_NOTICES.md`
with the exact version, source URL, package URL, license, and included files.

For Mermaid rendering, the app currently bundles `mermaid@11.15.0` under MIT.
The license text is kept in `docs/third-party/mermaid-11.15.0-MIT.txt`, and
`./test.sh` checks that the notice files remain present.

Do not copy icons, screenshots, store graphics, fonts, or JavaScript from other
apps or websites unless their license explicitly allows redistribution.
