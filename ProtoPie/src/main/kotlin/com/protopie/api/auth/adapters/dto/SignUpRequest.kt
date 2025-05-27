package com.protopie.api.auth.adapters.dto

data class SignUpRequest(
    val email: String,
    val password: String,
    val name: String
)
