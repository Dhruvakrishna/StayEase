plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.dagger.hilt.android")
  id("kotlin-kapt")
  id("org.jetbrains.kotlin.plugin.compose")
}

val hasGoogleServices = file("google-services.json").exists()
if (hasGoogleServices) {
  apply(plugin = "com.google.gms.google-services")
  apply(plugin = "com.google.firebase.crashlytics")
}

android {
  namespace = "com.example.stayease"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.example.stayease"
    minSdk = 24
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "com.example.stayease.TestRunner"
  }

  signingConfigs {
    create("release") {
      val storeFilePath = System.getenv("RELEASE_STORE_FILE")
      if (storeFilePath != null) storeFile = file(storeFilePath)
      storePassword = System.getenv("RELEASE_STORE_PASSWORD")
      keyAlias = System.getenv("RELEASE_KEY_ALIAS")
      keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
    }
  }

  buildTypes {
    debug { isMinifyEnabled = false }
    release {
      isMinifyEnabled = true
      signingConfig = signingConfigs.getByName("release")
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  flavorDimensions += "env"
  productFlavors {
    create("dev") {
      dimension = "env"
      applicationIdSuffix = ".dev"
      versionNameSuffix = "-dev"
      resValue("string", "app_name", "Stays")
      buildConfigField("Boolean", "FIREBASE_ENABLED", hasGoogleServices.toString())
      buildConfigField("String", "OVERPASS_BASE_URL", "\"https://overpass-api.de/\"")
      buildConfigField("String", "BOOKING_BASE_URL", "\"http://10.0.2.2:4010/\"")
    }
    create("stage") {
      dimension = "env"
      applicationIdSuffix = ".stage"
      versionNameSuffix = "-stage"
      resValue("string", "app_name", "Stays")
      buildConfigField("Boolean", "FIREBASE_ENABLED", hasGoogleServices.toString())
      buildConfigField("String", "OVERPASS_BASE_URL", "\"https://overpass-api.de/\"")
      buildConfigField("String", "BOOKING_BASE_URL", "\"http://10.0.2.2:4010/\"")
    }
    create("prod") {
      dimension = "env"
      resValue("string", "app_name", "Stays")
      buildConfigField("Boolean", "FIREBASE_ENABLED", hasGoogleServices.toString())
      buildConfigField("String", "OVERPASS_BASE_URL", "\"https://overpass-api.de/\"")
      buildConfigField("String", "BOOKING_BASE_URL", "\"https://api.example.com/\"")
    }
  }

  buildFeatures { compose = true; buildConfig = true }
  compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
  kotlinOptions { jvmTarget = "17" }
  packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}

dependencies {
  implementation(project(":core"))
  implementation(project(":domain"))
  implementation(project(":data"))
  implementation(project(":feature:auth"))
  implementation(project(":feature:hotels"))
  implementation(project(":feature:details"))
  implementation(project(":feature:bookings"))
  implementation(project(":feature:asyncdemo"))

  val composeBom = platform("androidx.compose:compose-bom:2025.01.00")
  implementation(composeBom)
  androidTestImplementation(composeBom)

  implementation("androidx.activity:activity-compose:1.10.0")
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-tooling-preview")
  debugImplementation("androidx.compose.ui:ui-tooling")
  implementation("androidx.compose.material3:material3:1.3.1")
  implementation("androidx.compose.material:material-icons-extended")
  implementation("androidx.navigation:navigation-compose:2.8.6")

  implementation("org.osmdroid:osmdroid-android:6.1.18")

  implementation("com.google.dagger:hilt-android:2.52")
  kapt("com.google.dagger:hilt-android-compiler:2.52")
  implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

  implementation("androidx.work:work-runtime-ktx:2.9.1")
  implementation("androidx.hilt:hilt-work:1.2.0")
  kapt("androidx.hilt:hilt-compiler:1.2.0")

  implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
  implementation("com.google.firebase:firebase-analytics-ktx")
  implementation("com.google.firebase:firebase-crashlytics-ktx")
  implementation("com.google.firebase:firebase-auth-ktx")

  androidTestImplementation("com.google.dagger:hilt-android-testing:2.52")
  kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.52")
  androidTestImplementation("androidx.test.ext:junit:1.2.1")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")
  debugImplementation("androidx.compose.ui:ui-test-manifest")

  testImplementation("junit:junit:4.13.2")
}
