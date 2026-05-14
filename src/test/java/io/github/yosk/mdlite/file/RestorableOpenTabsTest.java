package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.testing.TestAssertions;
import java.util.Arrays;

public final class RestorableOpenTabsTest {
    public static void main(String[] args) {
        RestorableOpenTabsTest test = new RestorableOpenTabsTest();
        test.fromKeepsStoredTabOrderAndActiveIndex();
        test.fromDropsDuplicateUrisWithoutChangingTheFirstTab();
        test.fromClampsNegativeActiveIndexToTheFirstTab();
        test.fromClampsTooLargeActiveIndexToTheLastTab();
        test.tabUsesFallbackTitleWhenStoredTitleIsBlank();
    }

    public void fromKeepsStoredTabOrderAndActiveIndex() {
        RestorableOpenTabs tabs = RestorableOpenTabs.from(Arrays.asList(
                tab("first.md", "content://first"),
                tab("second.md", "content://second"),
                tab("third.md", "content://third")), 1);

        TestAssertions.assertEquals(3, tabs.tabs().size(), "stored tabs must restore every distinct URI");
        TestAssertions.assertEquals("first.md", tabs.tabs().get(0).title(), "first stored tab must remain first");
        TestAssertions.assertEquals("second.md", tabs.tabs().get(1).title(), "second stored tab must remain second");
        TestAssertions.assertEquals(1, tabs.activeIndex(), "stored active index must select the same tab when it is valid");
    }

    public void fromDropsDuplicateUrisWithoutChangingTheFirstTab() {
        RestorableOpenTabs tabs = RestorableOpenTabs.from(Arrays.asList(
                tab("current.md", "content://same"),
                tab("old duplicate.md", "content://same"),
                tab("other.md", "content://other")), 0);

        TestAssertions.assertEquals(2, tabs.tabs().size(), "restored tabs must not contain duplicate URIs");
        TestAssertions.assertEquals("current.md", tabs.tabs().get(0).title(), "first stored duplicate must win");
        TestAssertions.assertEquals("other.md", tabs.tabs().get(1).title(), "distinct later tab must remain");
    }

    public void fromClampsNegativeActiveIndexToTheFirstTab() {
        RestorableOpenTabs tabs = RestorableOpenTabs.from(Arrays.asList(
                tab("first.md", "content://first"),
                tab("second.md", "content://second")), -1);

        TestAssertions.assertEquals(0, tabs.activeIndex(), "negative stored active index must restore the first tab");
    }

    public void fromClampsTooLargeActiveIndexToTheLastTab() {
        RestorableOpenTabs tabs = RestorableOpenTabs.from(Arrays.asList(
                tab("first.md", "content://first"),
                tab("second.md", "content://second")), 99);

        TestAssertions.assertEquals(1, tabs.activeIndex(), "out-of-range stored active index must restore the last tab");
    }

    public void tabUsesFallbackTitleWhenStoredTitleIsBlank() {
        RestorableOpenTab tab = RestorableOpenTab.of("  ", "content://blank-title");

        TestAssertions.assertEquals("Untitled Markdown", tab.title(), "blank stored title must not restore an empty tab label");
    }

    private static RestorableOpenTab tab(String title, String uri) {
        return RestorableOpenTab.of(title, uri);
    }

}
