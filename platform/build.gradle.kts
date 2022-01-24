plugins {
    id("java-platform")
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:2.6.3"))

    constraints {
        api("net.plshark:user-error-client:0.4.1")
        api("io.mockk:mockk:1.12.2")
    }
}
