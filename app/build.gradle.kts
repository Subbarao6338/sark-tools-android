plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

fun getKeychainPassword(account: String, service: String): String {
    return try {
        Runtime.getRuntime()
            .exec(arrayOf("security", "find-generic-password", "-a", account, "-s", service, "-w"))
            .inputStream.bufferedReader().readLine() ?: ""
    } catch (e: Exception) {
        ""
    }
}

val githubRunNumber = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 14
val calculatedVersionCode = githubRunNumber
val major = 0
val minor = calculatedVersionCode / 10
val patch = calculatedVersionCode % 10
val calculatedVersionName = "$major.$minor.$patch"

android {
    namespace = "com.toolbox"
    compileSdk = 36

    defaultConfig {
        applicationId = "sark.tools"
        minSdk = 26
        targetSdk = 36
        versionCode = calculatedVersionCode
        versionName = calculatedVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resourceConfigurations += listOf("en")

        ndk {
            abiFilters.clear()
            abiFilters.add("arm64-v8a")
        }
    }

    signingConfigs {
        val releaseKeystore = run {
            val rootRelease = rootProject.file("release.jks")
            val homeRelease = file("${System.getProperty("user.home")}/.android/release.jks")
            if (rootRelease.exists()) rootRelease else homeRelease
        }
        val envPassword = System.getenv("KEYSTORE_PASSWORD") ?: System.getenv("KEY_PASSWORD") ?: ""
        val keychainPass = getKeychainPassword("release-key", "android-release-keystore")
        val fallbackPass = if (releaseKeystore.name == "release.jks") "sarktools" else "android"
        val resolvedPassword = envPassword.ifEmpty { keychainPass.ifEmpty { fallbackPass } }
        val resolvedAlias = System.getenv("KEY_ALIAS") ?: "sarktools"

        val sharedSigningConfig = create("shared") {
            if (releaseKeystore.exists()) {
                storeFile = releaseKeystore
                storePassword = resolvedPassword
                keyAlias = resolvedAlias
                keyPassword = resolvedPassword
            }
        }

        getByName("debug") {
            if (releaseKeystore.exists()) {
                storeFile = releaseKeystore
                storePassword = resolvedPassword
                keyAlias = resolvedAlias
                keyPassword = resolvedPassword
            }
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("shared")
        }
        release {
            signingConfig = signingConfigs.getByName("shared")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk.debugSymbolLevel = "FULL"
        }
    }

    buildFeatures {
        compose = true
    }

    kotlin {
        jvmToolchain(21)
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.animation)
    implementation(libs.compose.foundation)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.navigation.compose)

    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.service)

    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)

    implementation(libs.datastore.preferences)

    implementation(libs.zxing.core)
    implementation(libs.mlkit.text.recognition)
    implementation(libs.mlkit.document.scanner)

    implementation(libs.activity.compose)
    implementation(libs.core.ktx)
    implementation(libs.core.splashscreen)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.glance.appwidget)
    implementation(libs.glance.material3)

    implementation(libs.lottie.compose)
}
