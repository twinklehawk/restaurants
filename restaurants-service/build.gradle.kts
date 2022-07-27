import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot")
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation(enforcedPlatform(project(":platform")))
    implementation(project(":restaurants-api"))
    implementation("net.plshark:user-error-client")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    runtimeOnly("org.postgresql:r2dbc-postgresql")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.flywaydb:flyway-core")
    testImplementation("io.mockk:mockk")
    testImplementation("io.projectreactor:reactor-test")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.postgresql:postgresql")
    testRuntimeOnly(project(":db"))
}

java { sourceCompatibility = JavaVersion.VERSION_1_8 }
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
tasks.withType<Test> {
    val runIntTests = System.getProperties().getProperty("runIntTests") == "true"
    useJUnitPlatform {
        if (!runIntTests)
            excludeTags("integrationTest")
    }
}
