#!/data/data/com.termux/files/usr/bin/sh
set -eu

OUT="${1:-/sdcard/Download/localmd-debug-fixture.md}"

cat > "$OUT" <<'MARKDOWN'
# LocalMD Debug Fixture

This file is used for quick manual verification from Termux.

## Checklist

- Opened from a Termux command
- Rendered headings and lists
- Rendered table
- Rendered code block
- Rendered Mermaid diagram in Pro builds

| Feature | Expected result |
| --- | --- |
| Table | Horizontal scrolling only when needed |
| Tabs | File opens as a tab |
| Theme | Text remains readable |

```java
final class Example {
    String message() {
        return "syntax highlighting";
    }
}
```

```mermaid
flowchart TD
    A[Open fixture] --> B[Render Markdown]
    B --> C[Verify UI]
```
MARKDOWN

echo "Created debug fixture: $OUT"
