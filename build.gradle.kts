plugins {
    kotlin("jvm") version "2.3.0"
}

group = "core.tastycake"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven("https://www.cursemaven.com")
}

dependencies {
    compileOnly(files("libs/HytaleServer.jar"))

    implementation("curse.maven:hyui-1431415:7567866")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}