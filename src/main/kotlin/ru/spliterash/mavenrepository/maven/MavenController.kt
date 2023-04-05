package ru.spliterash.mavenrepository.maven

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
import org.springframework.core.io.ClassPathResource
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.NoHandlerFoundException
import ru.spliterash.mavenrepository.auth.Auth
import ru.spliterash.mavenrepository.auth.NotAuthException
import ru.spliterash.mavenrepository.repository.MavenRepositoryService
import ru.spliterash.mavenrepository.repository.result.FileResult
import ru.spliterash.mavenrepository.repository.result.FolderResult
import java.io.InputStream

@RestControllerAdvice
class MavenController(
    private val mavenService: MavenRepositoryService
) {
    private var notFoundPage: ByteArray? = null


    @ExceptionHandler(NoHandlerFoundException::class)
    fun read(request: HttpServletRequest, @Auth auth: Boolean, response: HttpServletResponse) {
        val path = request.servletPath
        try {
            if (request.method == "GET")
                readFile(response, path, auth)
            else
                putFile(auth, request, request.inputStream)
        } catch (ex: NotAuthException) {
            notAuth(response)
        }
    }

    fun readFile(response: HttpServletResponse, path: String, auth: Boolean) {
        val result = mavenService.readFile(path, auth) ?: run {
            notFound(response)
            return
        }
        when (result) {
            is FolderResult -> {
                notFound(response)
                return
            }

            is FileResult -> {
                IOUtils.copy(result.openStream(), response.outputStream)
            }
        }
    }

    fun putFile(
        @Auth auth: Boolean,
        request: HttpServletRequest,
        content: InputStream,
    ) {
        if (!auth)
            throw NotAuthException()
        val path = request.servletPath
        mavenService.saveFile(path, content)
    }

    fun notAuth(response: HttpServletResponse) {
        notFound(response)
    }

    private fun notFoundPage(): ByteArray {
        notFoundPage?.let { return it }

        val resource = ClassPathResource("not_found.html")

        resource.inputStream.readAllBytes().let { bytes ->
            notFoundPage = bytes

            return bytes
        }
    }

    private fun notFound(response: HttpServletResponse) {
        val notFoundPage = notFoundPage()
        response.status = 404
        response.setContentLength(notFoundPage.size)

        IOUtils.write(notFoundPage, response.outputStream)
    }
}