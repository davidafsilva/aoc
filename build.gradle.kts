plugins {
    kotlin("jvm") version "1.9.21"
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
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.1")
    }
}
