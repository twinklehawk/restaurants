plugins {
    kotlin("jvm") version "1.8.21" apply false
    kotlin("plugin.spring") version "1.8.21" apply false
    id("org.springframework.boot") version "3.1.0" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.0" apply false
    id("org.jmailen.kotlinter") version "3.15.0" apply false
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
    apply(plugin = "org.jmailen.kotlinter")

    tasks.withType<JacocoReport> {
        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
        buildUponDefaultConfig = true
        jvmTarget = "17"
    }
}

task("printVersion") {
    doFirst {
        println("$version")
    }
}
