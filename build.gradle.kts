plugins {
    id("java")
}

group = "com.foppykitty.crubber"
version = "0.0.1-ALPHA"
val manifoldVersion = "2026.1.6"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencies {
    implementation("systems.manifold:manifold-params-rt:$manifoldVersion")
    testImplementation("junit:junit:4.12")
    annotationProcessor("systems.manifold:manifold-params:$manifoldVersion")
    testAnnotationProcessor("systems.manifold:manifold-params:$manifoldVersion")

    annotationProcessor("systems.manifold:manifold-strings:$manifoldVersion")
    testAnnotationProcessor("systems.manifold:manifold-strings:$manifoldVersion")

    implementation("systems.manifold:manifold-ext-rt:$manifoldVersion")
    testCompileOnly("junit:junit:4.12")
    annotationProcessor("systems.manifold:manifold-ext:$manifoldVersion")
    testAnnotationProcessor("systems.manifold:manifold-ext:$manifoldVersion")
}

if (JavaVersion.current() != JavaVersion.VERSION_1_8 &&
    sourceSets.main.get().allJava.files.any { it.name == "module-info.java"}
) {
    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(listOf("-Xplugin:Manifold", "--module-path", classpath.asPath))

    }
} else {
    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.add("-Xplugin:Manifold")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("--enable-preview")
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs("--enable-preview")
}

//tasks.withType<JavaCompile>().configureEach {
//    options.isFork = true
//    options.forkOptions.jvmArgs?.addAll(listOf(
//        "-Xexportinterface", // Helps Manifold with some internal visibility
//        "--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
//        "--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
//        "--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
//        "--add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
//        "--add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
//        "--add-opens=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED",
//        "--add-opens=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
//        "--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
//        "--add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
//        "--add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
//        "--add-opens=java.base/jdk.internal.loader=ALL-UNNAMED",
//        "--add-exports=java.base/jdk.internal.access=ALL-UNNAMED"
//    ))
//}


tasks.test {
    useJUnitPlatform()
}