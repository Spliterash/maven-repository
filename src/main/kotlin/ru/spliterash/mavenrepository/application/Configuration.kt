package ru.spliterash.mavenrepository.application

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.spliterash.mavenrepository.filter.AuthorizationService
import ru.spliterash.mavenrepository.controllers.AuthFilter

@Configuration
class Configuration {
    @Bean
    fun publishFilter(helper: AuthorizationService): FilterRegistrationBean<AuthFilter> {
        return FilterRegistrationBean(AuthFilter(helper))
    }
}