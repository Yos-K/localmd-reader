package io.github.yosk.mdlite.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;

import io.github.yosk.mdlite.viewer.ViewerTheme;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

/**
 * Medium-tier test (#75): button backgrounds must give ripple touch feedback
 * while keeping the existing rounded surface (fill, corner radius) unchanged.
 *
 * makeRoundedBackground is the single factory behind every button background
 * (toolbar, menu, tabs, TOC items, Pro dialog), so asserting the factory
 * covers all of them; the search input intentionally stays plain via
 * makePlainRoundedBackground (a text field must not ripple).
 */
@RunWith(RobolectricTestRunner.class)
public class MainActivityRippleMediumTest {
    private static final int FILL = 0xffeef5f3;
    private static final int STROKE = 0xffc9d8d5;

    @Test
    public void roundedBackgroundsGainARippleWrap() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();

        Drawable background = activity.makeRoundedBackground(FILL, STROKE, 8);

        assertTrue("button backgrounds must provide ripple touch feedback",
                background instanceof RippleDrawable);
    }

    @Test
    public void rippleKeepsTheRoundedSurfaceUnchanged() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();

        RippleDrawable background = (RippleDrawable) activity.makeRoundedBackground(FILL, STROKE, 8);
        Drawable content = background.getDrawable(0);

        assertTrue("the ripple must wrap the original rounded surface",
                content instanceof GradientDrawable);
        assertEquals("the fill color must stay exactly as requested",
                FILL, ((GradientDrawable) content).getColor().getDefaultColor());
        assertEquals("the corner radius must stay at the requested dp",
                (float) activity.dp(8), ((GradientDrawable) content).getCornerRadius(), 0.01f);
    }

    @Test
    public void rippleColorFollowsTheThemePrimary() {
        // RippleDrawable exposes no public getter for its constructor color
        // (getEffectColor() is an unrelated API 29 concept), so the color
        // decision is asserted directly; makeRoundedBackground wires it in.
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();
        int lightPrimary = activity.primaryColor();

        activity.applySelectedTheme(ViewerTheme.dark());
        int darkPrimary = activity.primaryColor();

        assertEquals("dark theme ripple must derive from the dark primary at 20% alpha",
                (darkPrimary & 0x00ffffff) | 0x33000000, activity.rippleColor());
        assertTrue("the ripple color must actually change with the theme",
                ((lightPrimary & 0x00ffffff) | 0x33000000) != activity.rippleColor());
    }

    @Test
    public void textInputBackgroundsStayPlain() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();

        Drawable background = activity.makePlainRoundedBackground(FILL, STROKE, 8);

        assertTrue("text fields must keep a plain (non-ripple) rounded background",
                background instanceof GradientDrawable);
    }

    @Test
    public void sectionCardsDoNotRippleButMenuRowsDo() {
        // #77 grouped-list pattern: the section card is a passive container,
        // the rows inside it carry the touch feedback.
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();

        assertTrue("section cards must stay plain (non-interactive surface)",
                activity.makePlainTonalBackground(FILL, 12) instanceof GradientDrawable);
        assertTrue("menu rows must provide ripple feedback without their own surface",
                activity.makeRowRippleBackground() instanceof RippleDrawable);
    }

    @Test
    public void activeTabIsAFilledPrimaryPillAndInactiveTabsAreTonalPills() {
        // #77 pill tabs: selection reads from fill contrast, not a border.
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();

        RippleDrawable pill = (RippleDrawable) activity.makeTonalBackground(
                activity.primaryColor(), MainActivity.PILL_RADIUS_DP);
        GradientDrawable surface = (GradientDrawable) pill.getDrawable(0);

        assertEquals("the active pill must be filled with the theme primary",
                activity.primaryColor(), surface.getColor().getDefaultColor());
        assertEquals("the pill radius must far exceed any button half-height",
                (float) activity.dp(MainActivity.PILL_RADIUS_DP), surface.getCornerRadius(), 0.01f);
    }

    @Test
    public void tonalBackgroundsKeepFillAndRadiusAndStillRipple() {
        // #77: panel buttons separate by fill contrast alone (borderless),
        // but must keep the same ripple feedback and rounded geometry.
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();

        Drawable background = activity.makeTonalBackground(FILL, 8);

        assertTrue("tonal surfaces must still provide ripple touch feedback",
                background instanceof RippleDrawable);
        Drawable content = ((RippleDrawable) background).getDrawable(0);
        assertTrue("the ripple must wrap a rounded tonal surface",
                content instanceof GradientDrawable);
        assertEquals("the tonal fill color must stay exactly as requested",
                FILL, ((GradientDrawable) content).getColor().getDefaultColor());
        assertEquals("the tonal corner radius must stay at the requested dp",
                (float) activity.dp(8), ((GradientDrawable) content).getCornerRadius(), 0.01f);
    }
}
