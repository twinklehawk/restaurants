import java.util.Properties
import kotlin.io.path.inputStream

plugins {
    `kotlin-dsl`
}

val props = Properties()
rootDir.toPath().resolveSibling("gradle.properties").inputStream().use {
    props.load(it)
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    val kotlinVersion: String = props["kotlinVersion"] as String

    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.3")
    implementation("org.jmailen.gradle:kotlinter-gradle:3.16.0")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.3")
}