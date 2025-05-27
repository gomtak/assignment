package com.protopie.api.user.application.service

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AdminOnly(
    val currentUser: String,
)
