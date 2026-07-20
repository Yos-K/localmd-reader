package io.github.yosk.mdlite.presentation;

import android.webkit.JavascriptInterface;

final class MermaidJsBridge {
    private final MermaidJsRenderEngine engine;

    MermaidJsBridge(MermaidJsRenderEngine engine) {
        this.engine = engine;
    }

    @JavascriptInterface
    public void ready() {
        engine.notifyReady();
    }

    @JavascriptInterface
    public void rendered(String documentUri, int diagramIndex, String svg) {
        engine.notifyRendered(documentUri, diagramIndex, svg);
    }

    @JavascriptInterface
    public void failed(String documentUri, int diagramIndex, String reason) {
        engine.notifyFailed(documentUri, diagramIndex, reason);
    }
}
