plugins {
    kotlin("jvm") version "1.4.31"
    kotlin("kapt") version "1.4.31"
    kotlin("plugin.spring") version "1.4.0"
    kotlin("plugin.allopen") version "1.4.21"
    kotlin("plugin.jpa") version "1.4.21"

    // spring
    id("org.springframework.boot") version "2.3.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"

    // liquibase
    id("org.liquibase.gradle") version "2.0.4"
}

repositories {
    mavenCentral()
    jcenter()
}

group = "com.leftindust"
version = "1.0-SNAPSHOT"