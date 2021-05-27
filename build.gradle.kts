plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("kapt") version "1.5.10"
    kotlin("plugin.spring") version "1.5.10"
    kotlin("plugin.allopen") version "1.5.10"
    kotlin("plugin.jpa") version "1.5.10"

    // spring
    id("org.springframework.boot") version "2.5.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"

    // liquibase
    id("org.liquibase.gradle") version "2.0.4"
}

repositories {
    mavenCentral()
    jcenter()
}

group = "com.leftindust"
version = "1.0-SNAPSHOT"