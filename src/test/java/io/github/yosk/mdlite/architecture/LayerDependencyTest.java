package io.github.yosk.mdlite.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Executable counterpart of the layer rules documented in AGENTS.md.
 * Each rule encodes an architectural decision that AI agents (and humans)
 * must not break without an explicit decision recorded in a PR.
 */
public final class LayerDependencyTest {

    private static final String ROOT = "io.github.yosk.mdlite";
    private static JavaClasses productionClasses;

    @BeforeAll
    static void importProductionClasses() {
        productionClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(ROOT);
    }

    @Test
    void domainPackageMustNotDependOnAndroidFramework() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage("android..", "androidx..");
        rule.check(productionClasses);
    }

    @Test
    void viewerPackageMustNotDependOnAndroidFramework() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..viewer..")
                .should().dependOnClassesThat().resideInAnyPackage("android..", "androidx..");
        rule.check(productionClasses);
    }

    @Test
    void filePackageMustNotDependOnAndroidFramework() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..file..")
                .should().dependOnClassesThat().resideInAnyPackage("android..", "androidx..");
        rule.check(productionClasses);
    }

    @Test
    void modelPackageMustNotDependOnAndroidFramework() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..model..")
                .should().dependOnClassesThat().resideInAnyPackage("android..", "androidx..");
        rule.check(productionClasses);
    }

    @Test
    void modelPackageMustNotDependOnAndroidAdapters() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..model..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..presentation..", "..infrastructure..");
        rule.check(productionClasses);
    }

    @Test
    void domainPackageMustNotDependOnOtherInternalLayers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..infrastructure..",
                        "..viewer..",
                        "..file..",
                        "..presentation..");
        rule.check(productionClasses);
    }

    @Test
    void viewerPackageMustNotDependOnInfrastructureOrPresentation() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..viewer..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..infrastructure..",
                        "..presentation..");
        rule.check(productionClasses);
    }

    @Test
    void filePackageMustNotDependOnInfrastructureOrPresentation() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..file..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..infrastructure..",
                        "..presentation..");
        rule.check(productionClasses);
    }

    @Test
    void activityClassesMustLiveInPresentationPackage() {
        // The Termux runner skips compiling the presentation package (it needs
        // Android SDK), so no Activity classes are visible there. allowEmptyShould
        // lets the rule pass in that environment while still enforcing it in
        // Gradle/CI where presentation is compiled.
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Activity")
                .should().resideInAPackage("..presentation..")
                .allowEmptyShould(true);
        rule.check(productionClasses);
    }

    @Test
    void layersMustNotFormCyclicDependencies() {
        ArchRule rule = slices()
                .matching(ROOT + ".(*)..")
                .should().beFreeOfCycles();
        rule.check(productionClasses);
    }

    // ── Extended rules (C-8) ───────────────────────────────────────────────

    /**
     * Infrastructure is a service/adapter layer and must not call up into the
     * Android UI wiring layer. Allowing this would invert the dependency
     * direction and couple rendering/purchase logic to Activity state.
     * allowEmptyShould: infrastructure classes that use Android APIs may be
     * absent on the Termux pure-JVM runner.
     */
    @Test
    void infrastructurePackageMustNotDependOnPresentation() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..infrastructure..")
                .should().dependOnClassesThat().resideInAPackage("..presentation..")
                .allowEmptyShould(true);
        rule.check(productionClasses);
    }

    /**
     * Viewer (document-display logic) and file (file-management types) are
     * sibling layers at the same abstraction level. Viewer must not pull in
     * FileInfo, PinnedDocuments, or other file-layer types; those cross
     * the boundary between display logic and storage management.
     */
    @Test
    void viewerPackageMustNotDependOnFilePackage() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..viewer..")
                .should().dependOnClassesThat().resideInAPackage("..file..");
        rule.check(productionClasses);
    }

    /**
     * File-layer types (file-open results, pinned documents, recent documents)
     * must not depend on viewer concepts such as ViewerTheme or FontSize.
     * These two layers are parallel; keeping them independent lets each evolve
     * without affecting the other.
     */
    @Test
    void filePackageMustNotDependOnViewerPackage() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..file..")
                .should().dependOnClassesThat().resideInAPackage("..viewer..");
        rule.check(productionClasses);
    }
}
