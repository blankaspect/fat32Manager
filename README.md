### blankaspect/fat32Manager

This repository contains the source code and resources of the FAT32Manager application.  The repo conforms to the Maven
standard directory layout \(ie, the source code is in `src/main/java` and the resources are in `src/main/resources`\).

The Java version of the source code is 17.

The source files in this repo have an expected tab width of 4.

----

The application requires a platform-specific shared library that is accessed through the Java Native Interface \(JNI\).
The application expects to find the library in the following directory:\
`src/main/resources/uk/blankaspect/fat32manager/lib`

Binaries of shared libraries are provided for 64-bit Linux and Windows:
* The Linux library, `libdriveio.so`, was built on Ubuntu 20.04.1 with GCC 11.  It has been tested only on Ubuntu
20.04.1.
* The Windows library, `driveio.dll`, was built on Windows 10 with the C++ compiler from Visual C++ 2015 \(v14.0\)
native build tools.  It has been tested only on Windows 10.

The `driveIO` directory of this repo contains the source code of the library for Linux and Windows.

----

The Kotlin-DSL-based Gradle build script extends the `compileJava` and `jar` tasks of the `java` plug-in and defines
the following tasks for launching the application:
* `runMain` \(for use after `compileJava`\)\
Runs the main class with Gradle's Java launcher. 
* `runMainJre` \(for use after `compileJava`\)\
Runs the main class with a Java launcher from a JRE that includes JavaFX modules.
* `runJar` \(for use after `jar`\)\
Runs the JAR with Gradle's Java launcher.
* `runJarJre` \(for use after `jar`\)\
Runs the JAR with a Java launcher from a JRE that includes JavaFX modules.

FAT32Manager is a non-modular JavaFX application.  The `compileJava`, `jar`, `runMain` and `runJar` tasks expect the
JavaFX modules to be provided by a [JavaFX SDK](https://gluonhq.com/products/javafx/); the two `run*Jre` tasks expect
the JavaFX modules to be provided by a JRE.

JavaFX modules are included in some third-party JREs, such as the
[Azul Zulu JRE FX packages](https://www.azul.com/downloads/).  If you prefer to create your own JRE that includes JavaFX
modules, the [blankaspect/makejre](https://github.com/blankaspect/makejre) repo contains scripts for Linux and Windows
that may be useful. 

----

The contents of this repository are covered by three licences:

* You may use the contents of the `uk.blankaspect.fat32manager` package \(including resources\) under the terms of the
GPL version 3 license.
* You may use the Roboto Mono font files under the terms of the Apache version 2.0 license.
* You may use all other contents of this repository under the terms of the MIT license.
