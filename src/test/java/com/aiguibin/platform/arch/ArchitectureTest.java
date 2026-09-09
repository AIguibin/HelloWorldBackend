package com.aiguibin.platform.arch;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * 架构守护测试：分层依赖规则（随业务模块增长自动生效）.
 * 规则约定：controller 禁止直调 mapper；common 禁止反向依赖业务层。
 * allowEmptyShould(true)：骨架期无匹配类时放行，业务模块出现后规则自动生效。
 */
@AnalyzeClasses(packages = "com.aiguibin.platform.arch", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule controllersShouldNotAccessMappersDirectly = noClasses()
            .that()
            .resideInAPackage("..controller..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..mapper..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule commonShouldNotDependOnBusinessLayers = noClasses()
            .that()
            .resideInAPackage("..common..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..controller..", "..service..", "..mapper..", "..entity..")
            .allowEmptyShould(true);
}
