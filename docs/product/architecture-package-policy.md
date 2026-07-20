# Architecture Package Policy

> Status: **current source of truth** for package and layering rules.

LocalMD Reader organizes code by abstraction level and conceptual cohesion,
not primarily by technical layer.

## Rule

Package names should communicate what concept owns the code.

Preferred grouping:

```text
io.github.yosk.mdlite
  model          # app specification and always-valid implementation models
  viewer         # viewer composition: language, theme, document, tabs
  markdown       # Markdown parsing/rendering/highlighting concepts
  file           # file type, size, opening, recent/restorable references
  entitlement    # free/pro capability model
  android        # Android-specific adapters and Activity wiring
```

The exact package names may evolve. The stable rule is:

- Group by concept and abstraction level first.
- Keep Android Framework types out of high-level models.
- Let Android adapter packages translate `Intent`, `Uri`, `SharedPreferences`,
  and `WebView` into application models.
- Keep specification decisions such as viewer text, theme style, file-open
  policy, and entitlement outside `Activity`.
- Avoid package names that only describe technical layers such as
  `presentation`, `infrastructure`, and `usecase` when they scatter one concept
  across multiple folders.

## Migration

The current code still contains older layer-oriented packages. Do not perform a
large package move only for aesthetics.

When touching a cohesive area:

1. Add or confirm tests for the behavior being moved.
2. Move one cohesive concept at a time.
3. Keep platform adapters near their Android boundary.
4. Run `./test.sh`.
5. Do not mix package migration with feature behavior changes.

## Current Direction

Recent refactoring moved language and theme decisions toward explicit viewer
models:

- `ViewerText` owns language-specific labels.
- `ViewerThemeStyle` owns theme-specific style values.
- `MainActivity` wires models to Android views instead of making language or
  theme decisions directly.
- `NavigationMenuState` owns the open/closed menu transition independently of
  Android animation properties.
- `MarkdownLibraryPanelState` owns unselected/expanded/collapsed library states
  and permits selected states only when location, listing, and query are valid.
- `DocumentListDialogState` owns the mutually exclusive recent, pinned, folder,
  and closed list sessions; `DocumentListCommand` owns their valid outcomes.
- `DocumentListDialogController` adapts those states and commands to Android
  dialogs so `MainActivity` does not infer a workflow from widget callbacks.
- `DisclosureState` owns collapsed/expanded transitions shared by inline menu
  sections; `ExpandableMenuSection` alone maps those states to Android views.
- `TableOfContentsMenuPanel` owns table-of-contents row rendering and styling;
  `MainActivity` only supplies headings and coordinates the selected heading.
- `HeadingNavigation` derives an always-valid unavailable or destination result
  from headings and scroll position; `MainActivity` does not retain a sentinel
  heading index.
- `DocumentTabSessionController` completes equivalent tab selection and closing
  transitions across click and gesture entry points while `OpenDocumentTabs`
  remains the platform-independent authoritative state.
- Each `OpenDocumentTab` subtype replaces rendered content polymorphically, so
  Android orchestration does not reconstruct tab types with `instanceof`.
  `OpenDocumentTabs` owns replacement within the session and preserves the
  active tab while inactive content is re-rendered.
- `MermaidRenderSessions` owns extracted blocks, pending jobs, render generations,
  and completed SVG by document. Android orchestration only executes the typed
  `MermaidRenderJob` values produced by a schedule.

Future package moves should preserve that direction.

Repository responsibilities follow the same ownership rule: the public source
repository owns reviewable source and ordinary CI, while the private release
repository owns signing and Play upload credentials. Release scripts must make
that boundary explicit. See ADR-0014.
