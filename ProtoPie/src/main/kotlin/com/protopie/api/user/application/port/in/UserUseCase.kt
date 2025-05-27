package com.protopie.api.user.application.port.`in`

import com.protopie.api.auth.security.CurrentUser
import com.protopie.api.user.adapters.dto.UpdateUserRequest
import com.protopie.api.user.adapters.dto.UserResponse
import com.protopie.api.user.domain.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface UserUseCase {
    fun fetchUser(userId: UUID, currentUser: CurrentUser): UserResponse
    fun updateUser(userId: UUID, request: UpdateUserRequest, currentUser: CurrentUser)
    fun deleteUser(userId: UUID, currentUser: CurrentUser)
    fun pageUsers(currentUser: CurrentUser, pageable: Pageable): Page<UserResponse>
}
