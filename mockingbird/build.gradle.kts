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
    val graphQLKotlinVersion = "4.0.0-alpha.10"
    val ktorVersion = "1.5.0"
    val biweeklyVersion = "0.6.6"
    val firebaseVersion = "7.0.1"
    val liquibaseVersion = "3.10.1"
    val coroutinesVersion = "1.4.3"


    // spring
    implementation("org.springframework.boot", "spring-boot-starter")
    implementation("org.springframework.boot", "spring-boot-starter-log4j2")
    implementation("org.springframework.boot", "spring-boot-starter-jdbc")
    implementation("org.springframework.boot", "spring-boot-starter-data-jpa")


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

    // graphql kotlin client currently has an issue with 2.12, forcing this fixes it for now
    implementation("com.fasterxml.jackson.core:jackson-databind") {
        version {
            strictly("2.11.1")
        }
    }

    // hibernate model code generation
    implementation("org.hibernate:hibernate-jpamodelgen:5.4.12.Final")
    kapt("org.hibernate:hibernate-jpamodelgen:5.4.12.Final")

    // firebase
    implementation("com.google.firebase", "firebase-admin", firebaseVersion)

    // database drivers
    implementation("org.postgresql", "postgresql")

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

    // spring testing
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    // faker
    testImplementation("io.github.serpro69:kotlin-faker:1.6.0")
}

// remove logback in favor of slf4j
configurations {
    all {
        exclude(module = "spring-boot-starter-logging")
        exclude(module = "logback-classic")
    }
}

project.the<SourceSetContainer>()["main"]

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
            "referenceUrl" to "hibernate:spring:com.leftindust.leftindust.dao.entity?" +
                    "dialect=org.hibernate.dialect.PostgreSQLDialect&" +
                    "hibernate.physical_naming_strategy=org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy&" +
                    "hibernate.implicit_naming_strategy=org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy"
        )
    }
    runList = "main"
}

// test properties
tasks.withType<Test> {
    useJUnitPlatform()

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

// koltin compiler args
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