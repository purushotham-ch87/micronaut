## Micronaut 5.0.4 Documentation

- [User Guide](https://docs.micronaut.io/5.0.4/guide/index.html)
- [API Reference](https://docs.micronaut.io/5.0.4/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/5.0.4/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)

---

- [Micronaut Gradle Plugin documentation](https://micronaut-projects.github.io/micronaut-gradle-plugin/latest/)
- [GraalVM Gradle Plugin documentation](https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html)
- [Shadow Gradle Plugin](https://gradleup.com/shadow/)
  
  ## Feature serialization-jackson documentation

- [Micronaut Serialization Jackson Core documentation](https://micronaut-projects.github.io/micronaut-serialization/latest/guide/)

## Feature micronaut-aot documentation

- [Micronaut AOT documentation](https://micronaut-projects.github.io/micronaut-aot/latest/guide/)



### Jacoco Test Report

Gradle does not inherently include the `jacocoTestReport` task unless you explicitly declare the plugin. [[1](https://docs.gradle.org/current/userguide/jacoco_plugin.html)]

##### Step 1: Add the JaCoCo Plugin

Open your  **build.gradle** file in the root of your `mm-getting-started` project and add the plugin ID: [[1](https://docs.gradle.org/current/userguide/jacoco_plugin.html), [2](https://codersee.com/jacoco-with-spring-boot-gradle-and-kotlin/)]

**For Groovy DSL (`build.gradle`):**

```groovy
plugins {    
    id "io.micronaut.application" version "X.X.X" // Your existing plugin    
    id "jacoco"                                   // <-- ADD THIS LINE
}
```

Use code with caution.

**For Kotlin DSL (`build.gradle.kts`):**

```kotlin
plugins {    
    id("io.micronaut.application") version "X.X.X" // Your existing plugin
    id("jacoco")                                   // <-- ADD THIS LINE
}
```

Use code with caution.



##### Step 2: Run the Correct Command

After saving the file, you need to combine the `--tests` filtering mechanism carefully. In Gradle, **any flag like `--tests` applies to the task immediately preceding it**. If you place tasks after a flag, Gradle might misinterpret them. [[1](https://stackoverflow.com/questions/46725952/generate-jacoco-code-coverage-report-for-individual-unit-tests-using-gradle)]

Run this sequence explicitly: [[1](https://stackoverflow.com/questions/46725952/generate-jacoco-code-coverage-report-for-individual-unit-tests-using-gradle), [2](https://codersee.com/jacoco-with-spring-boot-gradle-and-kotlin/)]

```bash
./gradlew test --tests "com.shree.started.*" jacocoTestReport
```

Use code with caution.



Alternative: Automate the Report Generation

Instead of typing out `jacocoTestReport` every time, you can instruct Gradle to **always run code coverage automatically** right after your tests finish. [[1](https://docs.gradle.org/current/userguide/jacoco_plugin.html)]

Add this configuration snippet to the bottom of your `build.gradle` file: [[1](https://docs.gradle.org/current/userguide/jacoco_plugin.html)]

**For Groovy DSL (`build.gradle`):**

```groovy
test {    
    finalizedBy jacocoTestReport 
}

jacocoTestReport {    
    dependsOn test 
}
```

Use code with caution.

**For Kotlin DSL (`build.gradle.kts`):**

```kotlin
tasks.test {    
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {    
    dependsOn(tasks.test)
}
```

Use code with caution.

Once this block is added, you only need to execute the test phase, and the coverage report will automatically generate: []

```bash
./gradlew test --tests "com.shree.started.*"
```

Use code with caution.



Let me know if you run into any **syntax errors** inside your build script or if your **report files** end up missing from the **`build/reports/jacoco/`** directory!



### Folder Structure `build/reports/`

```textile
build/
└── reports/
    ├── problems/          <-- Gradle build configuration warnings/errors
    ├── tests/             <-- Test execution results (Pass/Fail)
    └── jacoco/            <-- Code coverage analysis (Line/Branch metrics)

```



#### 📂 1. `problems/` (Gradle Internal Report)

- **Main File:** `problems-report.html`
- **What it is:** A new feature in modern Gradle versions. It does **not** contain code compilation errors or test failures.
- **What it tracks:** Deprecation warnings, invalid build script configurations, or plugin incompatibilities.
- **When to check it:** If your build feels slow, or if Gradle warns you that future updates will break your build scripts.

#### 📂 2. `tests/test/` (JUnit / Test Report)

- **Main File:** `index.html`
- **What it is:** The visual summary of your test suite execution.
- **Key Sections Inside:**
  - **Packages:** Groups results by your package structure (e.g., `com.shree.started`).
  - **Classes:** Shows individual test files (e.g., `HelloWorldControllerTest`).
  - **Failed Tests:** Lists the exact line of code that failed, along with the console output (`System.out`) and full Java stack trace.
- **When to check it:** Whenever a test fails (`FAILURE: Build failed...`) to see exactly why it failed.

#### 📂 3. `jacoco/test/html/` (JaCoCo Coverage Report)

- **Main File:** `index.html`
- **What it is:** The breakdown of how much of your production code was actually executed by your tests.
- **Color-Coded Metrics:**
  - 🟢 **Green:** Code lines that were completely executed during the test run.
  - 🟡 **Yellow:** Branches (like `if` statements) that were only partially tested (e.g., the `true` condition ran, but the `false` condition didn't).
  - 🔴 **Red:** Dead or untested code that your tests completely missed.
- **When to check it:** When you need to verify if your tests are thorough and meeting your team's code quality standards.



### JaCoCo Sessions HTML (`sessions.html`)

Standalone diagnostic page automatically generated alongside your main code coverage report. You can access it by clicking the **"Sessions"** link in the top-right corner of your `jacoco/test/html/index.html` report. [[1](https://docs.tibco.com/webfocus/922/doc/html/topic/com.ibi.help.admin/source/admin_console24.htm), [2](https://www.jacoco.org/jacoco/trunk/doc/classids.html)]

While the main report tells you *how much* code you covered, the Sessions page tells you **exactly which compiled class files were analyzed** to generate those numbers. [[1](https://www.jacoco.org/jacoco/trunk/doc/faq.html), [2](https://medium.com/make-android/measure-your-codes-reach-integrating-jacoco-code-coverage-in-android-apps-with-kotlin-dsl-382d577864e7)]

---

What Information Is on the Sessions HTML Page?

When you open this page, you will see a structured table containing three primary pieces of data:

1. **Session ID:** A unique string identifier assigned to that specific execution run (usually formatted as `YourMachineName-RandomID`).
2. **Execution Timestamps:** The exact start time and dump time showing when the JaCoCo agent recorded the code execution.
3. **The Class List Table:** A comprehensive list of every single compiled Java class file that JaCoCo tracked. [[1](https://agentclientprotocol.com/rfds/session-list), [2](https://www.neovasolutions.com/2024/04/09/mastering-test-coverage-with-jacoco-insights-and-best-practices/)]

---

Understanding the Class List Table Columns

The table lists your classes with three technical metrics that help you troubleshoot coverage issues:

| Column Name | What It Means                                                                                   | Why It Matters                                                                                   |
| ----------- | ----------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------ |
| **Id**      | A unique 64-bit hexadecimal hash number assigned to that exact compiled `.class` bytecode file. | It acts as a digital fingerprint for your compiled code.                                         |
| **Class**   | The fully qualified name of the Java class (e.g., `com.shree.started.HelloWorldController`).    | Helps you verify if your specific Micronaut controller or service was actually hit by the tests. |
| **Link**    | A clickable hyperlink that redirects you back to the source code visualization page.            | Allows you to quickly jump from the diagnostic list to the actual line-by-line coverage view.    |

---

Why is the Sessions HTML Page Useful?

Developers primarily use this page to troubleshoot a specific problem: **0% coverage or missing class files.**

If you wrote tests for `HelloWorldControllerTest` but the main report says `HelloWorldController` has **0% coverage** (or doesn't show up at all), you check the Sessions HTML page:

- **Scenario A (Class is missing from the list):** This means your test suite did not trigger or execute that code at all during the test run.
- **Scenario B (Class is present but has a warning):** If you see a warning about a **"Class ID mismatch,"** it means the source code was recompiled or modified *after* the test execution file (`jacoco.exec`) was created. JaCoCo refuses to show coverage data if the compiled class file doesn't perfectly match the fingerprint recorded during the test session. [[1](https://visola.github.io/posts/2014-03-22-building-with-ant/)]



#### Bundling the project to jar

#### Gradle

```bash
gradle clean assemble
```



#### Maven

```bash
mvn clean package
```



Docker Build

gradle clean dockerBuild



Run Error

docker: Error response from daemon: ports are not available: exposing port TCP 0.0.0.0:8080 -> 127.0.0.1:0: /forwards/expose returned unexpected status: 50



Port already used



gladble depndencyUpdates -Drevision=release




