# Markdown Renderer Test List

## Safe HTML

- [x] escapes raw HTML tags in Markdown text
- [x] does not emit raw script tags
- [x] escapes ampersands before angle brackets

## Basic Markdown

- [x] renders level 1 heading as h1
- [x] renders level 2 heading as h2
- [x] renders plain text as paragraph
- [x] renders fenced code block with escaped content
- [x] renders fenced code block with language info as code block
- [x] renders inline code with escaped content
- [x] renders HTTPS Markdown link as safe anchor
- [x] renders HTTP Markdown link as safe anchor
- [x] does not render JavaScript Markdown link as anchor
- [x] renders bullet list items as unordered list
- [x] renders unchecked checklist item as disabled checkbox in list
- [x] renders checked checklist item as disabled checked checkbox in list
- [x] renders numbered list items as ordered list
- [x] renders pipe table with header and body rows
- [x] escapes pipe table cell content and renders inline code
- [x] renders blockquote as blockquote
- [x] renders horizontal rule

## Safety

- [x] renderer does not crash on empty input
- [x] renderer does not crash on null input
- [x] renderer closes unterminated code fence instead of crashing
- [x] renderer does not crash on generated mixed Markdown text
