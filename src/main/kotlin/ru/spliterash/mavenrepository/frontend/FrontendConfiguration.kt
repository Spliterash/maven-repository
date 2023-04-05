package ru.spliterash.mavenrepository.frontend

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "frontend")
class FrontendConfiguration(
    val id: String = "spliterash-repo",
    val title: String = "Spliterash maven repository",
    val description: String = "My maven landfill",
    val logo: String = "https://avatars.githubusercontent.com/u/22550546?v=4",
    val site:String="https://spliterash.ru",
    val footer: String = "Frontend taken from Reposilite"
) {
}