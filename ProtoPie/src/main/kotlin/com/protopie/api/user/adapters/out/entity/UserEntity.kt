package com.protopie.api.user.adapters.out.entity

import com.protopie.api.common.RoleType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "users")
class UserEntity(
    email: String,
    password: String,
    name: String,
    role: RoleType,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null

    @Column(unique = true)
    var email: String = email
        protected set
    var password: String = password
        protected set
    var name: String = name
        protected set
    var role: RoleType = role
        protected set

    fun update(
        password: String,
        email: String,
        name: String,
        role: RoleType
    ) {
        this.password = password
        this.email = email
        this.name = name
        this.role = role
    }

    companion object {
        fun create(
            email: String,
            password: String,
            name: String,
            role: RoleType,
        ): UserEntity {
            return UserEntity(
                email = email,
                password = password,
                name = name,
                role = role
            )
        }
    }
}
