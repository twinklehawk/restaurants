plugins {
    java
    groovy
    jacoco
    id("org.springframework.boot")
    id("io.freefair.lombok")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(platform(project(":platform")))
    implementation("net.plshark.users:users-client:0.1.0")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot.experimental:spring-boot-starter-data-r2dbc")
    implementation("io.r2dbc:r2dbc-postgresql")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.codehaus.groovy:groovy-all")
    testImplementation("org.spockframework:spock-core")
    testImplementation("org.hamcrest:hamcrest-core")
    testImplementation("com.opentable.components:otj-pg-embedded")
    testRuntimeOnly("net.bytebuddy:byte-buddy")
    testRuntimeOnly("org.objenesis:objenesis")
}

tasks.generateLombokConfig { enabled = false }
tasks.compileJava { options.compilerArgs.add("-parameters") }
tasks.compileTestJava { options.compilerArgs.add("-parameters") }
tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        html.isEnabled = false
        csv.isEnabled = false
    }
}
