package io.github.yosk.mdlite.domain;

public final class ViewerLanguage {
    public static final String ENGLISH_VALUE = "en";
    public static final String JAPANESE_VALUE = "ja";

    private final String storedValue;

    private ViewerLanguage(String storedValue) {
        this.storedValue = storedValue;
    }

    public static ViewerLanguage english() {
        return new ViewerLanguage(ENGLISH_VALUE);
    }

    public static ViewerLanguage japanese() {
        return new ViewerLanguage(JAPANESE_VALUE);
    }

    public static ViewerLanguage fromStoredValue(String value) {
        if (JAPANESE_VALUE.equals(value)) {
            return japanese();
        }
        return english();
    }

    public ViewerLanguage toggled() {
        return isJapanese() ? english() : japanese();
    }

    public boolean isJapanese() {
        return JAPANESE_VALUE.equals(storedValue);
    }

    public String storedValue() {
        return storedValue;
    }
}
