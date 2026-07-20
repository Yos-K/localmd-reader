package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ViewerLanguageTest {

    @Test
    void fromStoredValueRestoresJapanese() {
        ViewerLanguage language = ViewerLanguage.fromStoredValue("ja");

        TestAssertions.assertEquals("ja", language.storedValue(), "stored Japanese language value must restore Japanese");
    }

    @Test
    void fromStoredValueDefaultsToEnglishForUnknownValue() {
        ViewerLanguage language = ViewerLanguage.fromStoredValue("unknown");

        TestAssertions.assertEquals("en", language.storedValue(), "unknown language value must fall back to English");
    }

    @Test
    void toggledSwitchesEnglishAndJapanese() {
        ViewerLanguage japanese = ViewerLanguage.english().toggled();
        ViewerLanguage english = japanese.toggled();

        TestAssertions.assertEquals("ja", japanese.storedValue(), "toggling English must switch to Japanese");
        TestAssertions.assertEquals("en", english.storedValue(), "toggling Japanese must switch to English");
    }
}
