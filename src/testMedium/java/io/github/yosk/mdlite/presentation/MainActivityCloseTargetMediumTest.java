package io.github.yosk.mdlite.presentation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

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
 * Medium-tier test (#73): tab close controls must offer a 48dp touch target
 * while staying visually small. The delegate lives on the tab row (a tab
 * group is shorter than 48dp, so expansion beyond it would be dead there —
 * review finding on PR #94); these tests pin the wiring (the row carries a
 * delegate only when closable tabs exist) and the geometry, including a touch
 * vertically outside the tab group but inside the expanded 48px area.
 */
@RunWith(RobolectricTestRunner.class)
public class MainActivityCloseTargetMediumTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void tabRowCarriesADelegateOnlyWhenClosableTabsExist() throws IOException {
        File markdown = folder.newFile("doc.md");
        Files.write(markdown.toPath(), "# Doc\n".getBytes(StandardCharsets.UTF_8));

        MainActivity welcomeOnly = Robolectric.buildActivity(MainActivity.class).setup().get();
        shadowOf(Looper.getMainLooper()).idle();
        assertNull("with only the welcome tab there is nothing to close, so no delegate",
                welcomeOnly.tabRow.getTouchDelegate());

        MainActivity withDocument = Robolectric.buildActivity(
                MainActivity.class, new Intent(Intent.ACTION_VIEW, Uri.fromFile(markdown))).setup().get();
        shadowOf(Looper.getMainLooper()).idle();
        assertNotNull("a closable tab must expand its close control's hit area on the row (#73)",
                withDocument.tabRow.getTouchDelegate());
    }

    @Test
    public void hitAreaCoversTheMinimumEvenOutsideTheTabGroup() {
        // Row 60px tall; the tab group inside is only 30px tall, ending at
        // y=30. The close control (20x20 at x 40..60 within the group) must
        // accept touches up to the 48px expansion: x in [36, 84], y in [-9, 39]
        // relative to the row, including y=35 which is BELOW the tab group.
        // The production walker collects CloseTabText instances specifically,
        // so the probe must be the real control class.
        android.app.Activity host = Robolectric.buildActivity(android.app.Activity.class).setup().get();
        LinearLayout row = new LinearLayout(host);
        FrameLayout group = new FrameLayout(host);
        CloseTabText close = new CloseTabText(host, 0);
        close.setClickable(true); // the real close control carries an OnClickListener
        group.addView(close);
        row.addView(group);
        row.layout(0, 0, 400, 60);
        group.layout(0, 0, 100, 30);
        close.layout(40, 5, 60, 25);

        new CloseTabTouchTargets(row, 48).run();

        assertNotNull(row.getTouchDelegate());
        assertTrue("a touch below the tab group but inside the 48px expansion must hit the close control",
                row.getTouchDelegate().onTouchEvent(downEventAt(50f, 35f)));
        assertTrue("a touch left of the drawn bounds but inside the expansion must hit",
                row.getTouchDelegate().onTouchEvent(downEventAt(37f, 15f)));
        assertFalse("a touch beyond the expanded area must not hit the close control",
                row.getTouchDelegate().onTouchEvent(downEventAt(100f, 15f)));
    }

    private static MotionEvent downEventAt(float x, float y) {
        return MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, x, y, 0);
    }
}
