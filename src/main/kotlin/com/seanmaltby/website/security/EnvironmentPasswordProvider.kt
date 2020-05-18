package com.seanmaltby.website.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.stereotype.Component

@Component
class EnvironmentPasswordProvider : AuthenticationProvider {
    @Value("\${admin.password}")
    private lateinit var adminPassword: String

    override fun supports(authentication: Class<*>): Boolean {
        return (PasswordAuthenticationToken::class.java.isAssignableFrom(authentication))
    }

    override fun authenticate(authentication: Authentication): Authentication {
        val password = authentication.credentials.toString()

        if (password != adminPassword) {
            throw BadCredentialsException("Incorrect password")
        }

        return PasswordAuthenticationToken(password, AuthorityUtils.NO_AUTHORITIES)
    }
}