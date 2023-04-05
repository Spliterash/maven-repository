package ru.spliterash.mavenrepository.api

import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.io.FilenameUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import ru.spliterash.mavenrepository.api.response.ErrorResponse
import ru.spliterash.mavenrepository.api.response.InfoResponse
import ru.spliterash.mavenrepository.auth.Auth
import ru.spliterash.mavenrepository.auth.NotAuthException
import ru.spliterash.mavenrepository.repository.MavenRepositoryService
import ru.spliterash.mavenrepository.repository.result.FileInfo
import ru.spliterash.mavenrepository.repository.result.FolderResult

@RestController
class ApiReadController(
    private val service: MavenRepositoryService
) {
    @GetMapping("/api/maven/details/{*path}")
    fun info(
        @Auth authorized: Boolean,
        @PathVariable path: String
    ): ResponseEntity<*> = when (val queryResult = service.readFile(path, authorized)) {
        null -> noAuth()
        is FolderResult -> ResponseEntity.ok(
            InfoResponse(
                FilenameUtils.getName(path),
                FileInfo.Type.DIRECTORY,
                queryResult.files.map(::mapFile)
            )
        )

        else -> ResponseEntity.ok(
            InfoResponse(
                FilenameUtils.getName(path),
                FileInfo.Type.FILE,
                listOf()
            )
        )
    }

    fun noAuth(): ResponseEntity<*> {
        return ResponseEntity.status(404).body(ErrorResponse(404, "Resource not found"))
    }

    @ExceptionHandler(NotAuthException::class)
    fun handleNoAuth(response: HttpServletResponse) = noAuth()

    private fun mapFile(file: FileInfo): InfoResponse.Item {
        val ext = file.name.substringBeforeLast(".")
        val contentType = when (ext) {
            "jar" -> "application/java-archive"
            "pom" -> "application/xml"
            else -> "application/octet-stream"
        }
        return if (file.type == FileInfo.Type.FILE)
            InfoResponse.Item(
                file.name,
                file.type,
                contentType,
                file.size
            )
        else
            InfoResponse.Item(
                file.name,
                file.type
            )

    }
}