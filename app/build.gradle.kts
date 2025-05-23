plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.translation"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.translation"
        minSdk = 28
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
    composeOptions{
        kotlinCompilerExtensionVersion="1.5.3"
    }

    packagingOptions {
        pickFirst ("META-INF/INDEX.LIST") // 选择第一个遇到的文件
        pickFirst ("META-INF/DEPENDENCIES")
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
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.constraintlayout:constraintlayout:2.2.0-alpha13")
    implementation(libs.androidx.material3)
    implementation(libs.androidx.camera.view)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
//    implementation ("androidx.activity:activity-compose:1.7.2")
//    implementation ("androidx.compose.ui:ui:1.5.0")
//    implementation ("androidx.compose.material3:material3:1.1.1")
//    implementation ("androidx.compose.ui:ui-tooling-preview:1.5.0")
//    debugImplementation ("androidx.compose.ui:ui-tooling:1.5.0")
//
//    //yzh_start
//    // 相机X库
//    implementation("androidx.camera:camera-core:1.3.0")
//    implementation("androidx.camera:camera-camera2:1.3.0")
//    implementation("androidx.camera:camera-lifecycle:1.3.0")
//    implementation("androidx.camera:camera-view:1.3.0")
//
//    // ML Kit文字识别（Google官方OCR）
//    implementation("com.google.mlkit:text-recognition:16.0.0")
//
//    // 翻译API（推荐使用Google）
//    implementation("com.google.cloud:google-cloud-translate:2.2.0")
//    implementation ("com.google.api.grpc:proto-google-common-protos:2.9.0")
//    //yzh_end

    //lcx
    //whx
}