package ru.spliterash.mavenrepository.application

import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import ru.spliterash.mavenrepository.auth.AuthArgumentResolver

@Configuration
class Configuration(
    private val authResolver: AuthArgumentResolver
) : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry
            .addResourceHandler("/assets/**")
            .addResourceLocations("classpath:/reposilite-frontend/assets/")
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(authResolver)
    }
}