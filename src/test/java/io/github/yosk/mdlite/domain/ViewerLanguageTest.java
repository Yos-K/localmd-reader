package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;

public final class ViewerLanguageTest {
    public static void main(String[] args) {
        ViewerLanguageTest test = new ViewerLanguageTest();
        test.fromStoredValueRestoresJapanese();
        test.fromStoredValueDefaultsToEnglishForUnknownValue();
        test.toggledSwitchesEnglishAndJapanese();
    }

    public void fromStoredValueRestoresJapanese() {
        ViewerLanguage language = ViewerLanguage.fromStoredValue("ja");

        TestAssertions.assertEquals("ja", language.storedValue(), "stored Japanese language value must restore Japanese");
    }

    public void fromStoredValueDefaultsToEnglishForUnknownValue() {
        ViewerLanguage language = ViewerLanguage.fromStoredValue("unknown");

        TestAssertions.assertEquals("en", language.storedValue(), "unknown language value must fall back to English");
    }

    public void toggledSwitchesEnglishAndJapanese() {
        ViewerLanguage japanese = ViewerLanguage.english().toggled();
        ViewerLanguage english = japanese.toggled();

        TestAssertions.assertEquals("ja", japanese.storedValue(), "toggling English must switch to Japanese");
        TestAssertions.assertEquals("en", english.storedValue(), "toggling Japanese must switch to English");
    }
}
