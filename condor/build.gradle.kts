import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")

    // spring
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    val coroutinesVersion = "1.4.3"
    val graphQLKotlinVersion = "4.0.0-alpha.15"


    // spring
    implementation("org.springframework.boot", "spring-boot-starter")
    implementation("org.springframework.boot", "spring-boot-starter-jdbc")

    // graphql-kotlin
    implementation("com.expediagroup", "graphql-kotlin-spring-server", graphQLKotlinVersion)


    // kotlin
    implementation("org.jetbrains.kotlin", "kotlin-reflect")
    implementation("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", coroutinesVersion)

    // database drivers
    implementation("org.postgresql", "postgresql")

    // spring testing
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.mockk", "mockk", "1.10.6")

}

// kotlin compiler args
tasks.withType<KotlinCompile> {
    kotlinOptions {
        useIR = true // this will be default on 1.5 which I plan to switch to
        jvmTarget = "${JavaVersion.VERSION_1_8}"
        allWarningsAsErrors = true
    }
}

// test properties
tasks.withType<Test> {
    useJUnitPlatform {
        excludeTags("Integration")
    }

    testLogging {
        events(
            TestLogEvent.FAILED,
            TestLogEvent.STANDARD_ERROR
        )
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}

val integrationTest = task<Test>("integrationTest") {
    // may need to make this single threaded.

    useJUnitPlatform {
        includeTags("Integration")
    }
}
