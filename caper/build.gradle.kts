import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")

    id("org.springframework.boot") version "2.4.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))

    //spring
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")

    //kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    //graphql
    implementation("com.expediagroup", "graphql-kotlin-spring-server", "4.+")

    //firebase
    implementation("com.google.firebase:firebase-admin:7.1.0")

    // sql connections and orm
    implementation("org.jetbrains.exposed", "exposed-core", "0.+")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.+")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.+")
    implementation("com.google.cloud.sql", "postgres-socket-factory", "1.+")
    implementation("org.postgresql", "postgresql")

    // testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.+")
}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
        incremental = true
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}