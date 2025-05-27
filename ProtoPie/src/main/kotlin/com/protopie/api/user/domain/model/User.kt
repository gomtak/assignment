package com.protopie.api.user.domain.model

import com.protopie.api.common.RoleType
import java.util.UUID

class User private constructor(
    val _id: UUID? = null,
    email: String,
    password: String,
    name: String,
    val roleType: RoleType
) {
    val id: UUID
        get() = _id ?: throw IllegalStateException("ID is not initialized")
    var email: String = email
        private set
    var password: String = password
        private set
    var name: String = name
        private set

    fun update(email: String, name: String) {
        this.email = email
        this.name = name
    }

    fun updateEmail(email: String) {
        this.email = email
    }
    fun updateName(name: String) {
        this.name = name
    }

    companion object {
        fun create(
            email: String,
            password: String,
            name: String,
            role: RoleType
        ): User {
            return User(
                email = email,
                password = password,
                name = name,
                roleType = role
            )
        }

        fun of(
            id: UUID,
            email: String,
            password: String,
            name: String,
            role: RoleType
        ): User {
            return User(
                _id = id,
                email = email,
                password = password,
                name = name,
                roleType = role
            )
        }
    }

}


