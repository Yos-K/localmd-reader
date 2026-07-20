package io.github.yosk.mdlite.presentation;

import static org.junit.Assert.assertFalse;

import android.webkit.WebSettings;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

/**
 * Medium-tier test: assert the reader WebView's locked-down configuration as
 * behaviour, on the JVM (Robolectric), without an emulator.
 *
 * The privacy/safety promise (the reader WebView never runs JavaScript) is a
 * Hard Constraint already guarded at source level by
 * scripts/check-hard-constraints.sh (a grep). This complements that grep with a
 * behavioural assertion: it builds the real MainActivity and reads the settings
 * off the actual reader WebView, so a regression is caught even if it slips past
 * the textual check. See docs/harness/test-strategy.md.
 *
 * Note: MainActivity uses a separate, JavaScript-enabled WebView for offline
 * Mermaid rendering (MermaidJsRenderEngine); this test only inspects the main
 * reader WebView (the field MainActivity.webView).
 */
@RunWith(RobolectricTestRunner.class)
public class MainActivityReaderWebViewMediumTest {

    @Test
    public void readerWebViewKeepsJavaScriptAndStorageDisabled() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();

        WebSettings settings = activity.webView.getSettings();

        assertFalse("reader WebView must keep JavaScript disabled (Hard Constraint)",
                settings.getJavaScriptEnabled());
        assertFalse("reader WebView must keep DOM storage disabled",
                settings.getDomStorageEnabled());
        assertFalse("reader WebView must keep file access disabled",
                settings.getAllowFileAccess());
        assertFalse("reader WebView must keep content access disabled",
                settings.getAllowContentAccess());
    }
}
