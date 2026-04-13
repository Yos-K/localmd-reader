package io.github.yosk.mdlite.domain;

public final class FontSize {
    public static final int MIN_SP = 12;
    public static final int MAX_SP = 28;
    public static final int DEFAULT_SP = 16;

    private final int sp;

    private FontSize(int sp) {
        if (sp < MIN_SP || sp > MAX_SP) {
            throw new IllegalArgumentException("font size out of range");
        }
        this.sp = sp;
    }

    public static FontSize defaultSize() {
        return new FontSize(DEFAULT_SP);
    }

    public static FontSize of(int sp) {
        return new FontSize(sp);
    }

    public int sp() {
        return sp;
    }

    public FontSize increased() {
        if (sp >= MAX_SP) {
            return this;
        }
        return new FontSize(sp + 1);
    }

    public FontSize decreased() {
        if (sp <= MIN_SP) {
            return this;
        }
        return new FontSize(sp - 1);
    }
}
