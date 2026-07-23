package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.testing.TestAssertions;
import io.github.yosk.mdlite.viewer.ViewerTheme;
import org.junit.jupiter.api.Test;

public final class HtmlPageBuilderCodePreviewTest {
    @Test
    void selectedCodePreviewPaneIsTheOnlyVisiblePane() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<div class=\"code-preview-toggle\"></div>"),
                ViewerTheme.light());

        TestAssertions.assertContains(page, ".code-preview-pane{display:none;}",
                "code preview panes must remain hidden until their option is selected");
    }
}
