enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "WaterReminder"

include(":androidApp")
include(":shared")
include(":core:domain")
include(":core:data")
include(":core:presentation")
include(":core:ui")
include(":core:notification")
include(":feature:water")
include(":di")
