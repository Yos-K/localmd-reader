package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.SafeHtml;

final class CodeBlockPreviewHtml {
    private CodeBlockPreviewHtml() {
    }

    static SafeHtml from(int index, SafeHtml raw, SafeHtml preview) {
        if (index < 0 || raw == null || preview == null) {
            throw new IllegalArgumentException("code preview requires an index and safe panes");
        }
        return SafeHtml.fromTrustedRendererOutput("<div class=\"code-preview-toggle\">"
                + "<button type=\"button\" class=\"code-preview-button code-preview-button-active\""
                + " onclick=\"localmdCodePreview(this,'raw')\">Raw</button>"
                + "<button type=\"button\" class=\"code-preview-button\""
                + " onclick=\"localmdCodePreview(this,'rendered')\">Preview</button>"
                + "<div class=\"code-preview-pane code-preview-raw\" style=\"display:block\">" + raw.value() + "</div>"
                + "<div class=\"code-preview-pane code-preview-rendered\" style=\"display:none\">" + preview.value() + "</div>"
                + "</div>");
    }
}
