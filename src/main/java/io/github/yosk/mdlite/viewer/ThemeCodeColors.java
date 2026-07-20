package io.github.yosk.mdlite.viewer;

/**
 * Highlighted code, on its own background token. One of the five semantic groups that replaced the
 * 29-positional-argument ViewerThemeStyle constructor (issue #71): a swap
 * across groups is now a compile error instead of a silent color bug.
 */
final class ThemeCodeColors {
    final String codeBackground;
    final String codeKeyword;
    final String codeLiteral;
    final String codeString;
    final String codeCommand;

    ThemeCodeColors(
            String codeBackground,
            String codeKeyword,
            String codeLiteral,
            String codeString,
            String codeCommand) {
        this.codeBackground = codeBackground;
        this.codeKeyword = codeKeyword;
        this.codeLiteral = codeLiteral;
        this.codeString = codeString;
        this.codeCommand = codeCommand;
    }
}
