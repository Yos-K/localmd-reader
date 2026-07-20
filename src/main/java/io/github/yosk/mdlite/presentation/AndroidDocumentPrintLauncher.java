package io.github.yosk.mdlite.presentation;

import android.content.Context;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebView;

final class AndroidDocumentPrintLauncher implements DocumentPrintLauncher {
    private final Context context;

    AndroidDocumentPrintLauncher(Context context) {
        this.context = context;
    }

    @Override
    public void print(WebView webView, String title) {
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        if (printManager == null) {
            return;
        }
        PrintDocumentAdapter adapter = webView.createPrintDocumentAdapter(title);
        printManager.print(title, adapter, new PrintAttributes.Builder().build());
    }
}
