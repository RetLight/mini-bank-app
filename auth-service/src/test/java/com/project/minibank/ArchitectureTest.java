package com.project.minibank;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

// Versión ejecutable de la regla hexagonal: todo apunta hacia el dominio. Falla `mvn test` si alguien la rompe.
@AnalyzeClasses(packages = "com.project.minibank", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule domainDoesNotKnowFrameworksNorOtherLayers = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..", "jakarta..", "io.jsonwebtoken..", "feign..", "io.github.resilience4j..",
                    "..application..", "..infrastructure..");

    // @Service y @Transactional (spring.stereotype / spring.transaction) se permiten a propósito: son anotaciones,
    // no infraestructura. Datos, web, seguridad y clientes HTTP no.
    @ArchTest
    static final ArchRule applicationDoesNotKnowInfrastructureDetails = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..infrastructure..", "org.springframework.data..", "org.springframework.web..",
                    "org.springframework.http..", "org.springframework.security..",
                    "jakarta.persistence..", "jakarta.servlet..", "io.jsonwebtoken..",
                    "org.springframework.cloud..", "feign..", "io.github.resilience4j..");

    @ArchTest
    static final ArchRule controllersTalkToPortsNotToPersistence = noClasses()
            .that().resideInAPackage("..infrastructure.adapter.in.web..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure.adapter.out.persistence..");

    @ArchTest
    static final ArchRule persistenceDoesNotKnowTheWeb = noClasses()
            .that().resideInAPackage("..infrastructure.adapter.out.persistence..")
            .should().dependOnClassesThat().resideInAnyPackage("..infrastructure.adapter.in.web..", "..common.security..");

    // common guarda lo que cada servicio necesita en su propia copia (token, formato de error, logs de llamadas):
    // no puede conocer el área de negocio.
    @ArchTest
    static final ArchRule commonDoesNotKnowTheBusinessArea = noClasses()
            .that().resideInAPackage("com.project.minibank.common..")
            .should().dependOnClassesThat().resideInAPackage("com.project.minibank.auth..");

    // Los servicios solo comparten el contrato del JWT (claims) y el JSON de sus endpoints, nunca clases.
    @ArchTest
    static final ArchRule nothingKnowsTheCodeOfOtherServices = noClasses()
            .should().dependOnClassesThat().resideInAnyPackage("com.project.minibank.customer..", "com.project.minibank.movement..");

    // JJWT solo se usa donde se valida o se firma el token.
    @ArchTest
    static final ArchRule onlySecurityKnowsJjwt = noClasses()
            .that().resideOutsideOfPackages("com.project.minibank.common.security..", "com.project.minibank.auth.infrastructure.adapter.out.security..")
            .should().dependOnClassesThat().resideInAPackage("io.jsonwebtoken..");

    // El cliente HTTP y su resiliencia son detalles de infraestructura: solo el paquete del adaptador los conoce
    // (más common.servicecall, que registra las llamadas que hace Feign).
    @ArchTest
    static final ArchRule onlyTheClientPackageKnowsFeignAndResilience4j = noClasses()
            .that().resideOutsideOfPackages("com.project.minibank.auth.infrastructure.adapter.out.customer..", "com.project.minibank.common.servicecall..")
            .should().dependOnClassesThat().resideInAnyPackage("feign..", "org.springframework.cloud.openfeign..",
                    "io.github.resilience4j..");

    @ArchTest
    static final ArchRule dependenciesAreInjectedByConstructor = NO_CLASSES_SHOULD_USE_FIELD_INJECTION;
}
