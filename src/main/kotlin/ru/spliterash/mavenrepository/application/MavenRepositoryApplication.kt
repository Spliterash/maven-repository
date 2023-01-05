package ru.spliterash.mavenrepository.application

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["ru.spliterash.mavenrepository"])
@ConfigurationPropertiesScan("ru.spliterash.mavenrepository")
@EnableConfigurationProperties
class MavenRepositoryApplication

fun main(args: Array<String>) {
    runApplication<MavenRepositoryApplication>(*args)
}
