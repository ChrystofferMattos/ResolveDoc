plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.resolvedoc"
    compileSdk = 35


    defaultConfig {
        applicationId = "com.example.resolvedoc"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }


}

dependencies {
        implementation(platform("androidx.compose:compose-bom:2024.09.02"))
        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.material3:material3")
        implementation("androidx.compose.material3:material3:1.4.0")
        implementation("androidx.compose.ui:ui-tooling-preview")
        implementation(libs.androidx.compose.ui.unit)
        implementation(libs.androidx.compose.foundation.layout)
        debugImplementation("androidx.compose.ui:ui-tooling")
        implementation("androidx.activity:activity-compose:1.9.3")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
        implementation("com.google.dagger:hilt-android:2.57.2")
        kapt("com.google.dagger:hilt-compiler:2.57.2")
        implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
        implementation("androidx.compose.material3:material3:1.4.0")
        implementation("androidx.work:work-runtime:2.9.1")
        implementation("androidx.hilt:hilt-work:1.2.0")
        implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
        implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
        implementation("com.google.firebase:firebase-analytics")
        implementation("com.google.firebase:firebase-auth")
        implementation("com.google.firebase:firebase-firestore")
        implementation("com.google.firebase:firebase-storage")
        implementation("com.google.dagger:hilt-android:hilt_version")
        kapt("com.google.dagger:hilt-compiler:hilt_version")
        implementation("androidx.compose.material:material-icons-extended:1.6.8")
        implementation("androidx.compose.material3:material3:1.4.0")
        debugImplementation("androidx.compose.ui:ui-test-manifest")
        debugImplementation("androidx.compose.ui:ui-tooling")
        implementation(libs.androidx.navigation.compose)
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.02"))
        androidTestImplementation("androidx.compose.ui:ui-test-junit4")
        debugImplementation("androidx.compose.ui:ui-test-manifest")
    }

