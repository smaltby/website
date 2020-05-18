package com.seanmaltby.website.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.RequestMatcher
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class PasswordAuthenticationFilter(requiresAuthenticationRequestMatcher: RequestMatcher, authenticationManager: AuthenticationManager) : AbstractAuthenticationProcessingFilter(requiresAuthenticationRequestMatcher) {
    init {
        setAuthenticationManager(authenticationManager)
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        if (request.method != "POST") {
            throw AuthenticationServiceException("Authentication method not supported: " + request.method)
        }

        var password = request.getParameter("password")

        if (password == null) {
            password = ""
        }

        val authRequest = PasswordAuthenticationToken(password)

        authRequest.details = authenticationDetailsSource.buildDetails(request)

        return authenticationManager.authenticate(authRequest)
    }
}