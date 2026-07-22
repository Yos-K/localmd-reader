package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.ProFeatureCatalog;
import io.github.yosk.mdlite.domain.ProFeatureDescriptor;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public final class ProFeatureViewerTextTest {
    @Test
    void englishViewerTextProvidesPurchaseActionLabel() {
        TestAssertions.assertEquals("Unlock Pro", english().purchaseProAction(),
                "English viewer text must provide the Pro purchase action label");
    }

    @Test
    void englishViewerTextExplainsProAsConvenienceNotARequiredUpgrade() {
        TestAssertions.assertEquals(
                "Free covers the core offline reader. Pro adds faster navigation and comfort tools for long files and linked project notes.",
                english().proFeaturesIntro(),
                "English Pro intro must describe Pro convenience");
    }

    @Test
    void japaneseViewerTextExplainsProAsConvenienceNotARequiredUpgrade() {
        TestAssertions.assertEquals(
                "Free版はオフライン閲覧の基本機能を備えています。Proでは長い文書や関連するプロジェクト文書を、より速く快適に読めます。",
                japanese().proFeaturesIntro(),
                "Japanese Pro intro must describe Pro convenience");
    }

    @ParameterizedTest(name = "feature {0}: {1}")
    @CsvSource(value = {
        "0|より快適な閲覧テーマ|ライトとダークだけでは読みづらい環境に合わせて、追加の配色を選べます。",
        "1|より多くのジェスチャーショートカット|円、方向、カスタムジェスチャーに、よく使う操作を割り当てられます。",
        "2|長い文書をすばやく移動|目次を開いて、読んでいた位置を失わずに見出しへ移動できます。",
        "3|見出し移動ショートカット|閲覧中にジェスチャーで前後の見出しへ移動できます。",
        "4|横に広い表を読みやすく|横に広い表を読むとき、見出し行と先頭列を表示したままにできます。",
        "5|より多くの閲覧履歴|多数のローカル文書を切り替えるとき、より多くの最近開いたファイルを保持します。",
        "6|つながったプロジェクトノート|ローカルの文書セット内にある安全な相対Markdownリンクを開けます。",
        "7|プロジェクトノート内のローカル画像|ローカルのMarkdown文書セット内にある安全な相対画像を表示します。",
        "8|エクスポートと印刷|HTMLとして保存するか、Androidの印刷機能からPDFとして保存できます。",
        "9|プロジェクトフォルダーを移動|ライブラリを閉じずに、階層を移動して関連するMarkdownファイルを開けます。"
    }, delimiter = '|')
    void japaneseViewerTextProvidesJapaneseCopyForEveryProFeature(
            int index, String title, String description) {
        ProFeatureDescriptor descriptor = japanese().proFeatureCatalog()[index];

        TestAssertions.assertSame(ProFeatureCatalog.initialFeatures()[index],
                descriptor.feature(), "feature identity");
        TestAssertions.assertEquals(title, descriptor.title(), "Japanese feature title");
        TestAssertions.assertEquals(description, descriptor.description(),
                "Japanese feature description");
    }

    @Test
    void englishViewerTextProvidesRestorePurchaseActionLabel() {
        TestAssertions.assertEquals("Restore purchase", english().restorePurchaseAction(),
                "English viewer text must provide the restore action label");
    }

    @Test
    void japaneseViewerTextProvidesPurchaseUnavailableMessage() {
        TestAssertions.assertEquals("購入はまだ利用できません。",
                japanese().purchaseMessage("purchase_unavailable"),
                "Japanese viewer text must provide the unavailable purchase message");
    }

    private static ViewerText english() {
        return ViewerText.fromLanguage(ViewerLanguage.english());
    }

    private static ViewerText japanese() {
        return ViewerText.fromLanguage(ViewerLanguage.japanese());
    }
}
