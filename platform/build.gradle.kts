plugins {
    id("java-platform")
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:2.3.1.RELEASE"))

    constraints {
        api("net.plshark.users:users-client:0.2.3")
        api("io.mockk:mockk:1.10.0")
    }
}
