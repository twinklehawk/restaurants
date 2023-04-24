plugins {
    kotlin("jvm") version "1.7.22" apply false
    kotlin("plugin.spring") version "1.7.22" apply false
    id("org.springframework.boot") version "3.0.6" apply false
    id("io.gitlab.arturbosch.detekt") version "1.22.0" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
    group = "net.plshark.restaurants"
    version = "0.0.1"
}

configure(subprojects.filter { it.name != "platform" && it.name != "db" } ) {
    apply(plugin = "jacoco")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    tasks.withType<JacocoReport> {
        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
        allRules = true
        buildUponDefaultConfig = true
        jvmTarget = "1.8"
    }
}

task("printVersion") {
    doFirst {
        println("$version")
    }
}
