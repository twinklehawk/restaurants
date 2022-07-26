plugins {
    kotlin("jvm") version "1.7.10" apply false
    kotlin("plugin.spring") version "1.6.21" apply false
    id("org.springframework.boot") version "2.6.7" apply false
    id("io.gitlab.arturbosch.detekt") version "1.19.0" apply false
}

allprojects {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/twinklehawk/user-error")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GPR_USER")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GPR_TOKEN")
            }
        }
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
