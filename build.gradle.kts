plugins {
    kotlin("jvm") version "2.1.0"
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

subprojects {
    group = "pt.davidafsilva.aoc"
    version = "1.0-SNAPSHOT"

    apply(plugin = "org.jetbrains.kotlin.jvm")

    kotlin {
        jvmToolchain(21)
    }

    dependencies {
        implementation("net.objecthunter:exp4j:0.4.8")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.7.3")
    }
}
