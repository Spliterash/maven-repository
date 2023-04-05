import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.5"
    id("org.graalvm.buildtools.native") version "0.9.18"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.8.20"
}
allprojects {
    apply(plugin = "java-library")

    repositories {
        mavenCentral()
    }

    dependencies{
        implementation(platform("org.springframework.boot:spring-boot-dependencies:3.0.5"))
    }
}

group = "ru.spliterash"

version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_17



dependencies {
    implementation(project(":frontend"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("commons-io:commons-io:2.11.0")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}
tasks.compileJava {
    inputs.files(tasks.processResources)
}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}