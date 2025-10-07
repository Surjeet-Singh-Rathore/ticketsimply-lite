plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.google.services.gms) apply false
    alias(libs.plugins.mapsplatform.secrets) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.org.jetbrains.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    id("androidx.navigation.safeargs") version "2.5.3" apply false
}

// Force Java 17 toolchain for all subprojects
subprojects {
    afterEvaluate {
        if (extensions.findByType<org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions>() != null) {
            extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions> {
                jvmTarget = "17"
            }
        }

        if (extensions.findByType<com.android.build.gradle.BaseExtension>() != null) {
            extensions.configure<com.android.build.gradle.BaseExtension> {
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
            }
        }
    }
}
