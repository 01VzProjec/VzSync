import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

// Load signing credentials from local.properties when present.
// In CI environments (e.g., GitHub Actions) this file does not exist,
// so the signing config falls back to environment variables injected
// by the workflow.
val localProps = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

android {
    namespace = "com.vztechcore.vzsync"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.vztechcore.vzsync"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // Release signing configuration.
    // Resolution order:
    //   1. local.properties (developer machine)
    //   2. Environment variables (CI build)
    signingConfigs {
        create("release") {
            storeFile = file("release-key.jks")
            storePassword = localProps.getProperty("VZSIGN_STORE_PASSWORD")
                ?: System.getenv("VZSIGN_STORE_PASSWORD")
                ?: ""
            keyAlias = localProps.getProperty("VZSIGN_KEY_ALIAS")
                ?: System.getenv("VZSIGN_KEY_ALIAS")
                ?: "vzsync"
            keyPassword = localProps.getProperty("VZSIGN_KEY_PASSWORD")
                ?: System.getenv("VZSIGN_KEY_PASSWORD")
                ?: ""
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Kotlin 2.3 compiler options DSL.
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        compose = true
    }

    // Exclude duplicate license/notice files that cause packaging conflicts
    // when multiple AARs ship the same metadata.
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/NOTICE.txt"
        }
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Activity & Navigation
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)

    // Image loading
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Security
    implementation(libs.androidx.security.crypto)

    // Git
    implementation(libs.jgit)

    // Icons
    implementation(libs.androidx.compose.material.icons.extended)

    // Lifecycle-aware state collection
    implementation(libs.androidx.lifecycle.runtime.compose)

    // SAF tree URI helper
    implementation(libs.androidx.documentfile)

    implementation(libs.androidx.appcompat)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}