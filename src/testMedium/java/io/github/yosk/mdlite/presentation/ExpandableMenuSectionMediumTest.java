package io.github.yosk.mdlite.presentation;

import static org.junit.Assert.assertEquals;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public final class ExpandableMenuSectionMediumTest {
    @Test
    public void togglingCollapsedSectionRendersAndShowsItsContent() {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        LinearLayout panel = hiddenPanel(activity);
        CountingAction renderContent = new CountingAction();
        ExpandableMenuSection section = new ExpandableMenuSection(panel, renderContent);

        section.toggle();

        assertEquals("expanded content must be rendered once", 1, renderContent.count);
        assertEquals("expanded section must be visible", View.VISIBLE, panel.getVisibility());
    }

    @Test
    public void togglingExpandedSectionHidesWithoutRenderingAgain() {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        LinearLayout panel = hiddenPanel(activity);
        CountingAction renderContent = new CountingAction();
        ExpandableMenuSection section = new ExpandableMenuSection(panel, renderContent);
        section.toggle();

        section.toggle();

        assertEquals("collapsing must not rerender hidden content", 1, renderContent.count);
        assertEquals("collapsed section must be gone", View.GONE, panel.getVisibility());
    }

    @Test
    public void refreshingAnExpandedSectionRendersCurrentContentAgain() {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        CountingAction renderContent = new CountingAction();
        ExpandableMenuSection section = new ExpandableMenuSection(
                hiddenPanel(activity), renderContent);
        section.toggle();

        section.refreshExpandedContent();

        assertEquals("an expanded section must refresh content when its owner reopens",
                2, renderContent.count);
    }

    private static LinearLayout hiddenPanel(Activity activity) {
        LinearLayout panel = new LinearLayout(activity);
        panel.setVisibility(View.GONE);
        return panel;
    }

    private static final class CountingAction implements Runnable {
        private int count;

        @Override
        public void run() {
            count++;
        }
    }
}
