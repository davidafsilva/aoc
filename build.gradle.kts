import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
}

group = "pt.davidafsilva.aoc"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("net.objecthunter:exp4j:0.4.8")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}
