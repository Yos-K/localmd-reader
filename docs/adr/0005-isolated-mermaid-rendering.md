# ADR-0005: Isolate Local Mermaid Rendering from the Reader

Status: Accepted

## Decision

Render bundled Mermaid-compatible diagrams asynchronously in a separate,
constrained rendering component. Keep the main reader WebView JavaScript-free,
load no renderer from a network, sanitize the resulting SVG, and preserve source
text when rendering fails.

## Alternatives Considered

- Enable Mermaid JavaScript in the document reader.
- Fetch Mermaid or a rendering service remotely.
- Implement a complete diagram engine in Java or WASM immediately.
- Omit diagrams or isolate a bundled local renderer.

## Why This Decision

Diagrams provide significant Markdown value. Isolation preserves the untrusted
document boundary, asynchronous work protects reading responsiveness, and a
bundled renderer keeps operation deterministic and offline.

## Why Alternatives Were Rejected

Reader scripting weakens ADR-0001. Remote rendering leaks content and requires
networking. A new native engine has high compatibility and maintenance cost.
Omitting diagrams removes an important technical-document use case.

## Reconsider When

Reconsider if a small, mature native or WASM renderer meets compatibility,
licensing, size, theme, security, and performance requirements better than the
isolated bundled implementation.

