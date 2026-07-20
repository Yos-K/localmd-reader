package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class MarkdownLibraryViewerTextTest {
    @Test
    void viewerTextProvidesLocalizedProjectLibraryNavigation() {
        ViewerText englishText = ViewerText.fromLanguage(ViewerLanguage.english());
        ViewerText japaneseText = ViewerText.fromLanguage(ViewerLanguage.japanese());

        TestAssertions.assertEquals("Markdown library", englishText.markdownLibrary(),
                "English project library title");
        TestAssertions.assertEquals("Markdownライブラリ", japaneseText.markdownLibrary(),
                "Japanese project library title");
        TestAssertions.assertEquals("Markdown library", englishText.openMarkdownLibrary(),
                "English collapsible library label must fit on one menu row");
        TestAssertions.assertEquals("Markdownライブラリ", japaneseText.openMarkdownLibrary(),
                "Japanese collapsible library label must fit on one menu row");
        TestAssertions.assertEquals("Up one folder", englishText.upOneFolder(),
                "English parent-folder action");
        TestAssertions.assertEquals("一つ上のフォルダーへ", japaneseText.upOneFolder(),
                "Japanese parent-folder action");
        TestAssertions.assertEquals("notes/", englishText.folderEntry("notes"),
                "English directory entries must be distinguishable without relying on color");
        TestAssertions.assertEquals("notes/", japaneseText.folderEntry("notes"),
                "Japanese directory entries must use familiar path notation");
    }

    @Test
    void englishLibraryFilterHintDescribesFilteringByName() {
        TestAssertions.assertEquals("Filter by file or folder name",
                ViewerText.fromLanguage(ViewerLanguage.english()).libraryFilterHint(),
                "English library filter must describe the immediate action");
    }

    @Test
    void japaneseLibraryFilterHintDescribesFilteringByName() {
        TestAssertions.assertEquals("ファイル・フォルダー名で絞り込み",
                ViewerText.fromLanguage(ViewerLanguage.japanese()).libraryFilterHint(),
                "Japanese library filter must describe the immediate action");
    }

    @Test
    void englishLibraryEmptyResultExplainsThatTheFilterHasNoMatch() {
        TestAssertions.assertEquals("No matching files or folders",
                ViewerText.fromLanguage(ViewerLanguage.english()).noMatchingLibraryItems(),
                "English empty result must distinguish filtering from an empty folder");
    }

    @Test
    void japaneseLibraryEmptyResultExplainsThatTheFilterHasNoMatch() {
        TestAssertions.assertEquals("一致するファイルやフォルダーはありません",
                ViewerText.fromLanguage(ViewerLanguage.japanese()).noMatchingLibraryItems(),
                "Japanese empty result must distinguish filtering from an empty folder");
    }

    @Test
    void englishFolderPickerGuidanceExplainsAndroidDownloadRestrictionAndNextAction() {
        ViewerText text = ViewerText.fromLanguage(ViewerLanguage.english());

        TestAssertions.assertEquals("Choose a library folder",
                text.libraryFolderGuidanceTitle(),
                "English guidance title must name the pending action");
        TestAssertions.assertEquals(
                "Android does not allow apps to use the Download folder itself. "
                        + "Choose or create a subfolder inside it instead.",
                text.libraryFolderGuidanceMessage(),
                "English guidance must explain both the restriction and the recovery action");
        TestAssertions.assertEquals("Choose folder", text.chooseLibraryFolder(),
                "English continuation must describe the action");
    }

    @Test
    void japaneseFolderPickerGuidanceExplainsAndroidDownloadRestrictionAndNextAction() {
        ViewerText text = ViewerText.fromLanguage(ViewerLanguage.japanese());

        TestAssertions.assertEquals("ライブラリフォルダーを選ぶ",
                text.libraryFolderGuidanceTitle(),
                "Japanese guidance title must name the pending action");
        TestAssertions.assertEquals(
                "Androidの制限により、ダウンロードフォルダー自体は使用できません。"
                        + "その中のサブフォルダーを選ぶか、新しく作成してください。",
                text.libraryFolderGuidanceMessage(),
                "Japanese guidance must explain both the restriction and the recovery action");
        TestAssertions.assertEquals("フォルダーを選ぶ", text.chooseLibraryFolder(),
                "Japanese continuation must describe the action");
    }
}
