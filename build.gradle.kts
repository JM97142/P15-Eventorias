// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("org.sonarqube") version "5.1.0.4882"
}

sonar {
    properties {
        property("sonar.projectKey", "JM97142_P15-Eventorias")
        property("sonar.organization", "jm97142")
        property("sonar.host.url", "https://sonarcloud.io")

        property("sonar.sources", "src/main/java, src/main/kotlin")
        property("sonar.tests", "src/test/java, src/androidTest/java")
        property("sonar.java.binaries", "${project.buildDir}/intermediates/javac/debug/classes")
        property("sonar.kotlin.binaries", "${project.buildDir}/tmp/kotlin-classes/debug")
        property("sonar.coverage.jacoco.xmlReportPaths", "${project.buildDir}/reports/jacoco/jacocoTestReport/jacocoUnitTestReport.xml")

    }
}