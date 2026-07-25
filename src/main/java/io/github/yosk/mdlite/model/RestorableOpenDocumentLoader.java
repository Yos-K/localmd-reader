package io.github.yosk.mdlite.model;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.file.FileSizePolicy;
import io.github.yosk.mdlite.file.MarkdownFileOpenResult;
import io.github.yosk.mdlite.file.RestorableOpenTab;
import io.github.yosk.mdlite.viewer.OpenDocumentTab;
import java.io.IOException;

public final class RestorableOpenDocumentLoader implements RestoredOpenDocumentTabs.Loader {
    private final Source source;
    private final Renderer renderer;
    private final FileSizePolicy fileSizePolicy;
    private final long maximumBytes;

    public RestorableOpenDocumentLoader(
            Source source, Renderer renderer, FileSizePolicy fileSizePolicy, long maximumBytes) {
        if (source == null || renderer == null || fileSizePolicy == null || maximumBytes < 0) {
            throw new IllegalArgumentException("restoration dependencies must not be null");
        }
        this.source = source;
        this.renderer = renderer;
        this.maximumBytes = maximumBytes;
        this.fileSizePolicy = fileSizePolicy;
    }

    @Override
    public RestoredOpenDocumentTab load(RestorableOpenTab storedTab) {
        try {
            String displayName = source.displayName(storedTab.uri());
            if (displayName == null || displayName.trim().length() == 0) {
                displayName = storedTab.title();
            }
            MarkdownFileOpenResult result = MarkdownFileOpenResult.from(
                    displayName, source.sizeBytes(storedTab.uri()), fileSizePolicy);
            if (!(result instanceof MarkdownFileOpenResult.ReadableMarkdownFile)) {
                return RestoredOpenDocumentTab.unavailable();
            }
            String markdown = source.readMarkdown(storedTab.uri(), maximumBytes);
            SafeHtml rendered = renderer.render(storedTab.uri(), markdown);
            return RestoredOpenDocumentTab.available(OpenDocumentTab.fileDocument(
                    ((MarkdownFileOpenResult.ReadableMarkdownFile) result).displayName(),
                    storedTab.uri(), rendered));
        } catch (IOException e) {
            return RestoredOpenDocumentTab.unavailable();
        } catch (SecurityException e) {
            return RestoredOpenDocumentTab.unavailable();
        }
    }

    public interface Source {
        String displayName(String uri);

        long sizeBytes(String uri);

        String readMarkdown(String uri, long maximumBytes) throws IOException;
    }

    public interface Renderer {
        SafeHtml render(String uri, String markdown);
    }
}
