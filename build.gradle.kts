plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")

    `maven-publish`
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

publishing {
    repositories {
        maven("https://repo.slne.dev/repository/maven-releases/") {
            name = "maven-releases"
            credentials {
                username = System.getenv("SLNE_RELEASES_REPO_USERNAME")
                password = System.getenv("SLNE_RELEASES_REPO_PASSWORD")
            }
        }
    }

    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}