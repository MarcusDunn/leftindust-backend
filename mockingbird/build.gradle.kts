import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")

    // spring
    id("org.springframework.boot") version "2.3.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"

    // liquibase
    id("org.liquibase.gradle") version "2.0.4"

    // compiler plugin
    id("org.jetbrains.kotlin.plugin.allopen")
}

repositories {
    mavenCentral()
}

dependencies {
    val graphQLKotlinVersion = "4.0.0-alpha.10"
    val ktorVersion = "1.5.0"

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("com.expediagroup:graphql-kotlin-spring-server:$graphQLKotlinVersion")
    implementation("io.ktor:ktor-client:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-gson:$ktorVersion")
    implementation("net.sf.biweekly:biweekly:0.6.6")

    implementation("com.fasterxml.jackson.core:jackson-databind") {
        version {
            strictly("2.11.1") // graphql kotlin client currently has an issue with 2.12, forcing this fixes it for now
        }
    }

    implementation("org.hibernate:hibernate-jpamodelgen:5.4.12.Final")
    kapt("org.hibernate:hibernate-jpamodelgen:5.4.12.Final")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("com.google.firebase:firebase-admin:7.0.1")
    implementation("org.postgresql:postgresql")
    implementation("com.h2database", "h2")

    implementation("org.liquibase:liquibase-core:3.10.1")

    liquibaseRuntime("org.postgresql:postgresql")
    liquibaseRuntime("org.liquibase:liquibase-core:4.2.2")
    liquibaseRuntime("org.liquibase.ext:liquibase-hibernate5:4.2.2")
    liquibaseRuntime("org.springframework.boot:spring-boot-starter-data-jpa")
    liquibaseRuntime("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    liquibaseRuntime("net.sf.biweekly:biweekly:0.6.6")

    liquibaseRuntime(sourceSets.main.get().output)

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    testImplementation("io.github.serpro69:kotlin-faker:1.6.0")
}


configurations {
    all {
        exclude(module = "spring-boot-starter-logging")
        exclude(module = "logback-classic")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
        freeCompilerArgs = listOf("-Xinline-classes")
    }
}

project.the<SourceSetContainer>()["main"]


liquibase {
    activities.register("main") {
        arguments = mapOf(
            "logLevel" to "info",
            "changeLogFile" to "src/main/resources/dbchangelog.xml",
            "url" to "jdbc:postgresql://127.0.0.1:5432/mediq",
            "username" to "mediq",
            "password" to "mediq",
            "referenceDriver" to "liquibase.ext.hibernate.database.connection.HibernateDriver",
            "referenceUrl" to "hibernate:spring:com.leftindust.mediq.dao.entity?" +
                    "dialect=org.hibernate.dialect.PostgreSQLDialect&" +
                    "hibernate.physical_naming_strategy=org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy&" +
                    "hibernate.implicit_naming_strategy=org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy"
        )
    }
    runList = "main"
}


tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        events(FAILED, STANDARD_ERROR, SKIPPED)
        exceptionFormat = FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}