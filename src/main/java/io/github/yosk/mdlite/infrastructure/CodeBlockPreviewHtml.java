package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.SafeHtml;

final class CodeBlockPreviewHtml {
    private CodeBlockPreviewHtml() {
    }

    static SafeHtml from(int index, SafeHtml raw, SafeHtml preview) {
        if (index < 0 || raw == null || preview == null) {
            throw new IllegalArgumentException("code preview requires an index and safe panes");
        }
        String rawId = "code-preview-" + index + "-raw";
        String previewId = "code-preview-" + index + "-preview";
        return SafeHtml.fromTrustedRendererOutput("<div class=\"code-preview-toggle\">"
                + "<input class=\"code-preview-radio code-preview-raw-radio\" type=\"radio\" name=\"code-preview-"
                + index + "\" id=\"" + rawId + "\" checked>"
                + "<label class=\"code-preview-label\" for=\"" + rawId + "\">Raw</label>"
                + "<input class=\"code-preview-radio code-preview-preview-radio\" type=\"radio\" name=\"code-preview-"
                + index + "\" id=\"" + previewId + "\">"
                + "<label class=\"code-preview-label\" for=\"" + previewId + "\">Preview</label>"
                + "<div class=\"code-preview-pane code-preview-raw\">" + raw.value() + "</div>"
                + "<div class=\"code-preview-pane code-preview-rendered\">" + preview.value() + "</div>"
                + "</div>");
    }
}
