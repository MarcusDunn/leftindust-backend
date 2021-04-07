import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("plugin.allopen")

    // spring
    id("org.springframework.boot")
    id("io.spring.dependency-management")

    // liquibase
    id("org.liquibase.gradle")
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    val graphQLKotlinVersion = "4.0.0-alpha.17"
    val ktorVersion = "1.5.0"
    val biweeklyVersion = "0.6.6"
    val firebaseVersion = "7.0.1"
    val liquibaseVersion = "3.10.1"
    val coroutinesVersion = "1.4.3"
    val springBootVersion = "2.4.4"


    // spring
    implementation("org.springframework.boot", "spring-boot-starter", springBootVersion)
    implementation("org.springframework.boot", "spring-boot-starter-log4j2", springBootVersion)
    implementation("org.springframework.boot", "spring-boot-starter-jdbc", springBootVersion)
    implementation("org.springframework.boot", "spring-boot-starter-data-jpa", springBootVersion)


    // kotlin
    implementation("org.jetbrains.kotlin", "kotlin-reflect")
    implementation("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", coroutinesVersion)

    // graphql kotlin
    implementation("com.expediagroup", "graphql-kotlin-spring-server", graphQLKotlinVersion)

    // ktor
    implementation("io.ktor", "ktor-client", ktorVersion)
    implementation("io.ktor", "ktor-client-cio", ktorVersion)
    implementation("io.ktor", "ktor-client-gson", ktorVersion)

    // biweekly
    implementation("net.sf.biweekly", "biweekly", biweeklyVersion)

    // hibernate model code generation
    implementation("org.hibernate", "hibernate-jpamodelgen", "5.4.12.Final")
    kapt("org.hibernate", "hibernate-jpamodelgen", "5.4.12.Final")

    // firebase
    implementation("com.google.firebase", "firebase-admin", firebaseVersion)

    // database drivers
    implementation("org.postgresql", "postgresql")
    testImplementation("com.h2database", "h2")

    // liquibase
    implementation("org.liquibase", "liquibase-core", liquibaseVersion)

    // liquibase runtime dependencies
    liquibaseRuntime("org.postgresql", "postgresql")
    liquibaseRuntime("org.liquibase", "liquibase-core", "4.2.2")
    liquibaseRuntime("org.liquibase.ext", "liquibase-hibernate5", "4.2.2")
    liquibaseRuntime("org.springframework.boot", "spring-boot-starter-data-jpa")
    liquibaseRuntime("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    liquibaseRuntime("net.sf.biweekly", "biweekly", "0.6.6")
    liquibaseRuntime(sourceSets.main.get().output)

    testImplementation("com.expediagroup", "graphql-kotlin-spring-client", graphQLKotlinVersion)

    // spring testing
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }

    testImplementation("com.ninja-squad", "springmockk", "3.0.1")
}

// remove logback in favor of slf4j
configurations {
    all {
        exclude(module = "spring-boot-starter-logging")
        exclude(module = "logback-classic")
    }
}

// liquibase plugin config
liquibase {
    activities.register("main") {
        arguments = mapOf(
            "logLevel" to "info",
            "changeLogFile" to "src/main/resources/dbchangelog.xml",
            "url" to "jdbc:postgresql://127.0.0.1:5432/mediq",
            "username" to "mediq",
            "password" to "mediq",
            "referenceDriver" to "liquibase.ext.hibernate.database.connection.HibernateDriver",
            "referenceUrl" to "hibernate:spring:com.leftindust.mockingbird.dao.entity?" +
                    "dialect=org.hibernate.dialect.PostgreSQLDialect&" +
                    "hibernate.physical_naming_strategy=org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy&" +
                    "hibernate.implicit_naming_strategy=org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy"
        )
    }
    runList = "main"
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
    useJUnitPlatform {
        includeTags("Integration")
    }
}

val performanceTest = task<Test>("performanceTest") {
    useJUnitPlatform {
        includeTags("Performance")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        useIR = true
        jvmTarget = "${JavaVersion.VERSION_1_8}"
        allWarningsAsErrors = true
    }
}

kapt {
    includeCompileClasspath = false
}