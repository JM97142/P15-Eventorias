import com.android.build.gradle.BaseExtension
import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("com.google.gms.google-services")
    id("dagger.hilt.android.plugin")
    id("jacoco")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        load(FileInputStream(file))
    }
}
val apiKey = localProperties.getProperty("API_KEY_GOOGLE") ?: ""
val apiMapsKey = localProperties.getProperty("API_KEY_GOOGLE_MAPS") ?: ""

android {
    namespace = "com.example.p15_eventorias"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.p15_eventorias"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "GOOGLE_API_KEY", "\"$apiKey\"")
        buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"$apiMapsKey\"")
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes.addAll(
                listOf(
                    "META-INF/LICENSE.md",
                    "META-INF/LICENSE-notice.md",
                    "META-INF/NOTICE.md",
                    "META-INF/LICENSE.txt",
                    "META-INF/NOTICE.txt"
                )
            )
        }
    }
    lint {
        checkReleaseBuilds = false
        abortOnError = false
        disable += listOf(
            "FrequentlyChangingValue",
            "RememberInComposition",
            "NullSafeMutableLiveData",
            "AutoboxingStateCreation"
        )
        abortOnError = false
    }
}

val androidExtension = extensions.getByType<BaseExtension>()

// Rapport pour les tests unitaires
tasks.register<JacocoReport>("jacocoUnitTestReport") {
    dependsOn("testDebugUnitTest")
    group = "Reporting"
    description = "Generate Jacoco coverage reports for unit tests"

    reports {
        xml.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacocoUnitTestReport/jacocoUnitTestReport.xml"))
        html.required.set(true)
    }

    val debugTree = fileTree("${layout.buildDirectory}/tmp/kotlin-classes/debug") {
        exclude(
            "**/R.class", "**/R$*.class", "**/BuildConfig.*",
            "**/Manifest*.*", "**/*Test*.*"
        )
    }
    val mainSrc = androidExtension.sourceSets.getByName("test").java.srcDirs

    classDirectories.setFrom(files(debugTree))
    sourceDirectories.setFrom(files(mainSrc))
    executionData.setFrom(fileTree(layout.buildDirectory) {
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
    })
}

// Rapport pour les tests instrumentés
tasks.register<JacocoReport>("jacocoAndroidTestReport") {
    dependsOn("connectedDebugAndroidTest")
    group = "Reporting"
    description = "Generate Jacoco coverage reports for instrumentation tests"

    reports {
        xml.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacocoAndroidTestReport/jacocoAndroidTestReport.xml"))
        html.required.set(true)
    }

    val debugTree = fileTree("${layout.buildDirectory}/tmp/kotlin-classes/debug") {
        exclude(
            "**/R.class", "**/R$*.class", "**/BuildConfig.*",
            "**/Manifest*.*", "**/*Test*.*"
        )
    }
    val mainSrc = androidExtension.sourceSets.getByName("main").java.srcDirs

    classDirectories.setFrom(files(debugTree))
    sourceDirectories.setFrom(files(mainSrc))
    executionData.setFrom(fileTree(layout.buildDirectory) {
        include("outputs/code_coverage/debugAndroidTest/connected/**/*.ec")
    })
}

dependencies {
    // Kotlin
    implementation(platform(libs.kotlin.bom))
    // Hilt
    implementation(libs.hilt)
    implementation(libs.lifecycle.viewmodel.savedstate.android)
    implementation(libs.ui.test.junit4.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.material)
    implementation(libs.compose.material3)
    implementation(libs.lifecycle.runtime.compose)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.material.icons.extended)
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    // Utils
    implementation(libs.coil.compose)
    implementation(libs.accompanist.permissions)
    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ui.test)
    androidTestImplementation(libs.mockk.android)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.inline)
    testImplementation (libs.google.firebase.firestore)
    testImplementation (libs.mockito.core.v451)
    testImplementation (libs.core.testing)
    testImplementation (libs.google.firebase.auth)
    testImplementation (libs.mockk)
    debugImplementation(libs.ui.test.manifest)
    // Google sign in
    implementation(libs.play.services.auth)
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.appcheck.debug)
    implementation(libs.firebase.ui.auth)
    implementation(libs.firebase.ui.firestore)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.google.firebase.appcheck.debug)

}