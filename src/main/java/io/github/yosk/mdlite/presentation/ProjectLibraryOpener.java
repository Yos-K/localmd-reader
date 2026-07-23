package io.github.yosk.mdlite.presentation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import io.github.yosk.mdlite.file.MarkdownLibraryEntryPoint;
import io.github.yosk.mdlite.file.MarkdownLibraryListing;
import io.github.yosk.mdlite.file.MarkdownLibraryLocation;
import io.github.yosk.mdlite.file.MarkdownLibraryRootName;
import io.github.yosk.mdlite.file.MarkdownLibraryRootStore;
import io.github.yosk.mdlite.file.RememberedMarkdownLibrary;

final class ProjectLibraryOpener {
    private final MainActivity activity;
    private final FolderDocumentReader folderDocumentReader;
    private final MarkdownLibraryRootStore rootStore;

    ProjectLibraryOpener(MainActivity activity, FolderDocumentReader folderDocumentReader) {
        this.activity = activity;
        this.folderDocumentReader = folderDocumentReader;
        this.rootStore = new MarkdownLibraryRootStore(
                new SharedPreferencesMarkdownLibraryRootStorage(activity));
    }

    void open() {
        MarkdownLibraryEntryPoint entryPoint = MarkdownLibraryEntryPoint.from(rootStore.load());
        if (entryPoint instanceof MarkdownLibraryEntryPoint.ResumeProjectLibrary) {
            MarkdownLibraryEntryPoint.ResumeProjectLibrary resume =
                    (MarkdownLibraryEntryPoint.ResumeProjectLibrary) entryPoint;
            openRoot(resume.treeUri());
            return;
        }
        launchFolderPicker();
    }

    void openSelectedRoot(String treeUri) {
        rootStore.remember(RememberedMarkdownLibrary.selected(treeUri));
        openRoot(treeUri);
    }

    void open(MarkdownLibraryLocation location) {
        try {
            activity.showProjectLibrary(
                    location,
                    MarkdownLibraryListing.from(folderDocumentReader.entries(location)));
        } catch (SecurityException e) {
            recoverFromInaccessibleRoot();
        } catch (IllegalArgumentException e) {
            recoverFromInaccessibleRoot();
        }
    }

    void chooseAnotherRoot() {
        launchFolderPicker();
    }

    private void openRoot(String treeUri) {
        try {
            Uri uri = Uri.parse(treeUri);
            MarkdownLibraryRootName rootName = MarkdownLibraryRootName.fromProviderValue(
                    folderDocumentReader.rootDisplayName(uri),
                    activity.viewerText.markdownLibrary());
            open(MarkdownLibraryLocation.root(treeUri, rootName.value()));
        } catch (SecurityException e) {
            recoverFromInaccessibleRoot();
        } catch (IllegalArgumentException e) {
            recoverFromInaccessibleRoot();
        }
    }

    private void recoverFromInaccessibleRoot() {
        rootStore.forget();
        launchFolderPicker();
    }

    private void launchFolderPicker() {
        new AlertDialog.Builder(activity)
                .setTitle(activity.viewerText.libraryFolderGuidanceTitle())
                .setMessage(activity.viewerText.libraryFolderGuidanceMessage())
                .setNegativeButton(activity.viewerText.cancel(), null)
                .setPositiveButton(activity.viewerText.chooseLibraryFolder(),
                        new StartFolderPickerClickListener(this))
                .show();
    }

    private void startFolderPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        activity.startActivityForResult(intent, MainActivity.REQUEST_OPEN_FOLDER);
    }

    private static final class StartFolderPickerClickListener
            implements DialogInterface.OnClickListener {
        private final ProjectLibraryOpener opener;

        private StartFolderPickerClickListener(ProjectLibraryOpener opener) {
            this.opener = opener;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            opener.startFolderPicker();
        }
    }
}
