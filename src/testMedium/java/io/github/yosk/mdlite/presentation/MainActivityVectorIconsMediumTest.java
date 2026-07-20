package io.github.yosk.mdlite.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Medium-tier test (#73): the toolbar and tab chrome must use vector icons
 * instead of text glyphs. Wiring-level assertions; the visual result is
 * reviewed through the theme-screenshots artifact.
 */
@RunWith(RobolectricTestRunner.class)
public class MainActivityVectorIconsMediumTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void menuButtonCarriesAVectorIconInsteadOfTheGlyph() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();

        assertFalse("the hamburger glyph must be gone from the menu button label",
                activity.menuButton.getText().toString().contains("☰"));
        assertNotNull("the menu button must carry a start vector icon (#73)",
                activity.menuButton.getCompoundDrawablesRelative()[0]);
    }

    @Test
    public void tabCloseControlCarriesAVectorIconInsteadOfTheGlyph() throws IOException {
        File markdown = folder.newFile("doc.md");
        Files.write(markdown.toPath(), "# Doc\n".getBytes(StandardCharsets.UTF_8));
        MainActivity activity = Robolectric.buildActivity(
                MainActivity.class, new Intent(Intent.ACTION_VIEW, Uri.fromFile(markdown))).setup().get();
        shadowOf(Looper.getMainLooper()).idle();

        ViewGroup documentGroup = (ViewGroup) activity.tabRow.getChildAt(1);
        TextView close = (TextView) documentGroup.getChildAt(1);

        assertEquals("the × glyph must be gone from the close control",
                "", close.getText().toString());
        assertNotNull("the close control must carry a vector icon (#73)",
                close.getCompoundDrawablesRelative()[0]);
        assertNotNull("the close control must keep its accessible description",
                close.getContentDescription());
    }
}
