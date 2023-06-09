plugins {
    id("io.spring.dependency-management")
}

dependencyManagement {
    val springVersion: String by properties

    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:$springVersion")
    }
    dependencies {
        dependency("io.mockk:mockk:1.13.5")
    }
}
