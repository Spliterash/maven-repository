package ru.spliterash.mavenrepository.controllers

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import ru.spliterash.mavenrepository.PUBLIC
import ru.spliterash.mavenrepository.filter.AuthorizationService

class AuthFilter(
    private val authHelper: AuthorizationService
) : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        request as HttpServletRequest
        response as HttpServletResponse

        // Если не PUT, то пофигу
        if (request.method == "PUT") {
            if (!isAuth(request)) {
                close(response)
                return
            }
        } else if (request.method == "GET") {
            val path = request.servletPath
            val repoName = path.substring(1).substringBefore("/")
            if (repoName != PUBLIC && !isAuth(request)) {
                close(response)
                return
            }
        }
        chain.doFilter(request, response)
    }

    private fun close(response: HttpServletResponse) {
        response.status = 401
        response.setHeader("WWW-Authenticate", """Basic realm="SpliterashMavenRepo", charset="UTF-8"""")
    }

    private fun isAuth(request: HttpServletRequest): Boolean {
        val header = request.getHeader("Authorization") ?: return false

        return authHelper.isAuth(header)
    }
}