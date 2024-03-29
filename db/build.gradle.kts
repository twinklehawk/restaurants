buildscript {
    dependencies {
        classpath("org.postgresql:postgresql:42.6.0")
    }
}

plugins {
    id("org.flywaydb.flyway") version "9.22.3"
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

flyway {
    // url, user, and password should be overridden with system properties for a real environment:
    // ./gradlew -Dflyway.url=jdbc:postgresql://localhost:5432/postgres -Dflyway.user=postgres -Dflyway.password=test-pass \
    //     -Dflyway.placeholders.username=test_user -Dflyway.placeholders.password=test_user_pass flywayMigrate
    url = "jdbc:postgresql://localhost:5432/postgres"
    user = "postgres"
    password = "test-pass"
    connectRetries = 10
    locations = arrayOf("filesystem:src/main/resources/db/migration/postgres")
    schemas = arrayOf("restaurants")
    placeholders = mapOf(
        "username" to "test_user",
        "password" to "test_user_pass",
        "schema" to "restaurants"
    )
}
