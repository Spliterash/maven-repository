package ru.spliterash.mavenrepository.frontend

import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.io.ResourceLoader
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@RestController
class FrontendController(
    private val configuration: FrontendConfiguration,
    private val loader: ResourceLoader
) {
    private val pageCache: String by lazy {
        getPage()
    }
    private val jsCache = hashMapOf<String, String>()


    private fun getPage(): String {
        val resource = loader.getResource("classpath:reposilite-frontend/index.html")

        val page = resource.inputStream.readAllBytes()
            .decodeToString()
            .replacePlaceholders()

        return page
    }

    private fun String.replacePlaceholders() = replace("{{REPOSILITE.TITLE}}", configuration.title)
        .replace("{{REPOSILITE.ID}}", configuration.id)
        .replace("{{REPOSILITE.DESCRIPTION}}", configuration.description)
        .replace("{{REPOSILITE.ORGANIZATION_LOGO}}", configuration.logo)

        .replace(URLEncoder.encode("{{REPOSILITE.BASE_PATH}}", StandardCharsets.UTF_8), "/")
        .replace("{{REPOSILITE.BASE_PATH}}", "/")
        .replace(URLEncoder.encode("{{REPOSILITE.VITE_BASE_PATH}}", StandardCharsets.UTF_8), ".")
        .replace("{{REPOSILITE.VITE_BASE_PATH}}", ".")
        .replace(URLEncoder.encode("{{REPOSILITE.ORGANIZATION_WEBSITE}}", StandardCharsets.UTF_8), configuration.site)
        .replace("{{REPOSILITE.ORGANIZATION_WEBSITE}}", configuration.site)

    @GetMapping("/", produces = [MediaType.TEXT_HTML_VALUE])
    fun onMainPageRequest(): String {
        return getPage()
    }

    @GetMapping("/assets/*.js", produces = ["application/javascript;charset=utf-8"])
    fun onJsRequest(request: HttpServletRequest): String {
        val servletPath = request.servletPath

        return jsCache.computeIfAbsent(servletPath) {
            val path = "classpath:reposilite-frontend$servletPath"
            val resource = loader.getResource(path)
            if (!resource.isReadable) {
                return@computeIfAbsent "404, file $path"
            }

            resource
                .inputStream
                .readAllBytes()
                .decodeToString()
                .replacePlaceholders()
        }
    }

}