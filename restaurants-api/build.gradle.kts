import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    jacoco
    kotlin("jvm")
}

dependencies {
    implementation(platform(project(":platform")))
    api("io.projectreactor:reactor-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("io.projectreactor:reactor-test")
}

java { sourceCompatibility = JavaVersion.VERSION_1_8 }
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
tasks.withType<Test> {
    useJUnitPlatform()
}
tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        html.isEnabled = false
        csv.isEnabled = false
    }
}
