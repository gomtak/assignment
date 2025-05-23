package com.protopie.api.auth.application.service

import com.protopie.api.auth.adapters.dto.TokenResponse
import com.protopie.api.auth.application.port.`in`.SignInUseCase
import com.protopie.api.auth.security.CustomUserDetails
import com.protopie.api.auth.jwt.JwtTokenProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.stereotype.Service

@Service
class SignInService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
) : SignInUseCase {
    override fun signIn(email: String, password: String): TokenResponse {
        val authenticationToken = UsernamePasswordAuthenticationToken(email, password)
        val authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken)
        val userDetails = authentication.principal as CustomUserDetails
        val tokenResponse = jwtTokenProvider.createToken(userDetails)

        return tokenResponse
    }
}
