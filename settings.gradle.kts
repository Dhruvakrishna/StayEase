pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories { google(); mavenCentral() }
}
rootProject.name = "StayEase"
include(":app", ":core", ":domain", ":data")
include(":feature:auth", ":feature:hotels", ":feature:details", ":feature:bookings")
