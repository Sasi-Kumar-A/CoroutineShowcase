plugins {
    kotlin("jvm") version "2.0.21"
    application
}

group = "com.example.coroutines"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // Core coroutines engine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    // Provides Dispatchers.Main for standard JVM/Desktop console environments
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")
}

application {
    mainClass.set("com.example.coroutines.MainKt")
}