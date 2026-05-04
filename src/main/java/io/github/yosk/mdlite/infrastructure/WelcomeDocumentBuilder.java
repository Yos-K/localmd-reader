package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.domain.ViewerLanguage;

public final class WelcomeDocumentBuilder {
    public static final String OPEN_MARKDOWN_URL = "localmd://open-markdown";

    private WelcomeDocumentBuilder() {
    }

    public static SafeHtml build(ViewerLanguage language) {
        ViewerLanguage safeLanguage = language == null ? ViewerLanguage.english() : language;
        return SafeHtml.fromTrustedRendererOutput(safeLanguage.isJapanese() ? japaneseHtml() : englishHtml());
    }

    private static String japaneseHtml() {
        return "<section class=\"welcome\">"
                + "<p class=\"welcome-kicker\">ローカルMarkdownビューア</p>"
                + "<h1>LocalMD Reader</h1>"
                + "<p class=\"welcome-lead\">広告、トラッキング、ログイン、ネットワークアクセスなしでMarkdownファイルを読みます。</p>"
                + "<a class=\"welcome-primary-action\" href=\"" + OPEN_MARKDOWN_URL + "\">Markdownファイルを開く</a>"
                + "<div class=\"welcome-grid\">"
                + "<div class=\"welcome-card\"><strong>戻る</strong><span>最近開いたファイルには、この端末で開いた直近5件が残ります。</span></div>"
                + "<div class=\"welcome-card\"><strong>読む</strong><span>ピンチで文字サイズを変えられます。複数ファイルはタブで開きます。</span></div>"
                + "</div>"
                + "<p class=\"welcome-note\">左上のメニュー、または左端から右へのスワイプでもファイルを開けます。生HTMLはテキストとして表示します。</p>"
                + "</section>";
    }

    private static String englishHtml() {
        return "<section class=\"welcome\">"
                + "<p class=\"welcome-kicker\">Local Markdown reader</p>"
                + "<h1>LocalMD Reader</h1>"
                + "<p class=\"welcome-lead\">Open a Markdown file and read it without ads, tracking, login, or network access.</p>"
                + "<a class=\"welcome-primary-action\" href=\"" + OPEN_MARKDOWN_URL + "\">Open Markdown file</a>"
                + "<div class=\"welcome-grid\">"
                + "<div class=\"welcome-card\"><strong>Return</strong><span>Recent files keeps the last 5 documents on this device.</span></div>"
                + "<div class=\"welcome-card\"><strong>Read</strong><span>Pinch to adjust text size. Open more files to create tabs.</span></div>"
                + "</div>"
                + "<p class=\"welcome-note\">You can also open files from the top-left menu or by swiping right from the left edge. Raw HTML is shown as text.</p>"
                + "</section>";
    }
}
