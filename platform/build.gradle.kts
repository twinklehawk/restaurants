plugins {
    id("java-platform")
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:3.0.6"))

    constraints {
        api("io.mockk:mockk:1.13.5")
    }
}
