# Icon Design Note

This note defines the design direction for the MdLite Reader app icon before
exporting the Play Store icon.

## Goal

Create an icon that feels polished, lightweight, and trustworthy while clearly
belonging to a local Markdown viewer.

The icon should work in three places:

- Android launcher adaptive icon.
- Google Play 512 x 512 px app icon.
- Google Play feature graphic and screenshot visual system.

## Brand Impression

MdLite Reader should feel:

- Fast.
- Quiet.
- Practical.
- Reading-focused.
- Privacy-conscious.
- Modern without looking decorative for its own sake.

Avoid:

- Heavy 3D effects.
- Photo-like detail.
- Generic document icons with no distinct shape.
- Pure monochrome utility styling.
- Ranking, price, badge, or promotional text.
- Visual similarity to Google Play, GitHub, or other third-party brands.

## Core Motif

Preferred motif:

```text
Markdown document + reading pane + subtle tab edge
```

Rationale:

- A document shape communicates local files.
- A reading pane communicates viewing rather than editing.
- A tab edge connects to one of the app's key MVP features.
- Markdown syntax can be hinted at with simple marks, but should not dominate.

Do not make the icon mainly a literal `MD` text mark. Text becomes fragile at
small launcher sizes and creates localization concerns.

## Shape

Use a bold, centered symbol with a clear silhouette:

- Rounded square or document surface inside the adaptive icon safe area.
- One folded or layered corner is acceptable if it remains simple.
- One secondary tab layer may sit behind the main document.
- Keep all important content away from adaptive icon mask edges.

Small-size rule:

At 48 x 48 px, the icon must still read as one clean document/viewer symbol.
Fine Markdown lines are optional decoration and must not be required to
understand the icon.

## Color Direction

Use a balanced, non-monochrome palette.

Preferred direction:

- Deep ink text color for document marks.
- Off-white or very light cool document surface.
- Teal or green accent for freshness and privacy.
- Warm yellow accent in a small amount for readability and contrast.

Avoid:

- Dominant purple or purple-blue gradients.
- Beige, cream, sand, tan, brown, orange, or espresso-dominant themes.
- Dark blue or slate-dominant themes.
- Pure black or pure white as the main store graphic background.

Draft palette:

```text
Ink: #17212B
Document: #F8FAF7
Teal: #2AA889
Yellow accent: #F2C94C
Soft shadow: #496057
```

The final implementation may adjust contrast, but should keep the same intent.

## Detail Level

Allowed details:

- 2 to 4 short Markdown-like lines.
- One heading line.
- One checkbox or bullet mark.
- One small table/code hint if it remains readable.

Avoid:

- Dense text rows.
- Tiny `#`, `*`, or code symbols that blur at launcher size.
- More than two stacked sheets.
- More than one strong shadow.

## Adaptive Icon Rules

The Android launcher icon should remain an adaptive icon:

- Keep background and foreground separate.
- Foreground content must fit inside the safe zone.
- Avoid relying on transparent edge details.
- Round icon and standard icon should use the same foreground.

Current resources to replace or update:

```text
src/main/res/drawable/ic_launcher_background.xml
src/main/res/drawable/ic_launcher_foreground.xml
src/main/res/mipmap-anydpi-v26/ic_launcher.xml
src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml
src/main/res/mipmap/ic_launcher.xml
src/main/res/mipmap/ic_launcher_round.xml
```

## Play Store Icon Rules

Required output:

```text
play-store/icon-512.png
```

Requirements:

- 512 x 512 px.
- 32-bit PNG with alpha.
- Up to 1024 KB.
- No price, ranking, store badge, or promotional text.
- Must visually match the launcher icon.

## Feature Graphic Connection

The feature graphic should extend the icon's visual system, not repeat the app
icon at large size.

Use:

- The same document shape language.
- The same teal and yellow accents.
- A simple reading scene with tabs and Markdown content.

Avoid:

- Enlarging the icon as the main feature graphic.
- Dense UI screenshots inside the feature graphic.
- Text-heavy slogans.

## Acceptance Checklist

- [ ] The icon is recognizable at 48 x 48 px.
- [ ] The icon is not dependent on readable text.
- [ ] The adaptive icon foreground stays inside the safe area.
- [ ] The palette does not read as one-note purple, beige, brown/orange, or dark blue.
- [ ] The icon communicates local document reading.
- [ ] The icon does not imply editing, cloud sync, or online services.
- [ ] The Play Store icon can be exported at 512 x 512 px.
- [ ] The feature graphic can reuse the same visual language.
