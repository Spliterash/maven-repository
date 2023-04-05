package ru.spliterash.mavenrepository.auth

import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class AuthArgumentResolver(
    private val authorizationService: AuthorizationService,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        if (parameter.getParameterAnnotation(Auth::class.java) == null)
            return false

        return parameter.parameterType == Boolean::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val header = webRequest.getHeader("Authorization") ?: return false

        return authorizationService.isAuth(header)
    }
}