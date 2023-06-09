plugins {
    id("kotlin-config")
    id("dependency-config")
}

dependencies {
    api("io.projectreactor:reactor-core")
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("io.mockk:mockk")
    testImplementation("io.projectreactor:reactor-test")
}
