plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.aireadevs.sendSmsToMail.sms2mail"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.aireadevs.sendSmsToMail.sms2mail"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isShrinkResources = false
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isShrinkResources = false
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    //* MATERIAL *//
    implementation(libs.androidx.material3)
    //*DAGGER HILT *//
    implementation(libs.hilt.android)
    ksp(libs.dagger.hilt.android.compiler)
    //Serializacion GSON
    implementation(libs.gson)
    //* DATASTORE *//
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.datastore.preferences)
    //* ROOM *//
    implementation(libs.room.room.ktx)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    //* SPLASHSCREEN *//
    implementation(libs.androidx.core.splashscreen)

    //* TEST *//
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}