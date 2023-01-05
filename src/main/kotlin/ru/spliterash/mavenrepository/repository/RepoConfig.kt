package ru.spliterash.mavenrepository.repository

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("repositories")
class RepoConfig(
    val directory: String,
    val public: List<String>
)