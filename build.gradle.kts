plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "dev.slne.surf"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.0")
    implementation("io.netty:netty-all:4.1.119.Final")

    testImplementation(kotlin("test"))
}

tasks {
    test {
        useJUnitPlatform()
    }
}


kotlin {
    jvmToolchain(21)
}