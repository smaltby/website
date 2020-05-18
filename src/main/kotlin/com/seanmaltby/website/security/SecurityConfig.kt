package com.seanmaltby.website.security

import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@EnableWebSecurity
class SecurityConfig(private val environmentPasswordProvider: EnvironmentPasswordProvider) : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        val adminPasswordFilter = PasswordAuthenticationFilter(
                AntPathRequestMatcher("/login", "POST"),
                ProviderManager(listOf(environmentPasswordProvider))
        )

        // Add error parameter to url on failed login attempt
        adminPasswordFilter.setAuthenticationFailureHandler(
                SimpleUrlAuthenticationFailureHandler("/login?error"))

        http
                .authorizeRequests()
                // admin pages require authentication
                .antMatchers("/admin/**").authenticated()
                // all other pages do not
                .anyRequest().permitAll()
                .and()
                // add a password only filter where the username password filter would be
                // intercepts post requests to /login, and evaluates the password parameter
                .addFilterAt(adminPasswordFilter, UsernamePasswordAuthenticationFilter::class.java)
                // redirect to login page if trying to access admin pages while unauthenticated
                .exceptionHandling()
                .authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/login"))
                .and()
                // Set X-Frame-Options to SAMEORIGIN so we can embed dynamic resources in iframes
                .headers().frameOptions().sameOrigin()
    }
}