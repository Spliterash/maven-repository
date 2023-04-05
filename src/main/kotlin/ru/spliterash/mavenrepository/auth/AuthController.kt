package ru.spliterash.mavenrepository.auth

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val authorizationService: AuthorizationService
) {
    @GetMapping("/api/auth/me")
    fun authStatus(
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<Any> {
        if (!authorizationService.isAuth(authHeader))
            return ResponseEntity.status(401).body("Not authorized")
        else
            // Who cares
            return ResponseEntity.ok(
                """
                {
                  "accessToken": {
                    "identifier": {
                      "type": "PERSISTENT",
                      "value": 1
                    },
                    "name": "admin",
                    "createdAt": [
                      2023,
                      4,
                      5
                    ],
                    "description": ""
                  },
                  "permissions": [
                    {
                      "identifier": "access-token:manager",
                      "shortcut": "m"
                    }
                  ],
                  "routes": []
                }
            """
            )
    }
}