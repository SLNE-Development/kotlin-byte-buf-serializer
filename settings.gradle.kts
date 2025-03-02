pluginManagement {
    plugins {
        kotlin("plugin.serialization") version "2.1.10"
        kotlin("jvm") version "2.1.10"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "kotlin-byte-buf-serializer"

