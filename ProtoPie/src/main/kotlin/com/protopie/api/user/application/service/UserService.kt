package com.protopie.api.user.application.service

import com.protopie.api.auth.security.CurrentUser
import com.protopie.api.user.adapters.dto.UpdateUserRequest
import com.protopie.api.user.adapters.dto.UserDeletedEvent
import com.protopie.api.user.adapters.dto.UserResponse
import com.protopie.api.user.application.port.`in`.UserUseCase
import com.protopie.api.user.application.port.out.UserPort
import com.protopie.api.user.domain.service.UserDomainService
import jakarta.persistence.EntityNotFoundException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserService(
    private val userPort: UserPort,
    private val userDomainService: UserDomainService,
    private val applicationEventPublisher: ApplicationEventPublisher
) : UserUseCase {

    @CheckUserAccess(targetUserId = "userId", currentUser = "currentUser")
    override fun fetchUser(userId: UUID, currentUser: CurrentUser): UserResponse {
        val user = userPort.getUserById(userId) ?: throw EntityNotFoundException("User with id $userId not found")
        return UserResponse.from(user)
    }

    @Transactional
    @CheckUserAccess(targetUserId = "userId", currentUser = "currentUser")
    override fun updateUser(
        userId: UUID,
        request: UpdateUserRequest,
        currentUser: CurrentUser
    ) {
        val user = userPort.getUserById(userId) ?: throw EntityNotFoundException("User with id $userId not found")
        userDomainService.updateUserInfo(user, request.email, request.name)
        userPort.updateUser(user)
    }

    @Transactional
    @CheckUserAccess(targetUserId = "userId", currentUser = "currentUser")
    override fun deleteUser(
        userId: UUID,
        currentUser: CurrentUser
    ) {
        val user = userPort.getUserById(userId) ?: throw EntityNotFoundException("User with id $userId not found")
        userPort.deleteUser(user.id)
        applicationEventPublisher.publishEvent(UserDeletedEvent(userId = user.id, email = user.email))
    }

    @AdminOnly(currentUser = "currentUser")
    override fun pageUsers(
        currentUser: CurrentUser,
        pageable: Pageable
    ): Page<UserResponse> {
        return userPort.getAllUsers(pageable)
            .map { UserResponse.from(it) }
    }
}
