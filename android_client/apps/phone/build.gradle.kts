import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.detekt)
    alias(libs.plugins.android.junit5)
    alias(libs.plugins.google.service.plugin)
    alias(libs.plugins.kotlin.parcelize)
//    alias(libs.plugins.kotlin.android)
}

fun getPropertyOrDefault(
    key: String,
    defaultValue: String,
): String = gradleLocalProperties(file("."), providers).getProperty(key) ?: defaultValue

configure<ApplicationExtension> {
    namespace = rootProject.extra["mainAppId"] as String
    compileSdk {
        version =
            release(rootProject.extra["compileSdkVersion"] as Int)
    }

    defaultConfig {
        applicationId = rootProject.extra["mainAppId"] as String
        minSdk = rootProject.extra["minSdkVersion"] as Int
        targetSdk = rootProject.extra["targetSdkVersion"] as Int
        versionCode = rootProject.extra["versionCode"] as Int
        versionName = rootProject.extra["versionName"] as String

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val webClientId =
            getPropertyOrDefault(
                "WEB_CLIENT_ID",
                "733301548359-93jkrf113dp2n3iu2qi10l7iss5lub2c.apps.googleusercontent.com",
            )
        buildConfigField("String", "WEB_CLIENT_ID", "\"$webClientId\"")
    }

    packaging {
        resources {
            excludes.addAll(
                listOf(
                    "META-INF/LICENSE.md",
                    "META-INF/LICENSE-notice.md",
                    "META-INF/NOTICE.md",
                ),
            )
        }
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
}

dependencies {
    // --- Core modules ---
//    implementation(project(":core:common"))

    // --- AndroidX & Core ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.datastore.preferences)

    // --- Compose & UI (Includes Activity, Navigation, ViewModel, Coil) ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    // --- Dependency Injection (Hilt) ---
    implementation(libs.bundles.hilt)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // --- Background Tasks (WorkManager) ---
    implementation(libs.androidx.work.runtime.ktx)

    // --- Database (Room) ---
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    // --- Identity & Credentials ---
    implementation(libs.androidx.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)

    // --- Firebase & Google Services ---
    implementation(libs.play.services.base)
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    // --- Media & Player (ExoPlayer) ---
    implementation(libs.bundles.media3)

    // --- Networking & Utilities ---
    // Note: This bundle includes Retrofit which wasn't in your original list,
    // but is defined in your TOML bundle.
    implementation(libs.bundles.networking)
    implementation(libs.prettytime)
    implementation(libs.process.phoenix)

    // --- ACRA ---
    implementation(libs.bundles.acra)

    // --- Unit Testing ---
    testImplementation(libs.bundles.testing.unit)
    testRuntimeOnly(libs.junit.platform.launcher)
    runtimeOnly(libs.junit.jupiter.engine)

    // --- Instrumented Testing (Android) ---
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.bundles.testing.android)

    // --- Debug Tools ---
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.leakcanary.android)
}
