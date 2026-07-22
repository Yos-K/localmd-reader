# Free And Pro Feature Policy

> Status: **current source of truth** for Free/Pro boundaries. For historical
> context see [`pro-development-context.md`](pro-development-context.md); domain
> rules live in [`domain-glossary.md`](../domain/domain-glossary.md).

This document defines the Free and Pro feature split for LocalMD Reader.

## Principles

- Free must remain useful for reading local Markdown files.
- Pro should improve daily reading comfort and speed.
- Pro guidance should preserve the Free value proposition first, then explain
  Pro as a convenience upgrade for long files and linked project notes.
- Pro must not weaken privacy, offline use, or the no-ads policy.
- Pro checks should stay outside the core Markdown open/read path.
- Support purchases should not unlock extra feature tiers beyond Pro.

## Free Features

Free includes the core reader:

- Open `.md` and `.markdown` files.
- Open files from Android file managers.
- Choose Markdown files from a selected folder.
- Open one or more files from Termux.
- Render common Markdown text.
- Headings, paragraphs, lists, checklists, blockquotes, code blocks, inline code,
  links, tables, and horizontal rules.
- Syntax highlighting for supported fenced code blocks.
- Basic local Mermaid diagram rendering.
- Light theme.
- Dark theme.
- Pinch font size adjustment.
- Double-tap shortcuts.
- Tabs.
- Tab restoration.
- Recent files up to 5 entries.
- Clear recent file history.
- Top or bottom controls placement.
- English and Japanese UI.
- Privacy dialog.

Free must not be made intentionally frustrating. A user should be able to read
Markdown comfortably without paying.

## Pro Features

Pro is a one-time purchase for extra reading comfort and power-user workflows.
It must be presented as a way to read long documents, wide tables, and linked
local Markdown sets faster, not as a fix for an intentionally weakened Free
reader.

Initial Pro candidates:

- Additional UI themes.
- Advanced gesture shortcuts beyond double tap.
- Table reading enhancements, starting with sticky table headers and first
  columns.
- Recent files up to 20 entries.
- Table of contents.
- Heading jump.
- Relative link support.
- Relative image support.
- Export options.

## Requested Feature Classification

Additional UI themes:

```text
Pro
```

Rationale: Light and dark theme stay Free. Extra themes such as sepia, AMOLED,
high contrast, or custom accent colors are comfort features for frequent use.

Mermaid support:

```text
Free basic / Pro advanced
```

Rationale: Mermaid is a major reason to choose this app for technical Markdown.
Basic offline rendering belongs in Free. Pro can later add comfort features such
as diagram-focused navigation, tuning, or export.

Code highlighting:

```text
Free
```

Rationale: Code readability is part of the core Markdown reader value. Keeping
basic highlighting Free makes the app useful before purchase.

Gesture shortcut registration:

```text
Free double tap / Pro advanced
```

Rationale: Double tap is simple enough to be a Free convenience. Circle,
directional, and custom drawn gestures are power-user customization and belong
in Pro.

Initial public Pro catalog order:

1. More comfortable reading themes.
2. More gesture shortcuts.
3. Faster long-document navigation.
4. Heading jump shortcuts.
5. Easier wide-table reading.
6. More reading history.
7. Linked project notes.
8. Local images in project notes.
9. Export the current rendered document as HTML.

## Free Baseline For These Areas

Themes:

- Free includes light and dark.
- Pro adds extra themes and future theme customization.
- Pro themes should represent distinct reading purposes rather than near-duplicate
  light or dark variants:
  modern gradient, dark aurora, AMOLED, high-contrast ink, and sepia long-form
  reading.

Mermaid:

- Free renders Mermaid diagrams locally if the renderer is safe and offline.
- Pro may add advanced Mermaid comfort features later.
- The bundled Mermaid renderer must preserve upstream license notices.
- Mermaid support must be documented as Mermaid-compatible rendering, not as an
  official Mermaid product or service.

Code:

- Free shows fenced code blocks with basic syntax highlighting where supported.
- Unsupported languages stay readable as escaped monospace code.

Gestures:

