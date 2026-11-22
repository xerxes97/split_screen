import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

android {
    namespace = "com.example.split_screen"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.split_screen"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", "\"${localProperties.getProperty("BASE_URL", "")}\"")
        buildConfigField("String", "API_KEY", "\"${localProperties.getProperty("API_KEY", "")}\"")
        // Play product ID: empty (Pro unavailable) or lowercase letters, digits, '_' and '.'.
        val proProductId = localProperties.getProperty("PRO_PRODUCT_ID", "").trim()
        if (proProductId.isNotEmpty() && !Regex("^[a-z0-9][a-z0-9_.]*$").matches(proProductId)) {
            throw GradleException("Invalid PRO_PRODUCT_ID in local.properties: '$proProductId'")
        }
        buildConfigField("String", "PRO_PRODUCT_ID", "\"$proProductId\"")
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.android.billing.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.google.material)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
// MergedManifestPermissionsTest reads the debug manifest merger report.
tasks.matching { it.name.startsWith("test") && it.name.endsWith("UnitTest") }.configureEach {
    dependsOn("processDebugMainManifest")
}
