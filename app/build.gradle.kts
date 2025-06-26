plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "1.9.10"
}

android {
    namespace = "pt.rednexx46.esp32companionapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "pt.rednexx46.esp32companionapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Core Compose UI
    implementation(libs.androidx.foundation)                        // Foundation components
    implementation(libs.androidx.foundation.layout)                 // Layout helpers
    implementation(libs.androidx.ui)                                // Core UI
    implementation(libs.androidx.ui.graphics)                       // Graphics support
    implementation(libs.androidx.ui.tooling.preview)               // Preview tooling
    implementation(libs.androidx.ui.util)                           // UI utilities

    // Material3 Extras
    implementation(libs.androidx.material3.window.size.class1)     // Window size class
    implementation(libs.androidx.material.icons.extended)          // Extended Material Icons


    // Navigation
    implementation(libs.androidx.navigation.compose.android)

    // Lifecycle and Activity integration
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Networking (Ktor client)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)

    // Firebase (Crashlytics or others)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.material3.android)
    implementation(libs.ads.mobile.sdk)

    // Unit Tests
    testImplementation(libs.junit)

    // Android Instrumented Tests
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debugging Tools
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}