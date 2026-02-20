plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    kotlin("plugin.serialization") version "2.0.21"
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("com.google.dagger:hilt-android:2.52")
                implementation("com.squareup.retrofit2:retrofit:2.11.0")
                implementation("androidx.work:work-runtime-ktx:2.10.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

android {
    namespace = "com.example.stayease.core"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    // Production optimization: ignore the old 'main' directory to avoid redeclaration errors
    // since we are migrating to KMP source sets (commonMain, androidMain).
    sourceSets {
        getByName("main") {
            java.setSrcDirs(emptyList<String>())
            kotlin.setSrcDirs(emptyList<String>())
        }
    }
}

dependencies {
    "kapt"("com.google.dagger:hilt-android-compiler:2.52")
}
