package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.SafeHtml;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

/**
 * Property-based XSS safety tests for {@link JavaSimpleMarkdownRenderer}.
 *
 * Encodes the security invariant that no matter what Markdown source we feed
 * the renderer, the resulting SafeHtml must not emit any <em>raw</em> unsafe
 * HTML that would execute as JavaScript when displayed in a WebView. This
 * complements the hard constraint in AGENTS.md that the WebView has JavaScript
 * disabled by adding a *defense in depth*: even if JS were ever re-enabled,
 * the renderer's output should still be safe.
 *
 * The invariant is stated as "no raw unsafe HTML is emitted", not "the literal
 * substring never appears". The renderer escapes hostile input rather than
 * dropping it, so escaped text such as {@code &lt;img ... onerror=...&gt;} is
 * explicitly allowed: it renders as visible text and cannot execute. Only a
 * live tag or attribute (an unescaped {@code <script}, {@code href="javascript:},
 * etc.) constitutes a breach.
 *
 * The arbitraries deliberately bias toward payloads that contain script-like
 * fragments so that shrinking can find the smallest input that breaks the
 * invariant if one ever exists.
 */
public final class MarkdownRendererXssProperty {

    private final JavaSimpleMarkdownRenderer renderer = new JavaSimpleMarkdownRenderer();

    @Property
    boolean outputNeverContainsRawScriptOpenTag(@ForAll("markdownPayloads") String markdown) {
        SafeHtml result = renderer.render(markdown);
        return !containsCaseInsensitive(result.value(), "<script");
    }

    @Property
    boolean outputNeverContainsRawIframeOpenTag(@ForAll("markdownPayloads") String markdown) {
        SafeHtml result = renderer.render(markdown);
        return !containsCaseInsensitive(result.value(), "<iframe");
    }

    @Property
    boolean outputNeverEmitsRawJavascriptUrlAsAnchorHref(@ForAll("markdownPayloads") String markdown) {
        // A breach is a live href pointing at a javascript: URL. Escaped text
        // like &lt;a href=&quot;javascript:...&gt; is safe and therefore allowed.
        String lower = renderer.render(markdown).value().toLowerCase();
        return !lower.contains("href=\"javascript:")
                && !lower.contains("href='javascript:")
                && !lower.contains("href=javascript:");
    }

    @Property
    boolean outputNeverEmitsRawDangerousHtmlTag(@ForAll("markdownPayloads") String markdown) {
        // The renderer only ever emits a fixed set of safe structural tags and
        // escapes everything from the source. None of these executable tags are
        // ever generated, so their presence (unescaped) would mean source HTML
        // leaked through. Escaped forms like &lt;img...&gt; remain allowed.
        String lower = renderer.render(markdown).value().toLowerCase();
        return !lower.contains("<img")
                && !lower.contains("<svg")
                && !lower.contains("<object")
                && !lower.contains("<embed");
    }

    @Property
    boolean nullInputIsHandledAsEmpty(@ForAll boolean unused) {
        SafeHtml result = renderer.render(null);
        return result != null && result.value() != null;
    }

    @Provide
    Arbitrary<String> markdownPayloads() {
        // Bias toward dangerous fragments so the shrinker can localize a breach
        // quickly if one exists. The arbitrary still covers ordinary Markdown.
        Arbitrary<String> dangerousFragments = Arbitraries.of(
                "<script>alert(1)</script>",
                "<SCRIPT>alert(1)</SCRIPT>",
                "<iframe src=\"https://evil\"></iframe>",
                "[link](javascript:alert(1))",
                "<a href=\"javascript:alert(1)\">x</a>",
                "<img src=x onerror=alert(1)>",
                "<svg onload=alert(1)>",
                "&lt;script&gt;",
                "```html\n<script>alert(1)</script>\n```",
                "# Heading <script>evil()</script>",
                "> Quote <img onerror=alert(1) src=x>");
        Arbitrary<String> ordinaryText = Arbitraries.strings()
                .ascii()
                .ofMinLength(0)
                .ofMaxLength(200);
        return Arbitraries.oneOf(dangerousFragments, ordinaryText);
    }

    private static boolean containsCaseInsensitive(String haystack, String needle) {
        return haystack.toLowerCase().contains(needle.toLowerCase());
    }
}
