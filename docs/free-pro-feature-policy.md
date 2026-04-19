# Free And Pro Feature Policy

This document defines the Free and Pro feature split for MdLite Reader.

## Principles

- Free must remain useful for reading local Markdown files.
- Pro should improve daily reading comfort and speed.
- Pro must not weaken privacy, offline use, or the no-ads policy.
- Pro checks should stay outside the core Markdown open/read path.
- Support purchases should not unlock extra feature tiers beyond Pro.

## Free Features

Free includes the core reader:

- Open `.md` and `.markdown` files.
- Open files from Android file managers.
- Open one or more files from Termux.
- Render common Markdown text.
- Headings, paragraphs, lists, checklists, blockquotes, code blocks, inline code,
  links, tables, and horizontal rules.
- Light theme.
- Dark theme.
- Pinch font size adjustment.
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

Initial Pro candidates:

- Additional UI themes.
- Mermaid diagram rendering.
- Syntax highlighting for code blocks.
- Custom gesture shortcuts.
- Table reading enhancements.
- Recent files beyond 5 entries.
- Table of contents.
- Heading jump.
- Folder browsing.
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
Pro
```

Rationale: Mermaid is valuable but not required for basic Markdown reading. It
also adds parser/rendering complexity and should be isolated behind a feature
gate.

Code highlighting:

```text
Pro
```

Rationale: Plain code block reading stays Free. Syntax highlighting is a
developer-focused enhancement.

Gesture shortcut registration:

```text
Pro
```

Rationale: Built-in basic gestures such as pinch font sizing stay Free. Custom
tap, double-tap, long-press, or swipe shortcuts are power-user customization.

## Free Baseline For These Areas

Themes:

- Free includes light and dark.
- Pro adds extra themes and future theme customization.

Mermaid:

- Free may show Mermaid fenced blocks as code.
- Pro can render Mermaid diagrams locally if the renderer is safe and offline.

Code:

- Free shows fenced code blocks in a readable monospace block.
- Pro adds syntax highlighting and possibly language labels.

Gestures:

- Free keeps built-in gestures required for normal reading.
- Pro adds user-configurable shortcuts.

## Pro Safety Requirements

- Pro features must not require ads, tracking, login, or analytics.
- Pro features must work offline unless the feature is explicitly documented as
  requiring external data.
- Mermaid support must not enable arbitrary network loading.
- Code highlighting should use a local highlighter.
- Gesture shortcuts must have safe defaults and an easy reset.
- The reader must remain usable when Pro entitlement cannot be checked.

## Deferred Decisions

The following details should be decided before implementing Pro:

- Whether Pro is 99 JPY or the lowest available Play Console price.
- Whether support purchases are added at the same time as Pro.
- How Pro entitlement is cached for offline use.
- Whether Mermaid rendering is implemented in WebView, Java, or a future
  rendering component.
- Which gestures are customizable in the first Pro version.
