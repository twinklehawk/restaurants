plugins {
    id("java-platform")
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:2.2.5.RELEASE"))
    api(enforcedPlatform("org.springframework.boot.experimental:spring-boot-bom-r2dbc:0.1.0.M3"))

    constraints {

    }
}
