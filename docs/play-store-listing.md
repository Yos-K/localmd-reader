# Play Store Listing

This document defines the Google Play listing text for MdLite Reader v0.1.0.

Source requirements checked on 2026-04-14:

- App name: 30 characters or fewer.
- Short description: 80 characters or fewer.
- Full description: 4,000 characters or fewer.
- App icon: 512 x 512 px, 32-bit PNG with alpha, up to 1024 KB.
- Feature graphic: 1024 x 500 px, JPEG or 24-bit PNG with no alpha.
- Screenshots: at least 2 total, JPEG or 24-bit PNG with no alpha.
- Recommended phone screenshots: at least 4, minimum 1080 px resolution, 9:16 portrait or 16:9 landscape.

Official references:

- https://support.google.com/googleplay/android-developer/answer/9859152
- https://support.google.com/googleplay/android-developer/answer/9866151

## App Details

App name:

```text
MdLite Reader
```

Copy source:

```text
play-store/listing/en-US/title.txt
```

Short description:

```text
Local Markdown reader with tabs, dark mode, and offline rendering
```

Copy source:

```text
play-store/listing/en-US/short-description.txt
```

Full description:

```text
MdLite Reader is a lightweight Android Markdown viewer for local files.

Open .md and .markdown documents from the app or from Android file managers, then read them on your device with a simple viewer focused on speed and clarity.

Designed for everyday reading:
- Local Markdown rendering
- Multiple tabs
- Tab restoration after restart
- Light and dark themes
- Pinch gesture for font size
- Recent files, limited to 5 entries
- Tables, code blocks, checklists, blockquotes, headings, and links

Privacy-oriented by design:
- No login
- No sync
- No analytics SDK
- No network permission
- Selected Markdown content stays on your device

MdLite Reader v0.1.0 focuses on viewing, not editing. Full CommonMark compatibility, Mermaid, math, footnotes, remote image loading, relative image rendering, and cloud sync are not included in this version.
```

Copy source:

```text
play-store/listing/en-US/full-description.txt
```

Character count check:

```text
scripts/listing text is under current Google Play limits:
- title: 30 characters or fewer
- short description: 80 characters or fewer
- full description: 4,000 characters or fewer
```

Editorial rules:

- Do not mention price, discounts, rankings, awards, or store badges.
- Do not imply editing, sync, cloud storage, or full CommonMark compatibility for
  v0.1.0.
- Keep the privacy claims aligned with `PRIVACY.md` and
  `docs/play-store-data-safety.md`.
- Update the app text, screenshot set, and data safety document together if a
  future version adds network access, sync, ads, analytics, crash reporting, or
  remote image loading.

## Store Category

Primary category:

```text
Productivity
```

Rationale:

MdLite Reader is a file-reading utility for notes, documentation, and local text workflows.

## App Icon

Required asset:

```text
play-store/icon-512.png
```

Design note:

```text
docs/icon-design-note.md
```

Export command:

```sh
scripts/export-play-store-icon.sh
```

Requirements:

- 512 x 512 px.
- 32-bit PNG with alpha.
- Up to 1024 KB.
- No ranking, price, store badge, or promotional text.
- Must visually match the launcher icon direction.

## Feature Graphic

Required asset:

```text
play-store/feature-graphic-1024x500.png
```

Export command:

```sh
scripts/export-play-store-feature-graphic.sh
```

Requirements:

- 1024 x 500 px.
- JPEG or 24-bit PNG.
- No alpha.
- Avoid pure white, pure black, or dark gray backgrounds.
- Keep the focal point near the center.
- No price, ranking, award, Google Play badge, or call-to-action text.

Draft visual direction:

Use a clean reading scene: a Markdown document surface, subtle code/list/table details, and the MdLite Reader mark. Keep text minimal or omit text entirely so the asset works across locales.

Alt text:

```text
Markdown document preview with tabs and reading controls in MdLite Reader.
```

Copy source:

```text
play-store/listing/en-US/feature-graphic-alt.txt
```

## Phone Screenshots

Recommended set: 4 portrait screenshots at 1080 x 1920 px.

Screenshot 1:

```text
Welcome screen with actions to open a Markdown file or view recent files.
```

Alt text:

```text
MdLite Reader welcome screen with open file and recent file actions.
```

Copy source:

```text
play-store/listing/en-US/phone-screenshot-01-alt.txt
```

Screenshot 2:

```text
Rendered Markdown document with headings, lists, code, and links.
```

Alt text:

```text
Markdown document rendered with headings, lists, code blocks, and links.
```

Copy source:

```text
play-store/listing/en-US/phone-screenshot-02-alt.txt
```

Screenshot 3:

```text
Dark theme reading view with a table and horizontal scroll hint.
```

Alt text:

```text
Dark theme Markdown view showing a table with horizontal scrolling.
```

Copy source:

```text
play-store/listing/en-US/phone-screenshot-03-alt.txt
```

Screenshot 4:

```text
Multiple tabs and the menu for theme, language, placement, and recent files.
```

Alt text:

```text
Tabs and menu controls for theme, language, placement, and recent files.
```

Copy source:

```text
play-store/listing/en-US/phone-screenshot-04-alt.txt
```

Screenshot rules:

- Use actual app screenshots.
- Do not include fingers, device frames, unrelated backgrounds, or Google Play badges.
- Do not include ranking, price, award, or call-to-action text.
- Do not show personal file names, private paths, account names, notifications, or tokens.
- Crop or clean the status bar before upload if it contains notifications or carrier information.

Capture workflow:

```text
docs/play-store-screenshots.md
```

## Preview Video

Decision for v0.1.0:

```text
Do not provide a preview video.
```

Rationale:

The app is a small viewer. Four clear phone screenshots should communicate the v0.1.0 value without adding video production and localization overhead.
