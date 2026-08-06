plugins {
    id("java")
    id("io.micronaut.application") version "5.0.2" apply false
    id("com.gradleup.shadow") version "9.4.1" apply false
    id("org.graalvm.buildtools.native") version "0.10.6" apply false
    id("io.micronaut.aot") version "5.0.2" apply false
}

group = "com.shree"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}