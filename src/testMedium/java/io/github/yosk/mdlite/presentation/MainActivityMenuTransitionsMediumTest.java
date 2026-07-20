package io.github.yosk.mdlite.presentation;

import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

import android.os.Looper;
import android.view.View;

import java.time.Duration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

/**
 * Medium-tier test (#76): the menu open/close transition must end in a
 * consistent state — including under rapid toggling, the acceptance condition
 * from the issue. Animations run on the Robolectric main looper, so idling it
 * past the transition duration deterministically completes them.
 */
@RunWith(RobolectricTestRunner.class)
public class MainActivityMenuTransitionsMediumTest {

    @Test
    public void openingShowsThePanelAndTheScrim() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();

        activity.openMenu();
        settle();

        assertEquals("menu panel must be visible after opening",
                View.VISIBLE, activity.menuScrollContainer.getVisibility());
        assertEquals("panel must finish fully slid in",
                0f, activity.menuScrollContainer.getTranslationX(), 0.01f);
        assertEquals("scrim must dim the content behind the menu",
                View.VISIBLE, activity.menuScrim.getVisibility());
    }

    @Test
    public void closingHidesThePanelAndTheScrim() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();
        activity.openMenu();
        settle();

        activity.closeMenu();
        settle();

        assertEquals("menu panel must be gone after closing",
                View.GONE, activity.menuScrollContainer.getVisibility());
        assertEquals("scrim must be gone after closing",
                View.GONE, activity.menuScrim.getVisibility());
    }

    @Test
    public void rapidTogglingEndsInTheLastRequestedState() {
        // Acceptance condition from #76: hammering open/close must not corrupt
        // state. The transition retargets from current values, so the final
        // call wins.
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();

        activity.openMenu();
        activity.closeMenu();
        activity.openMenu();
        settle();

        assertEquals("the last request (open) must win after rapid toggling",
                View.VISIBLE, activity.menuScrollContainer.getVisibility());
        assertEquals("panel must finish fully slid in after rapid toggling",
                0f, activity.menuScrollContainer.getTranslationX(), 0.01f);
    }

    private static void settle() {
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(600));
    }
}
