package com.protopie.api.user.application.port.out

import com.protopie.api.user.domain.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface UserPort {
    fun existsByEmail(email: String): Boolean
    fun save(user: User): UUID
    fun findByEmail(email: String): User?
    fun getUserById(userId: UUID): User?
    fun updateUser(user: User)
    fun deleteUser(userId: UUID)
    fun getAllUsers(pageable: Pageable): Page<User>
}
