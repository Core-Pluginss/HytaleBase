plugins {
    kotlin("jvm") version "2.3.0"
    java
    id("com.gradleup.shadow") version "8.3.5"
    id("java-library")
    id("maven-publish")
}

group = "core.tastycake"
version = "lts"

repositories {
    mavenCentral()

    maven("https://www.cursemaven.com")

    flatDir { dirs("libs") }
}

dependencies {
    implementation(files("libs/HytaleServer.jar"))

    implementation("curse.maven:hyui-1431415:7567866")

    testImplementation(kotlin("test"))
}

tasks.jar {
    enabled = false
}

tasks.build {
    dependsOn(tasks.shadowJar)
    dependsOn(tasks.publishToMavenLocal)
}

tasks.shadowJar {
    archiveBaseName.set("hytale-base")
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "core.tastycake"
            artifactId = "hytale-base"
            version = project.version.toString()

            artifact(tasks.shadowJar)
        }
    }
}

kotlin {
    jvmToolchain(23)
}

tasks.test {
    useJUnitPlatform()
}