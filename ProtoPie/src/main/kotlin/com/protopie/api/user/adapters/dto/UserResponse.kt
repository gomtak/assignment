package com.protopie.api.user.adapters.dto

import com.protopie.api.common.RoleType
import com.protopie.api.user.domain.model.User
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val role: RoleType
) {
    companion object {
        fun from(user: User): UserResponse = UserResponse(
            id = user.id,
            name = user.name,
            email = user.email,
            role = user.roleType
        )
    }
}
