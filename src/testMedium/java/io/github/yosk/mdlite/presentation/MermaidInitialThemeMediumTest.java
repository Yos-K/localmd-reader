package io.github.yosk.mdlite.presentation;

import static org.junit.Assert.assertTrue;

import io.github.yosk.mdlite.viewer.ViewerTheme;
import io.github.yosk.mdlite.viewer.ViewerThemeStyle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Medium-tier test (#73): the mermaid.initialize call baked into the render
 * page must take its colors from the shared theme style, not duplicated
 * literals. The expectations are read from ViewerThemeStyle at test time, so
 * a palette change cannot silently diverge from the baked-in default again.
 */
@RunWith(RobolectricTestRunner.class)
public class MermaidInitialThemeMediumTest {

    @Test
    public void initialMermaidColorsComeFromTheSharedThemeStyle() {
        ViewerThemeStyle style = ViewerThemeStyle.from(ViewerTheme.light());

        String script = MermaidJsRenderEngine.initialThemeScript(
                MermaidDiagramTheme.from(ViewerTheme.light()));

        assertTrue("the baked-in initialize call must be well-formed",
                script.startsWith("mermaid.initialize(localMdMermaidConfig("));
        assertTrue("diagram background must come from the shared theme style",
                script.contains("\"" + style.diagramBackground + "\""));
        assertTrue("diagram text color must come from the shared theme style",
                script.contains("\"" + style.diagramText + "\""));
        assertTrue("diagram line color must come from the shared theme style",
                script.contains("\"" + style.diagramLine + "\""));
        assertTrue("diagram primary must come from the shared theme style",
                script.contains("\"" + style.diagramPrimary + "\""));
        assertTrue("diagram secondary must come from the shared theme style",
                script.contains("\"" + style.diagramSecondary + "\""));
    }
}
