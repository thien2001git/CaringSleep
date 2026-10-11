import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.detekt)
    alias(libs.plugins.android.junit5)
    alias(libs.plugins.google.service.plugin)
}

configure<LibraryExtension> {
    namespace = "com.hoang.common"
    compileSdk = rootProject.extra["compileSdkVersion"] as Int

    defaultConfig {
        minSdk = rootProject.extra["minSdkVersion"] as Int

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = rootProject.extra["sourceCompatibility"] as JavaVersion
        targetCompatibility = rootProject.extra["targetCompatibility"] as JavaVersion
    }
    buildFeatures {
        compose = true
        buildConfig = true
        aidl = true
    }
    packaging {
        resources {
            excludes += "google/protobuf/**"
            excludes += "META-INF/INDEX.LIST"
        }
    }
}

kotlin {
    jvmToolchain(rootProject.extra["jvmToolchain"] as Int)
}
ksp {
    arg("jdkHome", System.getProperty("java.home"))
    arg("hilt.correctErrorTypes", "true")
}

dependencies {

    // --- AndroidX & Core ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.datastore.preferences)
    implementation(libs.androidx.lifecycle.service)

    // --- Compose & UI Bundle ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    // --- Dependency Injection (Hilt) Bundle ---
    implementation(libs.bundles.hilt)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // --- Background Tasks (WorkManager) ---
    implementation(libs.androidx.work.runtime.ktx)

    // --- Database (Room) Bundle ---
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    // --- Firebase & Google Services Bundle ---
    implementation(libs.play.services.base)
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    // --- Media & Player (ExoPlayer) Bundle ---
    implementation(libs.bundles.media3)

    // --- ACRA Bundle ---
    implementation(libs.bundles.acra)

    // --- Networking & Utilities Bundle ---
    implementation(libs.bundles.networking)
    implementation(libs.prettytime)
    implementation(libs.process.phoenix)

    // --- Unit Testing Bundle ---
    testImplementation(libs.bundles.testing.unit)
    testRuntimeOnly(libs.junit.platform.launcher)
    runtimeOnly(libs.junit.jupiter.engine)

    // --- Instrumented Testing (Android) Bundle ---
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.bundles.testing.android)

    // --- Debug Tools ---
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
