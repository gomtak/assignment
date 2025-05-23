package com.protopie.api.auth.adapters.dto

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
