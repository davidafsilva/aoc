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

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}
