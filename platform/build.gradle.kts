plugins {
    id("java-platform")
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:2.5.4"))

    constraints {
        api("net.plshark.users:users-client:0.3.0")
        api("io.mockk:mockk:1.10.0")
    }
}
