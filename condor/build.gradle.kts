import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")

    // spring
    id("org.springframework.boot") version "2.3.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"

    // graphql-kotlin
    id("com.expediagroup.graphql") version "4.0.0-alpha.14"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.springframework.boot", "spring-boot-starter")
    implementation("org.jetbrains.kotlin", "kotlin-reflect")
    implementation("org.quartz-scheduler", "quartz", "2.3.2")
    testImplementation("org.springframework.boot", "spring-boot-starter-test")
    implementation("com.google.firebase", "firebase-admin", "7.1.0")
    implementation("com.expediagroup", "graphql-kotlin-spring-server", "4.0.0-alpha.14")
    implementation("com.expediagroup","graphql-kotlin-ktor-client","4.0.0-alpha.14")
    implementation("com.expediagroup", "graphql-kotlin-federation", "4.0.0-alpha.14")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xopt-in=kotlin.RequiresOptIn")
        jvmTarget = "11"

    }
}

graphql {
    client {
        queryFileDirectory = "${project.projectDir}/src/main/resources/graphql/"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
repositories {
    mavenCentral()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}