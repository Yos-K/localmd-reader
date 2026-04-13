package io.github.yosk.mdlite.domain;

public final class ViewerLanguageTest {
    public static void main(String[] args) {
        ViewerLanguageTest test = new ViewerLanguageTest();
        test.fromStoredValueRestoresJapanese();
        test.fromStoredValueDefaultsToEnglishForUnknownValue();
        test.toggledSwitchesEnglishAndJapanese();
    }

    public void fromStoredValueRestoresJapanese() {
        ViewerLanguage language = ViewerLanguage.fromStoredValue("ja");

        assertEquals("ja", language.storedValue(), "stored Japanese language value must restore Japanese");
    }

    public void fromStoredValueDefaultsToEnglishForUnknownValue() {
        ViewerLanguage language = ViewerLanguage.fromStoredValue("unknown");

        assertEquals("en", language.storedValue(), "unknown language value must fall back to English");
    }

    public void toggledSwitchesEnglishAndJapanese() {
        ViewerLanguage japanese = ViewerLanguage.english().toggled();
        ViewerLanguage english = japanese.toggled();

        assertEquals("ja", japanese.storedValue(), "toggling English must switch to Japanese");
        assertEquals("en", english.storedValue(), "toggling Japanese must switch to English");
    }

    private static void assertEquals(String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + "\nExpected: " + expected + "\nActual: " + actual);
        }
    }
}
