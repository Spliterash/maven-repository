package ru.spliterash.mavenrepository.auth

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.Charset
import java.util.*

@Component
class AuthorizationService(
    @Value("#{'\${auth}'.split(',')}") private val loginPairs: List<String>
) {
    private val basicAuthLines = loginPairs.map {
        Base64
            .getEncoder()
            .encode(it.toByteArray())
            .toString(Charset.forName("UTF-8"))
    }

    fun isAuth(basicAuthLine: String): Boolean {
        val substr = basicAuthLine.substringAfter(" ")
        return basicAuthLines.contains(substr)
    }
}