import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktlint)
    id("kotlin-parcelize")
}

android {
    namespace = "at.florianschuster.hydro"
    compileSdk = 34

    defaultConfig {
        applicationId = "at.florianschuster.hydro"
        minSdk = 31
        targetSdk = 34
        versionCode = 2
        versionName = "1.0.0"

        vectorDrawables {
            useSupportLibrary = true
        }

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    lint {
        abortOnError = true
    }

    signingConfigs {
        create("release") {
            storeFile =
                if (file("keystore.jks").exists()) {
                    file("keystore.jks")
                } else {
                    null
                }

            val localProperties = gradleLocalProperties(rootDir, providers)

            storePassword = localProperties.getProperty("signingStorePassword")
                ?: System.getenv("SIGNING_STORE_PASSWORD")
                ?: null

            keyAlias = localProperties.getProperty("signingKeyAlias")
                ?: System.getenv("SIGNING_KEY_ALIAS")
                ?: null

            keyPassword = localProperties.getProperty("signingKeyPassword")
                ?: System.getenv("SIGNING_KEY_PASSWORD")
                ?: null
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (file("keystore.jks").exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/versions/9/previous-compilation-data.bin"
        }
    }
}

dependencies {
    implementation(platform(libs.kotlin.bom))
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.process)
    implementation(libs.activity.compose)
    implementation(libs.core.splashscreen)

    implementation(platform(libs.compose.bom))
    implementation(libs.animation)
    implementation(libs.material3)
    implementation(libs.material.icons.extended)

    implementation(libs.ui.tooling.preview)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    implementation(libs.reimagined)
    implementation(libs.reimagined.material3)
    implementation(libs.lottie.compose)

    implementation(libs.browser)
    implementation(libs.datastore.preferences)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}
