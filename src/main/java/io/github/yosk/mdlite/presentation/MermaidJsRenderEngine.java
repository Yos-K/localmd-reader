package io.github.yosk.mdlite.presentation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebSettings;
import android.webkit.WebView;
import io.github.yosk.mdlite.domain.MermaidRenderJob;
import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.viewer.ViewerTheme;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;

final class MermaidJsRenderEngine {
    interface Listener {
        void onMermaidRendered(MermaidRenderJob job, SafeHtml svg);

        void onMermaidRenderFailed(MermaidRenderJob job, String reason);
    }

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final WebView webView;
    private final Listener listener;
    private final Queue<RenderJob> jobs = new ArrayDeque<RenderJob>();
    private RenderJob activeJob;
    private boolean pageReady;
    private boolean rendering;

    MermaidJsRenderEngine(Context context, Listener listener) {
        this.listener = listener;
        webView = new WebView(context.getApplicationContext());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(false);
        settings.setDatabaseEnabled(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setBlockNetworkLoads(true);
        webView.addJavascriptInterface(new MermaidJsBridge(this), "LocalMDMermaid");
        webView.loadDataWithBaseURL(null, renderPage(context), "text/html", "UTF-8", null);
    }

    void enqueue(MermaidRenderJob job, MermaidDiagramTheme theme) {
        jobs.add(new RenderJob(job, theme));
        drain();
    }

    private void drain() {
        if (!pageReady || rendering || jobs.isEmpty()) {
            return;
        }
        rendering = true;
        RenderJob job = jobs.remove();
        activeJob = job;
        String script = "window.renderLocalMdMermaid("
                + quote(job.job.documentUri()) + ","
                + job.job.diagramIndex() + ","
                + quote(job.job.block().source()) + ","
                + quote(job.theme.background) + ","
                + quote(job.theme.text) + ","
                + quote(job.theme.line) + ","
                + quote(job.theme.primary) + ","
                + quote(job.theme.secondary) + ");";
        webView.evaluateJavascript(script, null);
    }

    private void completeSuccess(String documentUri, int diagramIndex, String svg) {
        RenderJob completed = activeJob;
        if (completed == null || !completed.job.matches(documentUri, diagramIndex)) { return; }
        activeJob = null;
        rendering = false;
        listener.onMermaidRendered(completed.job, SafeHtml.fromTrustedRendererOutput(svg));
        drain();
    }

    private void completeFailure(String documentUri, int diagramIndex, String reason) {
        RenderJob completed = activeJob;
        if (completed == null || !completed.job.matches(documentUri, diagramIndex)) { return; }
        activeJob = null;
        rendering = false;
        listener.onMermaidRenderFailed(completed.job, reason);
        drain();
    }

    void notifyReady() {
        mainHandler.post(new ReadyCallback(this));
    }

    void notifyRendered(String documentUri, int diagramIndex, String svg) {
        mainHandler.post(new RenderedCallback(this, documentUri, diagramIndex, svg));
    }

    void notifyFailed(String documentUri, int diagramIndex, String reason) {
        mainHandler.post(new FailedCallback(this, documentUri, diagramIndex, reason));
    }

    private static String renderPage(Context context) {
        return "<!doctype html><html><head><meta charset=\"utf-8\"></head><body>"
                + "<div id=\"target\"></div>"
                + "<script>" + readAsset(context, "mermaid.min.js") + "</script>"
                + "<script>"
                + "var localMdMermaidThemeCss='svg,svg *{font-size:16px;} .nodeLabel,.edgeLabel,.messageText,.actor{font-size:16px;line-height:1.35;white-space:normal;} foreignObject{overflow:visible;} svg{overflow:visible;}';"
                + "function localMdMermaidConfig(background,text,line,primary,secondary){return {"
                + "startOnLoad:false,securityLevel:'strict',theme:'base',"
                + "flowchart:{htmlLabels:true,wrappingWidth:180,padding:24,nodeSpacing:56,rankSpacing:64},"
                + "sequence:{wrap:true,width:180,messageMargin:44,noteMargin:16},"
                + "themeCSS:localMdMermaidThemeCss,"
                + "themeVariables:{"
                + "background:background,"
                + "mainBkg:background,"
                + "secondBkg:secondary,"
                + "primaryColor:primary,"
                + "primaryTextColor:text,"
                + "primaryBorderColor:line,"
                + "secondaryColor:secondary,"
                + "secondaryTextColor:text,"
                + "secondaryBorderColor:line,"
                + "tertiaryColor:background,"
                + "tertiaryTextColor:text,"
                + "tertiaryBorderColor:line,"
                + "lineColor:line,"
                + "textColor:text,"
                + "fontSize:'16px',"
                + "nodeTextColor:text,"
                + "actorTextColor:text,"
                + "actorLineColor:line,"
                + "actorBkg:primary,"
                + "labelTextColor:text,"
                + "loopTextColor:text,"
                + "noteTextColor:text,"
                + "noteBkg:secondary,"
                + "noteBorderColor:line,"
                + "activationBkg:secondary,"
                + "activationBorderColor:line,"
                + "sequenceNumberColor:text"
                + "}};}"
                + "function localMdVisibleSvg(svg){return svg.replace('<svg ','<svg style=\"overflow:visible\" ');}"
                + initialThemeScript(MermaidDiagramTheme.from(ViewerTheme.light()))
                + "window.renderLocalMdMermaid=function(documentUri,diagramIndex,source,background,text,line,primary,secondary){"
                + "var id='localmd-mermaid-'+Date.now()+'-'+diagramIndex;"
                + "mermaid.initialize(localMdMermaidConfig(background,text,line,primary,secondary));"
                + "mermaid.render(id,source).then(function(result){"
                + "LocalMDMermaid.rendered(documentUri,diagramIndex,localMdVisibleSvg(result.svg));"
                + "}).catch(function(error){"
                + "LocalMDMermaid.failed(documentUri,diagramIndex,error && error.message ? error.message : 'The diagram syntax is not supported or contains an error.');"
                + "});"
                + "};"
                + "LocalMDMermaid.ready();"
                + "</script></body></html>";
    }

    private static String readAsset(Context context, String name) {
        try {
            InputStream input = context.getAssets().open(name);
            try {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[8192];
                int read = input.read(buffer);
                while (read >= 0) {
                    output.write(buffer, 0, read);
                    read = input.read(buffer);
                }
                return new String(output.toByteArray(), StandardCharsets.UTF_8);
            } finally {
                input.close();
            }
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * The initialize call baked into the render page (#73): its colors come
     * from the shared theme style via MermaidDiagramTheme instead of
     * duplicated literals, so ViewerThemeStyle stays the single source of
     * truth. Each render re-initializes with the current theme anyway; this
     * default only covers the moment before the first job arrives.
     */
    static String initialThemeScript(MermaidDiagramTheme theme) {
        return "mermaid.initialize(localMdMermaidConfig("
                + quote(theme.background) + ","
                + quote(theme.text) + ","
                + quote(theme.line) + ","
                + quote(theme.primary) + ","
                + quote(theme.secondary) + "));";
    }

    private static String quote(String value) {
        String text = value == null ? "" : value;
        StringBuilder quoted = new StringBuilder();
        quoted.append('"');
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '\\':
                    quoted.append("\\\\");
                    break;
                case '"':
                    quoted.append("\\\"");
                    break;
                case '\n':
                    quoted.append("\\n");
                    break;
                case '\r':
                    quoted.append("\\r");
                    break;
                case '\t':
                    quoted.append("\\t");
                    break;
                default:
                    quoted.append(c);
                    break;
            }
        }
        quoted.append('"');
        return quoted.toString();
    }

    private static final class ReadyCallback implements Runnable {
        private final MermaidJsRenderEngine engine;

        private ReadyCallback(MermaidJsRenderEngine engine) {
            this.engine = engine;
        }

        @Override
        public void run() {
            engine.pageReady = true;
            engine.drain();
        }
    }

    private static final class RenderedCallback implements Runnable {
        private final MermaidJsRenderEngine engine;
        private final String documentUri;
        private final int diagramIndex;
        private final String svg;

        private RenderedCallback(MermaidJsRenderEngine engine, String documentUri, int diagramIndex, String svg) {
            this.engine = engine;
            this.documentUri = documentUri;
            this.diagramIndex = diagramIndex;
            this.svg = svg;
        }

        @Override
        public void run() {
            engine.completeSuccess(documentUri, diagramIndex, svg);
        }
    }

    private static final class FailedCallback implements Runnable {
        private final MermaidJsRenderEngine engine;
        private final String documentUri;
        private final int diagramIndex;
        private final String reason;

        private FailedCallback(MermaidJsRenderEngine engine, String documentUri, int diagramIndex, String reason) {
            this.engine = engine;
            this.documentUri = documentUri;
            this.diagramIndex = diagramIndex;
            this.reason = reason;
        }

        @Override
        public void run() {
            engine.completeFailure(documentUri, diagramIndex, reason);
        }
    }

    private static final class RenderJob {
        private final MermaidRenderJob job;
        private final MermaidDiagramTheme theme;

        private RenderJob(MermaidRenderJob job, MermaidDiagramTheme theme) {
            this.job = job;
            this.theme = theme;
        }
    }
}
