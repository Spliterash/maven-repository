package ru.spliterash.mavenrepository.controllers

import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import ru.spliterash.mavenrepository.repository.MavenRepositoryService

@RestController
class ReadController(
    private val repositoryService: MavenRepositoryService
) {

    @GetMapping("/{*path}")
    fun read(@PathVariable path: String, response: HttpServletResponse) {
        val stream = repositoryService.readFile(path) ?: run {
            response.status = 404
            return
        }

        IOUtils.copy(stream, response.outputStream)
    }
}