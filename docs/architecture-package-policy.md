# Architecture Package Policy

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

Future package moves should preserve that direction.
