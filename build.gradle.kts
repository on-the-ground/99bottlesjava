plugins {
    application
}

group = "com.bottles"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("--enable-preview", "-Xlint:preview"))
}

tasks.withType<Test>().configureEach { jvmArgs("--enable-preview") }

tasks.withType<JavaExec>().configureEach { jvmArgs("--enable-preview") }

application {
    mainClass = "Bottles"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.joohyung-park:effectivejava:0.4.1")
}
