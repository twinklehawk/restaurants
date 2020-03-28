plugins {
    id("java-platform")
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:2.3.0.M3"))

    constraints {
        api("net.plshark.users:users-client:0.2.3")
    }
}
