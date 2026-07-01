import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val keystoreProperties = Properties().apply {
    val file = rootProject.file("keystore.properties")
    if (file.exists()) {
        file.inputStream().use(::load)
    }
}

fun signingValue(propertyName: String, envName: String): String {
    return keystoreProperties.getProperty(propertyName)
        ?: System.getenv(envName)
        ?: ""
}

val releaseStoreFile = signingValue("storeFile", "MOBILEDECK_RELEASE_STORE_FILE")
val hasReleaseSigning = releaseStoreFile.isNotBlank() &&
    signingValue("storePassword", "MOBILEDECK_RELEASE_STORE_PASSWORD").isNotBlank() &&
    signingValue("keyAlias", "MOBILEDECK_RELEASE_KEY_ALIAS").isNotBlank() &&
    signingValue("keyPassword", "MOBILEDECK_RELEASE_KEY_PASSWORD").isNotBlank()

android {
    namespace = "com.remerer.mobiledeck"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.remerer.mobiledeck"
        minSdk = 24
        targetSdk = 35
        versionCode = 20
        versionName = "1.3.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            if (hasReleaseSigning) {
                storeFile = rootProject.file(releaseStoreFile)
                storePassword = signingValue("storePassword", "MOBILEDECK_RELEASE_STORE_PASSWORD")
                keyAlias = signingValue("keyAlias", "MOBILEDECK_RELEASE_KEY_ALIAS")
                keyPassword = signingValue("keyPassword", "MOBILEDECK_RELEASE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = if (hasReleaseSigning) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
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
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.okhttp)
    implementation(libs.play.services.code.scanner)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
