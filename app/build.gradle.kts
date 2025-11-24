plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.boxing.gestioncanina"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.boxing.gestioncanina"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            // NO agregamos applicationIdSuffix porque Firebase necesita el package exacto
            versionNameSuffix = "-DEBUG"
        }

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
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ========================================
    // CORE ANDROID
    // ========================================
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // ========================================
    // FIREBASE BoM (Bill of Materials)
    // ========================================
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

    // Firebase Services (sin versión, las maneja el BoM)
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // ========================================
    // FIREBASE APP CHECK
    // ========================================
    // Dependencia base de App Check
    implementation("com.google.firebase:firebase-appcheck")
    implementation(libs.androidx.activity)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Debug Provider (solo para desarrollo)
    debugImplementation("com.google.firebase:firebase-appcheck-debug")

    // Play Integrity Provider (para producción)
    releaseImplementation("com.google.firebase:firebase-appcheck-playintegrity")

    // ========================================
    // COROUTINES
    // ========================================
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")

    // ========================================
    // LIFECYCLE & VIEWMODEL
    // ========================================
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // Fragment KTX (útil si usas fragments)
    implementation("androidx.fragment:fragment-ktx:1.8.5")

    // ========================================
    // NAVEGACIÓN (Opcional - si usas Navigation Component)
    // ========================================
    // implementation("androidx.navigation:navigation-fragment-ktx:2.8.5")
    // implementation("androidx.navigation:navigation-ui-ktx:2.8.5")

    // ========================================
    // UTILIDADES ADICIONALES (Opcional)
    // ========================================
    // Para manejo de imágenes
    // implementation("io.coil-kt:coil:2.7.0")

    // Para manejo de fechas
    // implementation("com.jakewharton.threetenabp:threetenabp:1.4.7")

    // ========================================
    // TESTING
    // ========================================
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test:rules:1.6.1")
}