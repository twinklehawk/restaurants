plugins {
    id("org.springframework.boot") version "2.2.5.RELEASE" apply false
    id("io.freefair.lombok") version "4.1.6" apply false
}

allprojects {
    repositories {
        jcenter()
        maven { setUrl("https://repo.spring.io/milestone") }
        maven { setUrl("https://dl.bintray.com/twinklehawk/maven") }
    }
    group = "net.plshark.takeoutcontainers"
    version = "0.0.1"
}
