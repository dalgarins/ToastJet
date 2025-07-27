plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.5.0"
}

group = "com.ronnie"
version = "1.1-SNAPSHOT"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "251"
        }
        changeNotes = """
      Initial version
    """.trimIndent()
    }
}


sourceSets {
    main {
        java {
            srcDirs(arrayOf("src/main/java", "src/main/gen"))
        }
        kotlin {
            srcDirs(arrayOf("src/main/kotlin"))
        }
    }
}

dependencies {

    intellijPlatform {
        create("IC", "2025.1")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
        bundledPlugin("com.intellij.modules.json")
    }
    implementation("org.graalvm.sdk:graal-sdk:24.2.1")
    implementation("org.graalvm.polyglot:polyglot:24.2.1")
    implementation("org.graalvm.js:js:24.2.1")
    implementation("org.graalvm.js:js-scriptengine:24.2.1")
    implementation("org.graalvm.js:js-community:24.2.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
    implementation("io.socket:socket.io-client:2.0.1")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "21"
    }
}
