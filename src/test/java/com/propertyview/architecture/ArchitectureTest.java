package com.propertyview.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.propertyview", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    private static final String[] FRAMEWORKS = {
        "org.springframework..", "jakarta..", "org.hibernate..", "com.fasterxml..",
        "tools.jackson..", "io.swagger.."};

    @ArchTest
    static final ArchRule layersRespectTheDependencyRule = layeredArchitecture().consideringAllDependencies()
        .layer("Domain").definedBy("com.propertyview.domain..")
        .layer("Application").definedBy("com.propertyview.application..")
        .layer("Presentation").definedBy("com.propertyview.presentation..")
        .layer("Infrastructure").definedBy("com.propertyview.infrastructure..")
        .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Presentation", "Infrastructure")
        .whereLayer("Application").mayOnlyBeAccessedByLayers("Presentation", "Infrastructure")
        .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
        .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer();

    @ArchTest
    static final ArchRule domainIsFrameworkFree = noClasses().that().resideInAPackage("..domain..")
        .should().dependOnClassesThat().resideInAnyPackage(FRAMEWORKS)
        .because("the domain must not know about Spring, JPA, Jackson or Swagger");

    @ArchTest
    static final ArchRule applicationIsFrameworkFree = noClasses().that().resideInAPackage("..application..")
        .should().dependOnClassesThat().resideInAnyPackage(FRAMEWORKS)
        .because("use cases are plain classes wired in infrastructure.config");

    @ArchTest
    static final ArchRule persistenceDetailsStayInPersistence = noClasses()
        .that().resideOutsideOfPackage("..infrastructure.persistence..")
        .should().dependOnClassesThat().resideInAnyPackage("jakarta.persistence..", "org.springframework.data..", "org.hibernate..")
        .because("JPA and Spring Data are an implementation detail of the persistence adapter");

    @ArchTest
    static final ArchRule controllersDoNotUseRepositoriesDirectly = noClasses()
        .that().resideInAPackage("..presentation..")
        .should().dependOnClassesThat().resideInAPackage("..domain.port..")
        .because("controllers talk to use cases only");

    @ArchTest
    static final ArchRule controllersLiveInTheControllerPackage = classes()
        .that().areAnnotatedWith(RestController.class)
        .should().resideInAPackage("..presentation.web.controller..")
        .andShould().haveSimpleNameEndingWith("Controller");

    @ArchTest
    static final ArchRule noCyclesBetweenTopLevelPackages = slices()
        .matching("com.propertyview.(*)..").should().beFreeOfCycles();

    @ArchTest
    static final ArchRule noFieldInjection = GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

    @ArchTest
    static final ArchRule noStandardStreams = GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
}
