package io.github.yosk.mdlite.domain;

public final class SafeHtml {
    private final String value;

    private SafeHtml(String value) {
        if (value == null) {
            throw new IllegalArgumentException("safe html value must not be null");
        }
        this.value = value;
    }

    public static SafeHtml fromTrustedRendererOutput(String value) {
        return new SafeHtml(value);
    }

    public String value() {
        return value;
    }
}
