# MdLite Reader Manual Check

Use this file for v0.1.0 manual device checks.

## Checklist

- [ ] Open this Markdown file from the in-app picker.
- [ ] Open this Markdown file from a file manager.
- [x] Confirm checklists render.

## Table Wider Than The Screen

| Column 1 | Column 2 | Column 3 | Column 4 | Column 5 | Column 6 | Column 7 | Column 8 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| This row is intentionally wide | Swipe horizontally | Confirm the scroll hint | Confirm dark mode visibility | Confirm links still work | Confirm text is readable | Confirm no layout break | Done |

## Code

```text
data MarkdownFile = ReadableMarkdownFile OR UnsupportedFile
behavior Markdownを表示する = ReadableMarkdownFile AND ViewerSettings -> RenderedDocument
```

## Link

[Open example](https://example.com)

## Raw HTML Must Be Text

<script>alert("not executed")</script>
