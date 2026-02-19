plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("com.google.dagger.hilt.android")
  id("kotlin-kapt")
  id("org.jetbrains.kotlin.plugin.compose")
}
android {
  namespace = "com.example.stayease.feature.hotels"
  compileSdk = 35
  defaultConfig { minSdk = 24 }
  buildFeatures { compose = true }
  compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
  kotlinOptions { jvmTarget = "17" }
}
dependencies {
  implementation(project(":core"))
  implementation(project(":domain"))
  implementation(project(":data"))

  val composeBom = platform("androidx.compose:compose-bom:2025.01.00")
  implementation(composeBom)
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-tooling-preview")
  debugImplementation("androidx.compose.ui:ui-tooling")
  implementation("androidx.compose.material3:material3:1.3.1")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
  implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
  implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
  implementation("com.google.dagger:hilt-android:2.52")
  kapt("com.google.dagger:hilt-android-compiler:2.52")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
  implementation("androidx.paging:paging-compose:3.3.5")
  implementation("io.coil-kt:coil-compose:2.7.0")

  implementation("com.google.android.gms:play-services-location:21.3.0")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")
}
