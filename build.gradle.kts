/*====================================================================*\

Gradle build script : FAT32Manager

The 'compileJava' task expects the following environment variable to be
defined:
  JAVAFX_HOME
    the location of the JavaFX SDK

The 'runMainJre' and 'runJarJre' tasks expect the following environment
variable to be defined:
  JRE_DIR
    the location of a Java Runtime Environment that contains the Java SE
    modules, JDK modules and JavaFX modules that are required by the
    application at runtime

\*====================================================================*/

// Plug-ins

plugins {
    java
}

//----------------------------------------------------------------------

// Functions

fun _path(vararg components : String): String =
        components.map { it.replace('/', File.separatorChar) }.joinToString(separator = File.separator)

fun _appSystemProperties() =
        System.getProperties()
                .filter { (key, _) -> (key is String) && key.startsWith("blankaspect.") }
                .mapKeys { it.key as String }

//----------------------------------------------------------------------

// Properties

val javaVersion = 17

val projectName = project.name

val packageName     = "fat32manager"
val mainClassName   = "uk.blankaspect.${packageName}.Fat32ManagerApp"

val buildDir    = layout.buildDirectory.get().getAsFile().toString()
val jarDir      = _path(buildDir, "bin")
val jarFilename = "fat32Manager.jar"

val jfxLibDir   = _path(System.getenv("JAVAFX_HOME"), "lib")
val jfxLibs     = listOf(
    "javafx.base",
    "javafx.controls",
    "javafx.graphics",
    "javafx.swing"
)

// Location of the Java launcher in a JRE that includes JavaFX modules.  Note the unconventional test for Windows.
val jfxLauncher = _path(System.getenv("JRE_DIR"), "bin", if (File.separatorChar.equals('\\')) "java.exe" else "java")

// Java launcher: add JavaFX modules to module path
val jfxArgs = listOf(
    "--module-path", jfxLibDir,
    "--add-modules", jfxLibs.joinToString(",")
)

//----------------------------------------------------------------------

// Dependencies

dependencies {
    jfxLibs.forEach { compileOnly(files(_path(jfxLibDir, it + ".jar"))) }
}

//----------------------------------------------------------------------

// Java version

tasks.compileJava {
    options.release.set(javaVersion)
}

//----------------------------------------------------------------------

// Create executable JAR

tasks.jar {
    destinationDirectory.set(file(jarDir))
    archiveFileName.set(jarFilename)
    setPreserveFileTimestamps(true)
    manifest {
        attributes(
            "Application-Name" to projectName,
            "Main-Class"       to mainClassName
        )
    }
}

//----------------------------------------------------------------------

// Run main class with Gradle's Java launcher

tasks.register<JavaExec>("runMain") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set(mainClassName)
    jvmArgs = jfxArgs

    systemProperties(_appSystemProperties())
}

//----------------------------------------------------------------------

// Run main class with Java launcher from JavaFX JRE

tasks.register<JavaExec>("runMainJre") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set(mainClassName)
    executable(jfxLauncher)

    systemProperties(_appSystemProperties())
}

//----------------------------------------------------------------------

// Run executable JAR with Gradle's Java launcher

tasks.register<JavaExec>("runJar") {
    classpath = files(tasks.jar)
    jvmArgs = jfxArgs

    systemProperties(_appSystemProperties())
}

//----------------------------------------------------------------------

// Run executable JAR with Java launcher from JavaFX JRE

tasks.register<JavaExec>("runJarJre") {
    classpath = files(tasks.jar)
    executable(jfxLauncher)

    systemProperties(_appSystemProperties())
}

//----------------------------------------------------------------------
