// Top-level build file where you can add configuration options common to all sub-projects/modules.
import java.text.SimpleDateFormat
import java.util.Calendar

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.android.junit5)
        classpath(libs.hilt.android.gradle.plugin)
        classpath(libs.detekt.gradle.plugin)
        classpath(libs.dokka.gradle.plugin)
        classpath(libs.google.services)
    }
}

plugins {
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.devtools.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.service.plugin) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.rootcoverage)
    alias(libs.plugins.android.built.in1.kotlin) apply false
}

// --- Versioning Helpers ---

fun getBranchName(): String = runCatching {
    providers.exec {
        commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
    }.standardOutput.asText.get().trim().replace("/", "-")
}.getOrDefault("unknown")

fun getCommitCount(): Int = runCatching {
    providers.exec {
        commandLine("git", "rev-list", "--count", "HEAD")
    }.standardOutput.asText.get().trim().toInt()
}.getOrDefault(1)

// Helper to get Year and Week
val calendar: Calendar = Calendar.getInstance()
val year: String = SimpleDateFormat("yy").format(calendar.time) // e.g., "26"
val week = calendar.get(Calendar.WEEK_OF_YEAR).toString().padStart(2, '0') // e.g., "14"

extra.apply {
    val branch = getBranchName()
    val count = getCommitCount()
    val generatedVersionCode = "$year$week${count.toString().padStart(4, '0')}".toInt()
    val generatedVersionName = "1.0-$branch-$year.$week.$count"

    set("compileSdkVersion", 37)
    set("mainAppId", "com.hoang.sleep")
    set("minSdkVersion", 26)
    set("targetSdkVersion", 37)
    set("versionCode", generatedVersionCode)
    set("versionName", generatedVersionName)
    set("jvmToolchain", 17)
    set("sourceCompatibility", JavaVersion.VERSION_17)
    set("targetCompatibility", JavaVersion.VERSION_17)
}

println("$extra")

subprojects {
    apply {
        plugin("org.jlleitschuh.gradle.ktlint")
    }
    configurations.all {
        exclude(group = "com.google.protobuf", module = "protobuf-java")
        exclude(group = "com.google.firebase", module = "protolite-well-known-types")
    }
}

jacoco {
    toolVersion = libs.versions.jacoco.toString()
}

tasks.register("clean", Delete::class) {
    description = "clean"
    delete(rootProject.layout.buildDirectory.asFile)
}

val testWithLint = tasks.register("testWithLint") {
    group = "verification"
    description = "run unit tests with lint check"

    dependsOn(
        getTasksByName("test", true),
        getTasksByName("ktlintCheck", true),
    )
}

tasks.register("fullVerifyAndBuild") {
    group = "verification"
    description = "Runs ktlint, detekt, tests, and builds the APK"

    // This ensures it runs for all subprojects
    dependsOn(
        allprojects.map { it.tasks.matching { t -> t.name == "ktlintFormat" } },
        allprojects.map { it.tasks.matching { t -> t.name == "ktlintCheck" } },
        allprojects.map { it.tasks.matching { t -> t.name == "detekt" } },
        allprojects.map { it.tasks.matching { t -> t.name == "testDebugUnitTest" } },
        "rootCoverageReport",
        allprojects.map { it.tasks.matching { t -> t.name == "assembleDebug" } }
    )
}

rootCoverage {
    generateCsv = false
    generateHtml = true
    generateXml = true

    excludes =
        listOf(
            "**/config/**",
            "**/Dagger*",
            "**/hilt_aggregated_deps/**",
            "**/Hilt*",
            "**/*_Hilt*",
            "**/*ComponentTreeDeps",
            "**/codegen/**",
            "**/presentation/**",
            "**/generated/**",
            "**/*_Impl*",
            "**/ResearchApplication*",
            "**/*MainActivity*",
            "**/com/msc/**",
            "**/com/sec/healthresearch/backend/grpc/**",
            "**/ISACallbackStubAdapter.class",
            "**/ResearchAppDatabase*",
            "**/SHealthDataSource*",
        )

    executeAndroidTests = false
    executeUnitTests = true
    includeAndroidTestResults = false
    includeUnitTestResults = true
    includeNoLocationClasses = false
}
