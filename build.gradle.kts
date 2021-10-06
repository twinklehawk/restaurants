plugins {
    kotlin("jvm") version "1.3.72" apply false
    kotlin("plugin.spring") version "1.3.72" apply false
    id("org.springframework.boot") version "2.3.1.RELEASE" apply false
    id("com.github.ben-manes.versions") version "0.39.0"
    id("io.gitlab.arturbosch.detekt") version "1.10.0" apply false
}

allprojects {
    repositories {
        jcenter()
        maven { setUrl("https://dl.bintray.com/twinklehawk/maven") }
    }
    group = "net.plshark.takeoutcontainers"
    version = "0.0.1"
}

configure(subprojects.filter { it.name != "platform" && it.name != "db" } ) {
    apply(plugin = "jacoco")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    tasks.withType<JacocoReport> {
        reports {
            xml.isEnabled = true
            html.isEnabled = false
            csv.isEnabled = false
        }
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
        failFast = true
        buildUponDefaultConfig = true
        jvmTarget = "1.8"
    }

    dependencies {
        "detektPlugins"("io.gitlab.arturbosch.detekt:detekt-formatting:1.10.0")
    }
}

tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
    checkConstraints = true
    gradleReleaseChannel = "current"
}

task("printVersion") {
    doFirst {
        println("$version")
    }
}
