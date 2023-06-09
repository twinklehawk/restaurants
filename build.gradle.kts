plugins {
    kotlin("jvm") apply false
}

allprojects {
    repositories {
        mavenCentral()
    }

    group = "net.plshark.restaurants"
    version = "0.0.1"
}

task("printVersion") {
    doFirst {
        println("$version")
    }
}
