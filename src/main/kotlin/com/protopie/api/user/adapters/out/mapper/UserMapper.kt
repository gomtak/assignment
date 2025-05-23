package com.protopie.api.user.adapters.out.mapper

import com.protopie.api.user.adapters.out.entity.UserEntity
import com.protopie.api.user.domain.model.User

object UserMapper {
    fun User.toEntity() = UserEntity.create(
        email = email,
        password = password,
        name = name,
        role = roleType,
    )
    fun UserEntity.toDomain() = User.of(
        id = id ?: error("User ID must not be null"),
        email = email,
        password = password,
        name = name,
        role = role,
    )
}
