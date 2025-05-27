package com.protopie.api.user.application.service

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CheckUserAccess(
    val targetUserId: String,
    val currentUser: String
)
