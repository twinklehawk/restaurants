rootProject.name = "restaurants"

pluginManagement {
    val kotlinVersion: String by settings
    val springVersion: String by settings

    plugins {
        kotlin("plugin.spring") version kotlinVersion
        id("org.springframework.boot") version springVersion
    }
}

include(
    "db",
    "restaurants-api",
    "restaurants-service",
)
