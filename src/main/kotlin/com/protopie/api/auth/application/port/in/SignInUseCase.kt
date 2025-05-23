package com.protopie.api.auth.application.port.`in`

import com.protopie.api.auth.adapters.dto.TokenResponse

interface SignInUseCase {
    fun signIn(email: String, password: String): TokenResponse
}
