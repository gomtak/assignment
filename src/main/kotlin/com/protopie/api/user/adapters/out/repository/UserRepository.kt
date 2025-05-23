package com.protopie.api.user.adapters.out.repository

import com.protopie.api.common.RoleType
import com.protopie.api.user.adapters.out.entity.UserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): UserEntity?
    fun findAllByRoleNot(roleAdmin: RoleType, pageable: Pageable): Page<UserEntity>
}
