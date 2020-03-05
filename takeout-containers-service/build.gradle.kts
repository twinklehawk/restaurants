import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    groovy
    jacoco
    id("org.springframework.boot")
    id("io.freefair.lombok")
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation(platform(project(":platform")))
    implementation("net.plshark.users:users-client:0.1.0")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot.experimental:spring-boot-starter-data-r2dbc")
    implementation("io.r2dbc:r2dbc-postgresql")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.codehaus.groovy:groovy-all")
    testImplementation("org.spockframework:spock-core")
    testImplementation("org.hamcrest:hamcrest-core")
    testRuntimeOnly("net.bytebuddy:byte-buddy")
    testRuntimeOnly("org.objenesis:objenesis")
}

java { sourceCompatibility = JavaVersion.VERSION_1_8 }
tasks.generateLombokConfig { enabled = false }
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        html.isEnabled = false
        csv.isEnabled = false
    }
}
