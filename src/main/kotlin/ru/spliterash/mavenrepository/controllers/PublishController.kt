package ru.spliterash.mavenrepository.controllers

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController
import ru.spliterash.mavenrepository.repository.MavenRepositoryService
import java.io.InputStream

@RestController
class PublishController(
    private val mavenRepositoryService: MavenRepositoryService
) {
    @PutMapping("/{*path}")
    fun putFile(
        @PathVariable("path") path: String,
        file: InputStream,
    ) {
        mavenRepositoryService.saveFile(path, file)
    }
}