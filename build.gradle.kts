plugins {
    kotlin("jvm") version "1.4.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    val exposedVersion = "0.24.1"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    runtimeOnly("org.postgresql:postgresql:42.2.5")

    runtimeOnly("org.slf4j:slf4j-simple:1.6.1")

    val junitVersion = "5.7.0"
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")

    testImplementation("io.mockk:mockk:1.10.0")

    testRuntimeOnly("com.h2database:h2:1.4.200")
}

tasks {
    test {
        useJUnitPlatform()
    }
}