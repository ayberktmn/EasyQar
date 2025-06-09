import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

// local.properties dosyasını yükle
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use {
        localProperties.load(it)
    }
}

val fcmCredentialsFile: String = localProperties.getProperty("fcm_credentials_file") ?: ""


android {
    namespace = "com.example.EasyQar"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.easyqar"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "FCM_CREDENTIALS_FILE", "\"$fcmCredentialsFile\"")

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    kotlin {
        jvmToolchain(17)
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "17"
            languageVersion = "1.9"
            apiVersion = "1.9"
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"

            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE"
            excludes +="/META-INF/LICENSE.txt"
            excludes +="/META-INF/NOTICE"
        }
    }
}

dependencies {

    // Core
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Compose
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Firebase BOM (firestore, auth, messaging, analytics)
    implementation (platform("com.google.firebase:firebase-bom:30.1.0"))

    // Firebase Auth
    implementation("com.google.firebase:firebase-auth")

    // Firebase Firestore
    implementation("com.google.firebase:firebase-firestore")

    // Firebase Cloud Messaging (FCM)
    implementation("com.google.firebase:firebase-messaging")

    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics")

    // ZXing (tek sürüm yeterli)
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.4")

    // CameraX
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.0.0")

    // ML Kit
    implementation("com.google.mlkit:barcode-scanning:17.0.0")

    // AppCompat
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Google Sign-In
    implementation(libs.googleid)

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    implementation(libs.volley)
    kapt("com.google.dagger:hilt-android-compiler:2.51")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation("androidx.hilt:hilt-work:1.0.0")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.0")  // veya uygun versiyon

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation ("androidx.compose.material:material:1.5.0")

    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.core:core:1.12.0")

    // Android Credentials
    implementation("androidx.credentials:credentials:1.2.0-alpha03")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.0-alpha03")

    implementation ("com.google.firebase:firebase-messaging:23.1.1")

    implementation("com.google.auth:google-auth-library-oauth2-http:1.18.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.squareup.moshi:moshi:1.12.0")

    implementation ("androidx.media3:media3-exoplayer:1.3.1")
    implementation ("androidx.media3:media3-ui:1.3.1")

    implementation ("com.airbnb.android:lottie-compose:5.0.3")

    implementation ("androidx.datastore:datastore-preferences:1.0.0")

    // Material 3 Compose
    implementation("androidx.compose.material3:material3:1.1.0")

    //Coil
    implementation("io.coil-kt:coil-compose:2.5.0")

    //Swipe Refresh
    implementation("com.google.accompanist:accompanist-swiperefresh:0.34.0")

    implementation ("com.google.accompanist:accompanist-pager:0.30.1")
    implementation ("com.google.accompanist:accompanist-pager-indicators:0.30.1")
}

kapt {
    correctErrorTypes = true
}