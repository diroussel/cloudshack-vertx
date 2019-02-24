import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val vertxVersion = "3.6.3"
val junitVersion = "5.3.2"


val kotlinLoggingVer = "1.4.6"
val logbackVer = "1.2.3"
val jAnsiVer = "1.16"


plugins {
  java
  application
  kotlin("jvm") version "1.3.20"
  id("com.github.johnrengelman.shadow") version "4.0.4"
  `build-scan`
}

repositories {
  mavenCentral()
}


dependencies {
  compile(kotlin("stdlib"))
  implementation("io.vertx:vertx-core:$vertxVersion")


  compile("io.github.microutils:kotlin-logging:$kotlinLoggingVer")

  compile("ch.qos.logback:logback-classic:$logbackVer")
  compile("org.fusesource.jansi:jansi:$jAnsiVer")

  testImplementation("io.vertx:vertx-junit5:$vertxVersion")
  testImplementation("io.vertx:vertx-web-client:$vertxVersion")
  //testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
  testImplementation("io.kotlintest:kotlintest-runner-junit5:3.2.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
}

application {
  mainClassName = "io.vertx.core.Launcher"
}

val mainVerticleName = "io.vertx.starter.MainVerticle"
val watchForChange = "src/**/*.java"
val doOnChange = "${projectDir}/gradlew classes"


tasks {
  test {
    useJUnitPlatform()
  }

  withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
  }

  withType<Test> {
    useJUnitPlatform()
  }

  getByName<JavaExec>("run") {
    args = listOf("run", mainVerticleName, "--redeploy=${watchForChange}", "--launcher-class=${application.mainClassName}", "--on-redeploy=${doOnChange}")
  }

  withType<ShadowJar> {
    classifier = "fat"
    manifest {
      attributes["Main-Verticle"] = mainVerticleName
    }
    mergeServiceFiles {
      include("META-INF/services/io.vertx.core.spi.VerticleFactory")
    }
  }

  // for reproducible builds
  withType<AbstractArchiveTask> {
    setPreserveFileTimestamps(false)
    setReproducibleFileOrder(true)
  }
}


// stage is needed by herouk, and needs the right dependencies
val build: DefaultTask by tasks
val shadowJar = tasks["shadowJar"] as ShadowJar
build.dependsOn(shadowJar)
val clean: Delete by tasks
task("stage") {
  dependsOn(build, clean)
}

buildScan {
  termsOfServiceUrl = "https://gradle.com/terms-of-service"
  termsOfServiceAgree = "yes"
}
