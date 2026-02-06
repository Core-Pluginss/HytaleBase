# HytaleBase
Usage:
- Step 1:
   Clone this project and build it - `gradle clean build`
- Step 2:
    In your plugin's `build.gradle.kts` modify the repositories and dependencies:
```gradle
repositories {
    mavenCentral()
    
    mavenLocal() // <-- Add this
    
    // ... All your other repositories
}

dependencies {
    // No need for HytaleServer.jar, its included in HytaleBase.

    implementation("core.tastycake:hytale-base:lts") // <-- Add this
    
    // ... All your other dependencies
}
```
- Step 3: In your plugin's `setup()` method add the base initialization:
```java
// Java
@Override
protected void setup() {
    HytaleBase.Companion.init(this); // <-- Add this

    // ... The rest of your code
}
```
```kotlin
// Kotlin
override fun setup() {
    HytaleBase.init(this); // <-- Add this

    // ... The rest of your code
}
```
