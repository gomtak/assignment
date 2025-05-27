package com.protopie.api.user.adapters.dto

import java.time.LocalDateTime
import java.util.UUID

data class UserDeletedEvent(
    val userId: UUID,
    val email: String,
    val deletedAt: LocalDateTime = LocalDateTime.now()
)

