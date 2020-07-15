plugins {
    kotlin("jvm") version "1.3.72" apply false
    kotlin("plugin.spring") version "1.3.72" apply false
    id("org.springframework.boot") version "2.3.1.RELEASE" apply false
    id("com.github.ben-manes.versions") version "0.28.0"
}

allprojects {
    repositories {
        jcenter()
        maven { setUrl("https://dl.bintray.com/twinklehawk/maven") }
    }
    group = "net.plshark.takeoutcontainers"
    version = "0.0.1"
}

tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
    checkConstraints = true
    gradleReleaseChannel = "current"
}
