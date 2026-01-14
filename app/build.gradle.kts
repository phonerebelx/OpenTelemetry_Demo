plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("net.bytebuddy.byte-buddy-gradle-plugin")
}

android {
    namespace = "com.example.otel_playground"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.otel_playground"
        minSdk = 26
        targetSdk = 36
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.fragment.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    api(platform("io.opentelemetry.android:opentelemetry-android-bom:1.0.1-alpha"))
    implementation("io.opentelemetry.android:android-agent")
    implementation("io.opentelemetry.android.instrumentation:view-click:1.0.1-alpha")
    implementation("io.opentelemetry.android.instrumentation:android-log-library:1.0.1-alpha")
    add("byteBuddy", "io.opentelemetry.android.instrumentation:android-log-agent:1.0.1-alpha")
    implementation("io.opentelemetry.android.instrumentation:httpurlconnection-library:1.0.1-alpha")
    byteBuddy("io.opentelemetry.android.instrumentation:httpurlconnection-agent:1.0.1-alpha")
    implementation("io.opentelemetry.android.instrumentation:sessions:1.0.1-alpha")
    implementation("io.opentelemetry.android.instrumentation:compose-click:1.0.1-alpha")
    implementation(libs.opentelemetry.exporter.otlp)
    implementation(libs.opentelemetry.api.incubator)
}