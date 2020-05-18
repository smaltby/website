package com.seanmaltby.website.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class PasswordAuthenticationToken : AbstractAuthenticationToken {
    private val credentials: Any

    constructor(credentials: Any) : super(null) {
        this.credentials = credentials
    }

    constructor(credentials: Any, authorities: Collection<GrantedAuthority>?) : super(authorities) {
        this.credentials = credentials
        isAuthenticated = true
    }

    override fun getCredentials(): Any {
        return credentials
    }

    override fun getPrincipal(): Any {
        return "admin"
    }
}