plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("com.google.dagger.hilt.android")
  id("kotlin-kapt")
}
android {
  namespace = "com.example.stayease.data"
  compileSdk = 35
  defaultConfig { minSdk = 24 }
  compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
  kotlinOptions { jvmTarget = "17" }
  buildFeatures { buildConfig = true }
}
dependencies {
  implementation(project(":core"))
  implementation(project(":domain"))
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")

  api("com.squareup.retrofit2:retrofit:2.11.0")
  api("com.squareup.retrofit2:converter-moshi:2.11.0")
  api("com.squareup.okhttp3:okhttp:4.12.0")
  api("com.squareup.okhttp3:logging-interceptor:4.12.0")
  api("com.squareup.moshi:moshi-kotlin:1.15.1")
  kapt("com.squareup.moshi:moshi-kotlin-codegen:1.15.1")

  api("androidx.room:room-runtime:2.6.1")
  kapt("androidx.room:room-compiler:2.6.1")
  api("androidx.room:room-ktx:2.6.1")
  api("androidx.room:room-paging:2.6.1")

  implementation("androidx.datastore:datastore-preferences:1.1.1")
  api("androidx.paging:paging-runtime:3.3.5")

  implementation("com.google.dagger:hilt-android:2.52")
  kapt("com.google.dagger:hilt-android-compiler:2.52")

  implementation("com.google.android.gms:play-services-location:21.3.0")
  
  // Security for EncryptedSharedPreferences
  implementation("androidx.security:security-crypto:1.1.0-alpha06")

  testImplementation("junit:junit:4.13.2")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
  testImplementation("app.cash.turbine:turbine:1.1.0")
  testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}
