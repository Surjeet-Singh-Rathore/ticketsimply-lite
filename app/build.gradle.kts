import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services.gms)
    alias(libs.plugins.mapsplatform.secrets)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.devtools.ksp)
     alias(libs.plugins.sentry.android)
    alias(libs.plugins.org.jetbrains.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    compileSdk = 35
    namespace = "com.bitla.ticketsimply"

    defaultConfig {
        applicationId = "com.bitla.ticketsimply"
        minSdk = 24
        targetSdk = 35
        versionCode = 1200
        versionName = "7.0.6.1"

//        externalNativeBuild {
//            // For ndk-build, instead use the ndkBuild block.
//            cmake {
//                // Passes optional arguments to CMake.
//                arguments += listOf("-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON")
//            }
//        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        bundle {
            language {
                enableSplit = false
            }
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-XXLanguage:-ProperCheckAnnotationsTargetInTypeUsePositions")
//        useIR = true
//        freeCompilerArgs += ["-P", "plugjvmOptions += listOf("-Xms4000m", "-Xmx4000m", "-XX:+HeapDumpOnOutOfMemoryError")in:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"]
//        freeCompilerArgs += ["-Xallow-jvm-ir-dependencies", "-Xskip-prerelease-check"]
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    flavorDimensions.add("buildTypes")

    productFlavors {

        create("production") {
            dimension = "buildTypes"
            buildConfigField("String", "BASE_URL", "\"http://mba.ticketsimply.com\"")
        }

        create("development") {
            buildConfigField("String", "BASE_URL", "\"http://mba.ticketsimply.com\"")
        }

        create("Demo") {
            buildConfigField("String", "BASE_URL", "\"http://mba.ticketsimply.com\"")
//            buildConfigField "String", "BASE_URL", '"http://mba-stg.ticketsimply.com/"'
//            buildConfigField "String", "BASE_URL", '"http://mba.ticketsimply.com/"'
//            buildConfigField "String", "BASE_URL", '"http://chilestg-r5.ticketsimply.us/"'
//            buildConfigField "String", "BASE_URL", '"http://mba-stg.ticketsimply.id/"'
        }
    }
    ndkVersion = "27.1.12297006"
    externalNativeBuild {
        ndkBuild {
            path = File("src/main/jni/Android.mk")//path of Android.mk file
        }
    }
    namespace = "com.bitla.ts"
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }

    buildFeatures {
        buildConfig = true
    }

    /**
     * @Reference https://developers.google.com/maps/documentation/android-sdk/start#kotlin
     */
    secrets {
        // Optionally specify a different file name containing your secrets.
        // The plugin defaults to "local.properties"

        propertiesFileName = "secrets.properties"

        // A properties file containing default secret values. This file can be
        // checked in version control.
        defaultPropertiesFileName = "local.defaults.properties"

        // Configure which keys should be ignored by the plugin by providing regular expressions.
        // "sdk.dir" is ignored by default.
        ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
        ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
    }


}

dependencies {
    implementation(project(":escposprinter"))
    implementation(project(":tscalender"))
    implementation(project(":restaurant_app"))
    implementation(project(":coach"))
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.easypermissions.ktx)
    implementation(libs.converter.scalars)
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.legacy.support.v4)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation(libs.zxing.android.embedded)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.fragment.ktx)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.circleindicator)
    implementation(libs.whynotimagecarousel)
//    implementation(libs.core)
//    implementation(libs.play.core.ktx)
    implementation(libs.update.ktx)
    implementation(libs.asset.delivery.ktx)
    implementation(libs.feature.delivery.ktx)
    //  facebook shimmer loading
    implementation(libs.shimmer)
    //  circle imageview
    implementation(libs.circleimageview)
    //  Biometric Authentication
    implementation(libs.biometric)
    //  pagination
    implementation(libs.paging.runtime.ktx)
    //  lottie
    implementation(libs.lottie)
    //  timber
    implementation(libs.timber)
    //  glide
    implementation(libs.glide)
    //  dashboard chart
    implementation(libs.mpandroidchart)
    //  gson
    implementation(libs.gson)
    // LiveData & ViewModel
    implementation(libs.core.runtime)
    implementation(libs.common)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    //noinspection UseTomlInstead
    implementation("android.arch.lifecycle:extensions:1.1.1")
    implementation(libs.lifecycle.runtime.ktx)
    // Kotlin coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    // Koin main features for Android (Scope,ViewModel ...)
    implementation(libs.koin.android)
    // Koin Android - experimental builder extensions
    implementation(libs.koin.android.ext)
    // Koin for Jetpack Compose (unstable version)
    implementation(libs.koin.androidx.compose)
    // retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(platform(libs.androidx.compose.bom))
    // Firebase
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.config)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.firebase.perf)
    // disabling automatic initialization for all your components and dependencies
    implementation(libs.startup.runtime)
    // Utilities for Maps SDK for Android (requires Google Play Services)
    implementation(libs.android.maps.utils)
    implementation(libs.zxing.core)
    implementation(libs.calendarview)
    implementation(libs.commons.codec)
    implementation(libs.ssp.android)
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)
    implementation(libs.sdp.android)
    // An Android library to hook and fix Toast BadTokenException
    implementation(libs.toastcompat)
    // implementation "androidx.datastore:datastore-preferences:1.0.0"
    implementation(libs.preference.ktx)
    implementation(libs.printerlibrary)
    implementation(libs.qrgenerator)
    //implementation(libs.material.calendar.view)
    implementation(libs.balloon)
    implementation(libs.applandeo.material.calendar.view)
    // compose
    implementation(libs.activity.compose)
    implementation(libs.androidx.material)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.ui.tooling.preview)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.viewbinding)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.constraintlayout.compose)
    // implementation(libs.android.pdf.viewer)
    implementation(libs.seatbookview)
    implementation(libs.coil.compose)
//    implementation fileTree (dir: 'libs', include: ['*.aar'])
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
    implementation(libs.slf4j.api)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.datastore.preferences)
    //PhonePe Release
    implementation(libs.intentsdk)
    //razorpay
    implementation(libs.checkout)
    implementation("androidx.compose.foundation:foundation:1.7.8")
    implementation("com.otaliastudios:zoomlayout:1.9.0")


    // Encrypted Shared Preferences
    implementation(libs.securePreference)
    implementation(libs.flexbox)




}