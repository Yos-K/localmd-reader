package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class SafeHtmlCodePreviewTest {
    @Test
    void allowedHeadingDropsEveryAttribute() {
        TestAssertions.assertEquals("<h1>Title</h1>",
                SafeHtmlCodePreview.from("<h1 class=\"title\" onclick=\"bad()\">Title</h1>").value(),
                "HTML preview must preserve allowed structure without attributes");
    }

    @Test
    void scriptElementRemainsEscapedText() {
        TestAssertions.assertEquals("&lt;script&gt;bad()&lt;/script&gt;",
                SafeHtmlCodePreview.from("<script>bad()</script>").value(),
                "HTML preview must never emit executable script elements");
    }

    @Test
    void plainTextIsHtmlEscaped() {
        TestAssertions.assertEquals("a &amp; b", SafeHtmlCodePreview.from("a & b").value(),
                "plain preview text must remain safe HTML");
    }
}
