plugins {
    id("io.micronaut.application")// version "5.0.2"
    id("com.gradleup.shadow")// version "9.4.1"
    id("io.micronaut.aot")// version "5.0.2"
    id("jacoco")
    id("checkstyle")
    id("pmd")
    id("com.github.spotbugs") version "6.5.9"
    id("org.graalvm.buildtools.native")// version "0.10.6"
}

version = "0.1"
group = "com.shree.cloudnative"



repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    compileOnly("io.micronaut:micronaut-http-client")
    runtimeOnly("ch.qos.logback:logback-classic")
    testImplementation("io.micronaut:micronaut-http-client")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    add("spotbugsPlugins", "com.h3xstream.findsecbugs:findsecbugs-plugin:1.14.0")
}



application {
    mainClass = "com.shree.cloudnative.Application"
}

java {
    sourceCompatibility = JavaVersion.toVersion("25")
    targetCompatibility = JavaVersion.toVersion("25")
}


spotbugs {
    toolVersion.set("4.10.3")
    ignoreFailures.set(false)
    showProgress.set(true)
    effort.set(com.github.spotbugs.snom.Effort.MAX)
    excludeFilter.set(file("${projectDir}/config/spotbugs/spotbugs-exclude.xml"))
    //onlyAnalyze = listOf("com.shree.started.*")
}

tasks.spotbugsMain {
    reports {
        create("html") {
            required.set(true)
            outputLocation.set(file("${layout.buildDirectory.get()}/reports/spotbugs/main.html"))
        }
    }
}

tasks.withType<org.gradle.api.tasks.compile.JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:unchecked")
}

graalvmNative.toolchainDetection = false
graalvmNative {
    binaries {
        all {
            buildArgs.add("-H:+SharedArenaSupport")
        }
    }
}
/*graalvmNative {
    binaries {
        named("main") {
            buildArgs.add("-H:+SharedArenaSupport")
            buildArgs.add("--static")
            buildArgs.add("--libc=musl")
        }
    }
}*/




micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.shree.cloudnative.*")
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
    }

}

/*tasks.named<io.micronaut.gradle.docker.MicronautDockerfile>("dockerfile") {

    baseImage = "eclipse-temurin:25-jre"
}*/

tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    //baseImage.set("mcr.microsoft.com/azurelinux/base/core:3.0")
    baseImage.set("gcr.io/distroless/base-debian12")
}





// https://docs.gradle.org/current/userguide/upgrading_major_version_9.html#test_task_fails_when_no_tests_are_discovered
tasks.withType<AbstractTestTask>().configureEach {
    failOnNoDiscoveredTests = false
}




