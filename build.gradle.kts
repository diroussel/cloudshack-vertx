import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val vertxVersion = "3.6.3"
val junitVersion = "5.3.2"

plugins {
  java
  application
  kotlin("jvm") version "1.3.20"
  id("com.github.johnrengelman.shadow") version "4.0.3"
  `build-scan`
}

repositories {
  mavenCentral()
}


dependencies {
  compile(kotlin("stdlib"))
  implementation("io.vertx:vertx-core:$vertxVersion")

  testImplementation("io.vertx:vertx-junit5:$vertxVersion")
  testImplementation("io.vertx:vertx-web-client:$vertxVersion")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
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
}

buildScan {
  termsOfServiceUrl = "https://gradle.com/terms-of-service"
  termsOfServiceAgree = "yes"
}