- Free keeps built-in gestures required for normal reading.
- Free includes double-tap shortcut configuration.
- Pro adds circle, directional, and custom drawn shortcuts.
- Pro adds next-heading and previous-heading actions for shortcut assignment.

Recent files:

- Free keeps a small recent list of 5 entries.
- Pro extends the recent list to 20 entries and adds pinned documents.

File and library selection:

- Free uses the ordinary Android file picker as its single local-file entry
  point. It does not add a one-shot folder picker that duplicates this flow.
- The Pro action must be worded as opening the Markdown library because it may
  resume the remembered root without showing Android's folder picker.
- Pro provides project-style folder browsing: directories are listed before
  Markdown files, selecting a directory moves into it, and every nested level
  provides an action to return to its parent.
- The Pro library is a persistent tree directly below its menu action, not a
  one-shot dialog. Opening a document closes the menu for reading but preserves
  the current tree so reopening the menu resumes navigation immediately.
- The library title shows a breadcrumb from the project root to the current
  directory. Deep paths keep the root and nearest directories while collapsing
  intermediate segments so the title remains usable on a phone.
- The breadcrumb starts with the selected root folder's display name so users
  can distinguish projects. If the document provider supplies no usable name,
  the localized Markdown library label is used as a fallback.
- Pro can filter the current library directory by file or folder name without
  querying the document provider again. Clearing the query restores the full
  current directory, and navigation keeps each matched item's original type.
- Pro remembers the last selected project root and reopens it from the library
  action while Android's persisted URI permission remains valid.
- If that permission is revoked or the stored URI becomes invalid, the app must
  forget the root and return to Android's folder picker instead of trapping or
  crashing the user.
- Empty nested folders must retain both the parent-folder action and the action
  to choose another root folder, so the navigation flow never becomes trapped.
- This boundary is defined by ADR-0017.

Tables:

- Free keeps readable mobile tables with horizontal scrolling and visible scroll
  hints.
- Pro adds sticky table headers and sticky first columns so wide tables stay
  readable while scrolling.

Relative links:

- Free keeps relative Markdown link text readable.
- Pro preserves safe relative Markdown links as anchors so linked local document
  sets can become navigable.
- Absolute file URLs, custom schemes, root-relative paths, and protocol-relative
  URLs must not be emitted as anchors.

Relative images:

- Free keeps relative Markdown image alt text readable.
- Pro renders safe relative image references as image tags for local document
  sets that include nearby image files.
- Absolute file URLs, custom schemes, root-relative paths, protocol-relative
  URLs, and remote image URLs must not be emitted as image tags.

HTML export:

- Free does not expose rendered-document export.
- Pro can save the current file document as an HTML file through Android's
  document creation UI.
- The exported body comes only from the sanitized renderer output and keeps the
  current reading theme and font size.
- Pro also opens Android's standard print flow for the current file document;
  compatible devices can choose Save as PDF there without a custom PDF engine.
- Saved files and temporary user Markdown drafts can be exported or printed.
  Generated welcome content stays outside the document-output type and cannot
  be exported. Available print destinations depend on the device's print
  services.

## Pro Safety Requirements

- The Free/Pro boundary must be represented by explicit domain objects, not by
  scattered booleans in UI code.
- The current release entitlement must be defined in one place so that future
  billing integration can replace it without changing the Markdown reading
  path.
- Pro features must not require ads, tracking, login, or analytics.
- Pro features must work offline unless the feature is explicitly documented as
  requiring external data.
- Mermaid support must not enable arbitrary network loading.
- Mermaid support must use only bundled local code and must not fetch renderer
  code from a CDN.
- Code highlighting should use a local highlighter.
- Gesture shortcuts must have safe defaults and an easy reset.
- The reader must remain usable when Pro entitlement cannot be checked.

## Deferred Decisions

The following details should be decided before implementing Pro:

- Whether Pro is 99 JPY or the lowest available Play Console price.
- Whether support purchases are added at the same time as Pro.
- How Pro entitlement is cached for offline use.
- Whether Mermaid rendering can eventually move from Mermaid.js to a smaller
  renderer without losing needed compatibility.
- Which gestures are customizable in the first Pro version.
