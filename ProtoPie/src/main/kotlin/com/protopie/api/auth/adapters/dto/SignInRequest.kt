package com.protopie.api.auth.adapters.dto

data class SignInRequest(
    val email: String,
    val password: String,
)
