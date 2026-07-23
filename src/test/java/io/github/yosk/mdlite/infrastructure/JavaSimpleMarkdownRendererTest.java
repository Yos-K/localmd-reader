package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.domain.CodeHighlighting;
import io.github.yosk.mdlite.domain.DocumentRenderingProfile;
import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.domain.MermaidRendering;
import io.github.yosk.mdlite.domain.RelativeImageRendering;
import io.github.yosk.mdlite.domain.RelativeLinkRendering;
import io.github.yosk.mdlite.testing.TestAssertions;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public final class JavaSimpleMarkdownRendererTest {
    @Test
    void rendersMarkdownBoldAsStrongText() {
        SafeHtml html = renderer.render("This is **important**.");

        TestAssertions.assertContains(html.value(), "This is <strong>important</strong>.",
                "Markdown bold syntax must render as strong text");
    }

    @Test
    void escapedMarkdownBoldMarkersDoNotCreateStrongText() {
        SafeHtml html = renderer.render("This is \\**important\\**.");

        TestAssertions.assertNotContains(html.value(), "<strong>important</strong>",
                "escaped Markdown bold markers must remain literal text");
    }

    @Test
    void htmlCodeBlockProvidesRawAndRenderedPreviewPanes() {
        SafeHtml html = renderer.render("```html\n<strong>Hello</strong>\n```");

        TestAssertions.assertContains(html.value(),
                "<div class=\"code-preview-pane code-preview-rendered\"><strong>Hello</strong></div>",
                "HTML preview must render allowed markup beside its escaped raw source");
    }

    @Test
    void htmlCodeBlockPreviewDoesNotEmitScriptElements() {
        SafeHtml html = renderer.render("```html\n<script>alert(1)</script>\n```");

        TestAssertions.assertNotContains(html.value(), "<script>",
                "HTML code previews must never activate script elements");
    }

    @Test
    void markdownCodeBlockProvidesRenderedPreviewPane() {
        SafeHtml html = renderer.render("```markdown\n# Title\n\n**important**\n```");

        TestAssertions.assertContains(html.value(),
                "<div class=\"code-preview-pane code-preview-rendered\"><h1 id=\"title\">Title</h1><p><strong>important</strong></p></div>",
                "Markdown code previews must reuse the normal Markdown renderer");
    }

    @Test
    void codeFenceMetadataDoesNotDisableItsLanguagePreview() {
        SafeHtml html = renderer.render("```html title=example\n<h1>Title</h1>\n```");

        TestAssertions.assertContains(html.value(), "class=\"code-preview-toggle\"",
                "the first code-fence info token must continue to select preview behavior");
    }

    @Test
    void htmlPreviewDropsAttributesFromAllowedElements() {
        SafeHtml html = renderer.render(
                "```html\n<h1 class=\"title\" onclick=\"alert(1)\">Title</h1>\n```");

        TestAssertions.assertContains(html.value(),
                "<div class=\"code-preview-pane code-preview-rendered\"><h1>Title</h1></div>",
                "HTML preview must preserve an allowed element while dropping every attribute");
    }

    private final JavaSimpleMarkdownRenderer renderer = new JavaSimpleMarkdownRenderer();

    @Test
    void escapesRawHtmlTagsInMarkdownText() {
        SafeHtml html = renderer.render("<b>hello</b>");

        TestAssertions.assertContains(html.value(), "&lt;b&gt;hello&lt;/b&gt;", "raw HTML tags must be escaped as text");
        TestAssertions.assertNotContains(html.value(), "<b>hello</b>", "raw HTML must not be emitted");
    }

    @Test
    void doesNotEmitRawScriptTags() {
        SafeHtml html = renderer.render("<script>alert(1)</script>");

        TestAssertions.assertContains(html.value(), "&lt;script&gt;alert(1)&lt;/script&gt;", "script tags must be escaped");
        TestAssertions.assertNotContains(html.value(), "<script>", "raw script start tag must not be emitted");
        TestAssertions.assertNotContains(html.value(), "</script>", "raw script end tag must not be emitted");
    }

    @Test
    void escapesRawAnchorWithJavascriptUrl() {
        SafeHtml html = renderer.render("<a href=\"javascript:alert(1)\">x</a>");

        TestAssertions.assertContains(html.value(), "&lt;a href=&quot;javascript:alert(1)&quot;&gt;x&lt;/a&gt;", "raw anchor tags with javascript URLs must be escaped as text");
        TestAssertions.assertNotContains(html.value(), "<a href=\"javascript:", "raw javascript anchor must not be emitted");
    }

    @Test
    void escapesRawImageWithInlineEventHandler() {
        SafeHtml html = renderer.render("<img src=x onerror=alert(1)>");

        TestAssertions.assertContains(html.value(), "&lt;img src=x onerror=alert(1)&gt;", "raw image tags with event handlers must be escaped as text");
        TestAssertions.assertNotContains(html.value(), "<img", "raw image tags must not be emitted");
        TestAssertions.assertNotContains(html.value(), "<img src=x onerror=", "inline event handler attributes must not be emitted as HTML attributes");
    }

    @Test
    void escapesAmpersandsBeforeAngleBrackets() {
        SafeHtml html = renderer.render("A & B < C");

        TestAssertions.assertContains(html.value(), "A &amp; B &lt; C", "ampersands and angle brackets must be escaped");
        TestAssertions.assertNotContains(html.value(), "A & B &lt; C", "raw ampersand must not remain before escaped angle bracket");
    }

    @Test
    void rendersLevelOneHeadingAsH1() {
        SafeHtml html = renderer.render("# Title");

        TestAssertions.assertContains(html.value(), "<h1 id=\"title\">Title</h1>", "level 1 heading must render as h1 with a jump target");
    }

    @Test
    void rendersLevelTwoHeadingAsH2() {
        SafeHtml html = renderer.render("## Implementation Domain Model");

        TestAssertions.assertContains(html.value(), "<h2 id=\"implementation-domain-model\">Implementation Domain Model</h2>", "level 2 heading must render as h2 with a jump target");
    }

    @Test
    void rendersHeadingAnchorsForTableOfContentsJumpTargets() {
        SafeHtml html = renderer.render("# Intro\n## Intro");

        TestAssertions.assertContains(html.value(), "<h1 id=\"intro\">Intro</h1>", "first heading must expose a stable jump target");
        TestAssertions.assertContains(html.value(), "<h2 id=\"intro-2\">Intro</h2>", "repeated heading must expose a unique jump target");
    }

    @Test
    void rendersPlainTextAsParagraph() {
        SafeHtml html = renderer.render("Plain text");

        TestAssertions.assertContains(html.value(), "<p>Plain text</p>", "plain text must render as paragraph");
    }

    @Test
    void rendersFencedCodeBlockWithEscapedContent() {
        SafeHtml html = renderer.render("```\nif (a < b) {\n  return true;\n}\n```");

        TestAssertions.assertContains(html.value(), "<pre><code>", "fenced code block must render as pre/code");
        TestAssertions.assertContains(html.value(), "if (a &lt; b) {", "code block content must be escaped");
        TestAssertions.assertContains(html.value(), "</code></pre>", "fenced code block must close pre/code");
    }

    @Test
    void rendersFencedCodeBlockWithLanguageInfoAsCodeBlock() {
        SafeHtml html = renderer.render("```text\ndata MarkdownFile = ReadableMarkdownFile OR UnsupportedFile\n```");

        TestAssertions.assertContains(html.value(), "<pre><code", "fenced code block with language info must render as pre/code");
        TestAssertions.assertContains(html.value(), "data MarkdownFile = ReadableMarkdownFile OR UnsupportedFile", "fenced code block content must be preserved");
        TestAssertions.assertNotContains(html.value(), "<p>```text", "language fence marker must not render as paragraph");
    }

    @Test
    void rendersSafeLanguageClassForFencedCodeBlock() {
        SafeHtml html = renderer.render("```java\npublic final class Note {}\n```");

        TestAssertions.assertContains(html.value(), "<pre><code class=\"language-java\">", "safe language info must render as a language class");
        TestAssertions.assertContains(html.value(), "public final class Note {}", "code content must be preserved");
    }

    @Test
    void doesNotRenderUnsafeLanguageClassForFencedCodeBlock() {
        SafeHtml html = renderer.render("```java\" onclick=\"alert(1)\nvalue\n```");

        TestAssertions.assertContains(html.value(), "<pre><code>", "unsafe language info must fall back to plain code block");
        TestAssertions.assertNotContains(html.value(), "onclick", "unsafe language info must not be emitted");
    }

    @Test
    void defaultRendererKeepsJavaCodeFencePlainForFreeReading() {
        SafeHtml html = renderer.render("```java\npublic final class Note {}\n```");

        TestAssertions.assertContains(html.value(), "public final class Note {}", "Free rendering must keep Java code readable as plain escaped text");
        TestAssertions.assertNotContains(html.value(), "code-keyword", "Free rendering must not apply Pro syntax highlighting");
    }

    @Test
    void syntaxHighlightedRendererHighlightsJavaKeywordsInFencedCodeBlock() {
        SafeHtml html = renderer.render(
                "```java\npublic final class Note {\n  public boolean enabled() {\n    return true;\n  }\n}\n```",
                CodeHighlighting.syntaxHighlighted());

        TestAssertions.assertContains(html.value(), "<span class=\"code-keyword\">public</span>", "Pro highlighting must mark Java access keywords");
        TestAssertions.assertContains(html.value(), "<span class=\"code-keyword\">final</span>", "Pro highlighting must mark Java modifier keywords");
        TestAssertions.assertContains(html.value(), "<span class=\"code-keyword\">class</span>", "Pro highlighting must mark Java declaration keywords");
        TestAssertions.assertContains(html.value(), "<span class=\"code-type\">Note</span>", "Pro highlighting must mark Java class names");
        TestAssertions.assertContains(html.value(), "<span class=\"code-function\">enabled</span>", "Pro highlighting must mark Java function names");
        TestAssertions.assertContains(html.value(), "<span class=\"code-keyword\">return</span>", "Pro highlighting must mark Java flow keywords");
        TestAssertions.assertContains(html.value(), "<span class=\"code-literal\">true</span>", "Pro highlighting must mark Java boolean literals");
    }

    @Test
    void syntaxHighlightedRendererHighlightsKotlinKeywordsInFencedCodeBlock() {
        SafeHtml html = renderer.render(
                "```kotlin\ndata class Note(val title: String?)\nfun enabled(): Boolean = true\n```",
                CodeHighlighting.syntaxHighlighted());

        TestAssertions.assertContains(html.value(), "<span class=\"code-keyword\">data</span>", "Pro highlighting must mark Kotlin data keyword");
        TestAssertions.assertContains(html.value(), "<span class=\"code-keyword\">class</span>", "Pro highlighting must mark Kotlin class keyword");
        TestAssertions.assertContains(html.value(), "<span class=\"code-type\">Note</span>", "Pro highlighting must mark Kotlin class names");
        TestAssertions.assertContains(html.value(), "<span class=\"code-keyword\">val</span>", "Pro highlighting must mark Kotlin val keyword");
        TestAssertions.assertContains(html.value(), "<span class=\"code-variable\">title</span>", "Pro highlighting must mark Kotlin variable names");
        TestAssertions.assertContains(html.value(), "<span class=\"code-function\">enabled</span>", "Pro highlighting must mark Kotlin function names");
        TestAssertions.assertContains(html.value(), "<span class=\"code-literal\">true</span>", "Pro highlighting must mark Kotlin boolean literals");
    }

    @Test
    void syntaxHighlightedRendererHighlightsJavaScriptKeywordsInFencedCodeBlock() {
        SafeHtml html = renderer.render(
                "```javascript\nconst note = true;\nfunction openNote(value) {\n  return value;\n}\n```",
                CodeHighlighting.syntaxHighlighted());

        TestAssertions.assertContains(html.value(), "<span class=\"code-keyword\">const</span>", "Pro highlighting must mark JavaScript const keyword");
        TestAssertions.assertContains(html.value(), "<span class=\"code-variable\">note</span>", "Pro highlighting must mark JavaScript variable names");
        TestAssertions.assertContains(html.value(), "<span class=\"code-function\">openNote</span>", "Pro highlighting must mark JavaScript function names");
        TestAssertions.assertContains(html.value(), "<span class=\"code-keyword\">return</span>", "Pro highlighting must mark JavaScript return keyword");
        TestAssertions.assertContains(html.value(), "<span class=\"code-literal\">true</span>", "Pro highlighting must mark JavaScript boolean literals");
    }

    @Test
    void syntaxHighlightedRendererHighlightsTypeScriptKeywordsInFencedCodeBlock() {
        SafeHtml html = renderer.render(
                "```ts\nexport class Note {\n  readonly title: string;\n}\n```",
                CodeHighlighting.syntaxHighlighted());

        TestAssertions.assertContains(html.value(), "<span class=\"code-keyword\">export</span>", "Pro highlighting must mark TypeScript export keyword");
        TestAssertions.assertContains(html.value(), "<span class=\"code-keyword\">class</span>", "Pro highlighting must mark TypeScript class keyword");
        TestAssertions.assertContains(html.value(), "<span class=\"code-type\">Note</span>", "Pro highlighting must mark TypeScript class names");
        TestAssertions.assertContains(html.value(), "<span class=\"code-variable\">title</span>", "Pro highlighting must mark TypeScript readonly field names");
    }

    @Test
    void syntaxHighlightedRendererHighlightsPythonKeywordsInFencedCodeBlock() {
        SafeHtml html = renderer.render(
                "```python\ndef enabled():\n    return True\n```",
                CodeHighlighting.syntaxHighlighted());

        TestAssertions.assertContains(html.value(), "<span class=\"code-keyword\">def</span>", "Pro highlighting must mark Python def keyword");
        TestAssertions.assertContains(html.value(), "<span class=\"code-function\">enabled</span>", "Pro highlighting must mark Python function names");
        TestAssertions.assertContains(html.value(), "<span class=\"code-keyword\">return</span>", "Pro highlighting must mark Python return keyword");
        TestAssertions.assertContains(html.value(), "<span class=\"code-literal\">True</span>", "Pro highlighting must mark Python boolean literals");
    }

    @Test
    void syntaxHighlightedRendererHighlightsHtmlTagsAndAttributesInFencedCodeBlock() {
        SafeHtml html = renderer.render(
                "```html\n<section class=\"welcome\">Open</section>\n```",
                CodeHighlighting.syntaxHighlighted());

        TestAssertions.assertContains(html.value(), "&lt;<span class=\"code-type\">section</span>", "Pro highlighting must mark HTML tag names");
        TestAssertions.assertContains(html.value(), "<span class=\"code-variable\">class</span>", "Pro highlighting must mark HTML attribute names");
        TestAssertions.assertContains(html.value(), "<span class=\"code-string\">&quot;welcome&quot;</span>", "Pro highlighting must mark HTML attribute values");
    }

    @Test
    void syntaxHighlightedRendererHighlightsXmlTagsAndAttributesInFencedCodeBlock() {
        SafeHtml html = renderer.render(
                "```xml\n<uses-sdk android:minSdkVersion=\"23\" />\n```",
                CodeHighlighting.syntaxHighlighted());

        TestAssertions.assertContains(html.value(), "&lt;<span class=\"code-type\">uses-sdk</span>", "Pro highlighting must mark XML tag names");
        TestAssertions.assertContains(html.value(), "<span class=\"code-variable\">android:minSdkVersion</span>", "Pro highlighting must mark XML attribute names");
    }

    @Test
    void syntaxHighlightedRendererHighlightsCssSelectorsAndPropertiesInFencedCodeBlock() {
        SafeHtml html = renderer.render(
                "```css\n.welcome {\n  color: #172121;\n}\n```",
                CodeHighlighting.syntaxHighlighted());

        TestAssertions.assertContains(html.value(), "<span class=\"code-type\">.welcome</span> {", "Pro highlighting must mark CSS selectors");
        TestAssertions.assertContains(html.value(), "<span class=\"code-variable\">color</span>: #172121;", "Pro highlighting must mark CSS property names");
    }

    @Test
    void syntaxHighlightedRendererHighlightsYamlKeysAndLiteralsInFencedCodeBlock() {
        SafeHtml html = renderer.render(
                "```yaml\nname: \"LocalMD\"\nenabled: true\n```",
                CodeHighlighting.syntaxHighlighted());

        TestAssertions.assertContains(html.value(), "<span class=\"code-variable\">name</span>: <span class=\"code-string\">&quot;LocalMD&quot;</span>", "Pro highlighting must mark YAML keys and strings");
        TestAssertions.assertContains(html.value(), "<span class=\"code-variable\">enabled</span>: <span class=\"code-literal\">true</span>", "Pro highlighting must mark YAML boolean literals");
    }

    @Test
    void syntaxHighlightedRendererHighlightsJsonStructuralTokensInFencedCodeBlock() {
        SafeHtml html = renderer.render(
                "```json\n{\"name\":\"LocalMD\",\"enabled\":true}\n```",
                CodeHighlighting.syntaxHighlighted());

        TestAssertions.assertContains(html.value(), "<span class=\"code-string\">&quot;name&quot;</span>", "Pro highlighting must mark JSON object keys as strings");
        TestAssertions.assertContains(html.value(), "<span class=\"code-string\">&quot;LocalMD&quot;</span>", "Pro highlighting must mark JSON string values");
        TestAssertions.assertContains(html.value(), "<span class=\"code-literal\">true</span>", "Pro highlighting must mark JSON boolean literals");
    }

    @Test
    void syntaxHighlightedRendererHighlightsShellCommandsInFencedCodeBlock() {
        SafeHtml html = renderer.render(
                "```sh\ncd ~/AndroidDev\nscripts/build-signed-release.sh aab\n```",
                CodeHighlighting.syntaxHighlighted());

        TestAssertions.assertContains(html.value(), "<span class=\"code-command\">cd</span>", "Pro highlighting must mark shell built-in commands");
        TestAssertions.assertContains(html.value(), "<span class=\"code-command\">scripts/build-signed-release.sh</span>", "Pro highlighting must mark shell command paths");
    }

    @Test
    void syntaxHighlightedRendererEscapesRawHtmlBeforeHighlightingCode() {
        SafeHtml html = renderer.render(
                "```java\npublic String html = \"<script>\";\n```",
                CodeHighlighting.syntaxHighlighted());

        TestAssertions.assertContains(html.value(), "&lt;script&gt;", "Pro highlighting must still escape raw HTML inside code");
        TestAssertions.assertNotContains(html.value(), "<script>", "Pro highlighting must not emit raw script tags from code");
    }

    @Test
    void syntaxHighlightedRendererLeavesUnknownLanguagesAsPlainEscapedCode() {
        SafeHtml html = renderer.render(
                "```unknown\npublic <tag>\n```",
                CodeHighlighting.syntaxHighlighted());

        TestAssertions.assertContains(html.value(), "public &lt;tag&gt;", "Unknown languages must stay plain and escaped");
        TestAssertions.assertNotContains(html.value(), "code-keyword", "Unknown languages must not receive Java highlighting");
    }

    @Test
    void defaultRendererKeepsMermaidFenceAsPlainCode() {
        SafeHtml html = renderer.render("```mermaid\ngraph TD\nA-->B\n```");

        TestAssertions.assertContains(html.value(), "<pre><code class=\"language-mermaid\">", "Default rendering must keep Mermaid fences as readable code blocks");
        TestAssertions.assertContains(html.value(), "graph TD", "Default rendering must keep Mermaid source readable");
        TestAssertions.assertNotContains(html.value(), "mermaid-placeholder", "Default rendering must not start asynchronous Mermaid rendering");
    }

    @Test
    void mermaidRendererShowsMermaidPlaceholderBeforeBackgroundRenderCompletes() {
        SafeHtml html = renderer.render(
                "```mermaid\ngraph TD\nA-->B\n```",
                CodeHighlighting.plain(),
                MermaidRendering.diagrams(),
                null);

        TestAssertions.assertContains(html.value(), "class=\"mermaid-placeholder\"", "Mermaid rendering must reserve a Mermaid diagram slot");
        TestAssertions.assertContains(html.value(), "Rendering in background...", "Mermaid placeholder must explain background rendering");
        TestAssertions.assertContains(html.value(), "graph TD", "Mermaid placeholder must keep source visible until SVG is ready");
    }

    @Test
    void mermaidRendererEmbedsRenderedMermaidSvgAfterBackgroundRenderCompletes() {
        Map<Integer, SafeHtml> renderedDiagrams = new HashMap<Integer, SafeHtml>();
        renderedDiagrams.put(Integer.valueOf(0), SafeHtml.fromTrustedRendererOutput("<svg><text>A</text></svg>"));

        SafeHtml html = renderer.render(
                "```mermaid\ngraph TD\nA-->B\n```",
                CodeHighlighting.plain(),
                MermaidRendering.diagrams(),
                renderedDiagrams);

        TestAssertions.assertContains(html.value(), "<div class=\"mermaid-diagram\"><div class=\"mermaid-diagram-scale\"><svg><text>A</text></svg></div></div>", "Completed background rendering must embed the trusted Mermaid SVG in a scalable wrapper");
        TestAssertions.assertNotContains(html.value(), "Rendering in background...", "Completed Mermaid rendering must remove the placeholder");
    }

    @Test
    void rendersInlineCodeWithEscapedContent() {
        SafeHtml html = renderer.render("Use `<tag>` here");

        TestAssertions.assertContains(html.value(), "Use <code>&lt;tag&gt;</code> here", "inline code content must be escaped");
    }

    @Test
    void rendersHttpsMarkdownLinkAsSafeAnchor() {
        SafeHtml html = renderer.render("Read [docs](https://example.com/docs?a=1&b=2)");

        TestAssertions.assertContains(html.value(), "Read <a href=\"https://example.com/docs?a=1&amp;b=2\">docs</a>", "HTTPS Markdown link must render as safe anchor with escaped URL");
    }

    @Test
    void rendersHttpMarkdownLinkAsSafeAnchor() {
        SafeHtml html = renderer.render("Open [site](http://example.com)");

        TestAssertions.assertContains(html.value(), "Open <a href=\"http://example.com\">site</a>", "HTTP Markdown link must render as safe anchor");
    }

    @Test
    void defaultRendererKeepsRelativeMarkdownLinkAsReadableText() {
        SafeHtml html = renderer.render("Open [next](./next.md)");

        TestAssertions.assertContains(html.value(), "Open next", "Default rendering must keep relative link text readable");
        TestAssertions.assertNotContains(html.value(), "href=\"./next.md\"", "Default rendering must not expose relative links as anchors");
    }

    @Test
    void relativeLinkRenderingKeepsSafeRelativeMarkdownLinkAsAnchor() {
        SafeHtml html = renderer.render(
                "Open [next](../docs/next file.md#intro)",
                CodeHighlighting.plain(),
                MermaidRendering.plainCode(),
                RelativeLinkRendering.enabled(),
                null);

        TestAssertions.assertContains(
                html.value(),
                "Open <a href=\"https://localmd.local/__relative_markdown__?path=..%2Fdocs%2Fnext+file.md%23intro\">next</a>",
                "Enabled relative link rendering must preserve the original relative Markdown path for WebView interception");
    }

    @Test
    void renderingProfileAppliesEntitlementDerivedRelativeLinkRendering() {
        SafeHtml html = renderer.render(
                "Open [next](./next.md)",
                DocumentRenderingProfile.fromEntitlement(FeatureEntitlement.pro()),
                null);

        TestAssertions.assertContains(
                html.value(),
                "href=\"https://localmd.local/__relative_markdown__?path=.%2Fnext.md\"",
                "rendering profile must apply its relative-link decision");
    }

    @Test
    void relativeLinkRenderingRejectsAbsoluteFileUrlAsAnchor() {
        SafeHtml html = renderer.render(
                "Open [secret](file:///sdcard/secret.md)",
                CodeHighlighting.plain(),
                MermaidRendering.plainCode(),
                RelativeLinkRendering.enabled(),
                null);

        TestAssertions.assertContains(html.value(), "Open secret", "Unsafe file URL must keep readable link text");
        TestAssertions.assertNotContains(html.value(), "href=\"file://", "Unsafe file URL must not render as anchor");
    }

    @Test
    void defaultRendererKeepsRelativeMarkdownImageAsReadableAltText() {
        SafeHtml html = renderer.render("Logo ![LocalMD icon](./images/icon.png)");

        TestAssertions.assertContains(html.value(), "Logo LocalMD icon", "Default image rendering must keep relative image alt text readable");
        TestAssertions.assertNotContains(html.value(), "<img", "Default image rendering must not emit image tags for relative Markdown images");
    }

    @Test
    void relativeImageRenderingKeepsSafeRelativeMarkdownImageAsImageTag() {
        SafeHtml html = renderer.render(
                "Logo ![LocalMD icon](../images/icon light.png)",
                CodeHighlighting.plain(),
                MermaidRendering.plainCode(),
                RelativeLinkRendering.disabled(),
                RelativeImageRendering.enabled(),
                null);

        TestAssertions.assertContains(
                html.value(),
                "Logo <img src=\"https://localmd.local/__relative_image__?path=..%2Fimages%2Ficon+light.png\" alt=\"LocalMD icon\">",
                "Enabled relative image rendering must preserve the original relative image path for WebView interception");
    }

    @Test
    void relativeImageRenderingRejectsAbsoluteFileUrlAsImageTag() {
        SafeHtml html = renderer.render(
                "Secret ![private](file:///sdcard/private.png)",
                CodeHighlighting.plain(),
                MermaidRendering.plainCode(),
                RelativeLinkRendering.disabled(),
                RelativeImageRendering.enabled(),
                null);

        TestAssertions.assertContains(html.value(), "Secret private", "Unsafe image URL must keep readable alt text");
        TestAssertions.assertNotContains(html.value(), "<img", "Unsafe image URL must not render as an image tag");
    }

    @Test
    void doesNotRenderJavascriptMarkdownLinkAsAnchor() {
        SafeHtml html = renderer.render("Bad [link](javascript:alert(1))");

        TestAssertions.assertContains(html.value(), "Bad link", "unsafe Markdown link must keep readable text");
        TestAssertions.assertNotContains(html.value(), "<a href=\"javascript:", "unsafe Markdown link must not render as anchor");
    }

    @Test
    void relativeLinkRenderingProducesExactAnchorForSafeRelativeUrl() {
        // Pins the exact anchor so an off-by-one in the label/URL slice is caught.
        String html = renderWithRelativeLinks("[guide](sub/guide.md)");

        TestAssertions.assertContains(
                html,
                "<a href=\"https://localmd.local/__relative_markdown__?path=sub%2Fguide.md\">guide</a>",
                "a safe relative URL must render as an anchor wrapping exactly the label");
    }

    @Test
    void relativeLinkRenderingKeepsTextThatFollowsTheLink() {
        // The link parser must resume right after the ')', so trailing text survives
        // exactly once (guards the returned end index against a 0 / off-by-one regression).
        String html = renderWithRelativeLinks("[a](sub/a.md) and more");

        TestAssertions.assertContains(
                html,
                "<a href=\"https://localmd.local/__relative_markdown__?path=sub%2Fa.md\">a</a> and more",
                "text after a link must be rendered once, immediately after the anchor");
    }

    @Test
    void relativeLinkRenderingPreservesLiteralPlusSignsInMarkdownFileNames() {
        String html = renderWithRelativeLinks("[cpp](C++ notes.md)");

        TestAssertions.assertContains(
                html,
                "href=\"https://localmd.local/__relative_markdown__?path=C%2B%2B+notes.md\"",
                "literal plus signs in relative Markdown paths must be percent-encoded before WebView interception");
    }

    @Test
    void relativeLinkRenderingRejectsEmptyUrlAsAnchor() {
        // url.length() must be strictly > 0: an empty target is not a safe relative link.
        String html = renderWithRelativeLinks("[home]()");

        TestAssertions.assertContains(html, "<p>home</p>", "an empty URL must keep the label as readable text");
        TestAssertions.assertNotContains(html, "<a ", "an empty URL must not render as an anchor");
    }

    @Test
    void relativeLinkRenderingRejectsLeadingColonUrlAsAnchor() {
        // A ':' anywhere (here at index 0) marks a scheme-like URL, never a safe relative one.
        String html = renderWithRelativeLinks("[x](:8080/path)");

        TestAssertions.assertContains(html, "<p>x</p>", "a URL beginning with a colon must keep the label as readable text");
        TestAssertions.assertNotContains(html, "<a ", "a colon-prefixed URL must not render as an anchor");
    }

    @Test
    void relativeLinkRenderingRejectsRootAbsolutePathAsAnchor() {
        // A root-absolute path escapes the document folder, so it is not a safe relative link.
        String html = renderWithRelativeLinks("[x](/etc/secret.md)");

        TestAssertions.assertContains(html, "<p>x</p>", "a root-absolute path must keep the label as readable text");
        TestAssertions.assertNotContains(html, "href=\"/", "a root-absolute path must not render as an anchor");
    }

    @Test
    void keepsBracketTextWithoutAUrlAsLiteralText() {
        // ']' as the final character (no '(...)' follows) is not a link: keep it verbatim.
        String html = renderWithRelativeLinks("note [draft]");

        TestAssertions.assertContains(html, "note [draft]", "bracketed text with no URL must stay literal");
        TestAssertions.assertNotContains(html, "<a ", "bracketed text with no URL must not render as an anchor");
    }

    @Test
    void keepsLinkWithUnterminatedParenthesisAsLiteralText() {
        // No closing ')' means the link is incomplete and must stay literal.
        String html = renderWithRelativeLinks("see [a](sub/a.md");

        TestAssertions.assertContains(html, "see [a](sub/a.md", "a link with no closing parenthesis must stay literal");
        TestAssertions.assertNotContains(html, "<a ", "an unterminated link must not render as an anchor");
    }

    @Test
    void rendersBulletListItemsAsUnorderedList() {
        SafeHtml html = renderer.render("- first\n- second");

        TestAssertions.assertContains(html.value(), "<ul><li>first</li><li>second</li></ul>", "bullet list items must render as unordered list");
    }

    @Test
    void rendersUncheckedChecklistItemAsDisabledCheckboxInList() {
        SafeHtml html = renderer.render("- [ ] Write tests");

        TestAssertions.assertContains(html.value(), "<ul class=\"checklist\"><li><input type=\"checkbox\" disabled> Write tests</li></ul>", "unchecked checklist item must render as disabled unchecked checkbox");
    }

    @Test
    void rendersCheckedChecklistItemAsDisabledCheckedCheckboxInList() {
        SafeHtml html = renderer.render("- [x] Build APK");

        TestAssertions.assertContains(html.value(), "<ul class=\"checklist\"><li><input type=\"checkbox\" checked disabled> Build APK</li></ul>", "checked checklist item must render as disabled checked checkbox");
    }

    @Test
    void rendersNumberedListItemsAsOrderedList() {
        SafeHtml html = renderer.render("1. first\n2. second");

        TestAssertions.assertContains(html.value(), "<ol><li>first</li><li>second</li></ol>", "numbered list items must render as ordered list");
    }

    @Test
    void rendersPipeTableWithHeaderAndBodyRows() {
        SafeHtml html = renderer.render("| Name | Status |\n| --- | --- |\n| Tests | Passing |\n| APK | Built |");

        TestAssertions.assertContains(html.value(), "<div class=\"table-scroll\"><table><thead><tr><th>Name</th><th>Status</th></tr></thead><tbody><tr><td>Tests</td><td>Passing</td></tr><tr><td>APK</td><td>Built</td></tr></tbody></table></div>", "pipe table must render inside a visible horizontal scroll container");
    }

    @Test
    void escapesPipeTableCellContentAndRendersInlineCode() {
        SafeHtml html = renderer.render("| Name | Value |\n| --- | --- |\n| Tag | `<b>` & text |");

        TestAssertions.assertContains(html.value(), "<td><code>&lt;b&gt;</code> &amp; text</td>", "table cell content must be escaped and inline code must render safely");
        TestAssertions.assertNotContains(html.value(), "<b>", "raw HTML from table cell must not be emitted");
    }

    @Test
    void rendersBlockquoteAsBlockquote() {
        SafeHtml html = renderer.render("> quoted <text>");

        TestAssertions.assertContains(html.value(), "<blockquote>quoted &lt;text&gt;</blockquote>", "blockquote content must render escaped inside blockquote");
    }

    @Test
    void rendersHorizontalRule() {
        SafeHtml html = renderer.render("---");

        TestAssertions.assertContains(html.value(), "<hr>", "horizontal rule must render as hr");
    }

    @Test
    void rendererDoesNotCrashOnEmptyInput() {
        SafeHtml html = renderer.render("");

        TestAssertions.assertEquals("", html.value(), "empty Markdown input must render as an empty safe HTML fragment");
    }

    @Test
    void rendererDoesNotCrashOnNullInput() {
        SafeHtml html = renderer.render(null);

        TestAssertions.assertEquals("", html.value(), "null Markdown input must be handled as empty input");
    }

    @Test
    void rendererClosesUnterminatedCodeFenceInsteadOfCrashing() {
        SafeHtml html = renderer.render("```text\nvalue < 3");

        TestAssertions.assertContains(html.value(), "<pre><code", "unterminated fenced code block must still open a code block");
        TestAssertions.assertContains(html.value(), "value &lt; 3", "unterminated fenced code block content must be escaped");
        TestAssertions.assertContains(html.value(), "</code></pre>", "unterminated fenced code block must be closed at end of document");
    }

    @Test
    void rendererDoesNotCrashOnGeneratedMixedMarkdownText() {
        SafeHtml html = renderer.render(generatedMixedMarkdownText());

        TestAssertions.assertNotNull(html.value(), "generated mixed Markdown must always produce a safe HTML fragment");
        TestAssertions.assertNotContains(html.value(), "<script>", "generated mixed Markdown must not emit raw script tags");
    }

    // Fence detection must match the TOC heading extractor (MarkdownHeadings): a hash line
    // inside an indented (<=3 space) fence is code, not a heading, so the page and the
    // TOC/jump list agree. (issue #168 — Codex review on the TOC-only fix)
    @Test
    void indentedFenceHashLineIsNotRenderedAsHeading() {
        SafeHtml html = renderer.render("# Real\n   ```\n# Not heading\n   ```\n## After");

        TestAssertions.assertContains(html.value(), "<h1 id=\"real\">Real</h1>", "real heading before the indented fence renders");
        TestAssertions.assertContains(html.value(), "<h2 id=\"after\">After</h2>", "heading after the indented fence renders");
        TestAssertions.assertFalse(html.value().contains(">Not heading</h1>"),
                "a hash line inside an indented fence must not render as a heading");
    }

    private String renderWithRelativeLinks(String markdown) {
        return renderer.render(markdown, CodeHighlighting.plain(), MermaidRendering.plainCode(),
                RelativeLinkRendering.enabled(), null).value();
    }

    private static String generatedMixedMarkdownText() {
        return "# ## heading\n"
                + "- item\n"
                + "- [x] done\n"
                + "1. ordered\n"
                + "| a | b |\n"
                + "| --- | --- |\n"
                + "| `<b>` | [label](https://example.com) |\n"
                + "[bad](javascript:alert(1)) <script> &\n"
                + "```\n"
                + "if (a < b) return true;\n"
                + "```\n"
                + "> quote\n"
                + "---\n"
                + "`code`\n";
    }
}
