plugins { id("org.jetbrains.kotlin.jvm") }
kotlin { jvmToolchain(17) }
dependencies {
  implementation(project(":core"))
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
  implementation("androidx.paging:paging-common:3.3.5")
  implementation("javax.inject:javax.inject:1")

  testImplementation("junit:junit:4.13.2")
  testImplementation("io.mockk:mockk:1.13.12")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
  testImplementation("com.google.truth:truth:1.4.2")
}
