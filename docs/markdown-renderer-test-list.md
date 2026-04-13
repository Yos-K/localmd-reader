# Markdown Renderer Test List

## Safe HTML

- [x] escapes raw HTML tags in Markdown text
- [x] does not emit raw script tags
- [x] escapes ampersands before angle brackets

## Basic Markdown

- [x] renders level 1 heading as h1
- [x] renders plain text as paragraph
- [x] renders fenced code block with escaped content
- [x] renders inline code with escaped content
- [x] renders bullet list items as unordered list
- [x] renders numbered list items as ordered list
- [x] renders blockquote as blockquote
- [x] renders horizontal rule

## Safety

- [ ] renderer does not crash on empty input
- [ ] renderer does not crash on arbitrary text
