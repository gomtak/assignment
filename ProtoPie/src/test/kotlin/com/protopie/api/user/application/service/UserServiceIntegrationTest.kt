package com.protopie.api.user.application.service

import com.protopie.api.auth.security.CurrentUser
import com.protopie.api.common.RoleType
import com.protopie.api.config.TestContainerConfig
import com.protopie.api.user.adapters.dto.UpdateUserRequest
import com.protopie.api.user.adapters.dto.UserDeletedEvent
import com.protopie.api.user.adapters.out.entity.UserEntity
import com.protopie.api.user.adapters.out.repository.UserRepository
import com.protopie.api.user.application.port.`in`.UserUseCase
import com.protopie.api.user.domain.model.User
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.event.ApplicationEvents
import org.springframework.test.context.event.RecordApplicationEvents
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@RecordApplicationEvents
@Transactional
class UserServiceIntegrationTest: TestContainerConfig() {
    @Autowired
    private lateinit var userUseCase: UserUseCase

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var events: ApplicationEvents

    @Test
    fun `사용자 조회`() {
        val user = userRepository.save(
            UserEntity.create(
                email = "test@test.com",
                password = "password",
                name = "test",
                role = RoleType.ROLE_MEMBER
            )
        )
        val result = userUseCase.fetchUser(user.id!!, mockCurrentUser(user.id!!))

        assertEquals(user.id, result.id)
        assertEquals("test@test.com", result.email)
    }

    @Test
    fun `사용자 정보 수정`() {
        val user = userRepository.save(
            UserEntity.create(
                email = "a@a.com",
                password = "password",
                name = "old",
                role = RoleType.ROLE_MEMBER
            )
        )

        userUseCase.updateUser(user.id!!, UpdateUserRequest("b@b.com", "new"), mockCurrentUser(user.id!!))

        val updated = userUseCase.fetchUser(user.id!!, mockCurrentUser(user.id!!))
        assertEquals("b@b.com", updated.email)
        assertEquals("new", updated.name)
    }

    @Test
    fun `사용자 삭제시 DB 삭제 및 UserDeletedEvent 발행`() {
        val user = userRepository.save(
            UserEntity.create(
                email = "delete@test.com",
                password = "password",
                name = "delete-me",
                role = RoleType.ROLE_MEMBER
            )
        )

        userUseCase.deleteUser(user.id!!, mockCurrentUser(user.id!!))

        assertThrows(EntityNotFoundException::class.java) {
            userUseCase.fetchUser(user.id!!, mockCurrentUser(user.id!!))
        }

        val event = events.stream(UserDeletedEvent::class.java).findFirst().orElse(null)
        assertNotNull(event)
        assertEquals(user.id, event.userId)
        assertEquals(user.email, event.email)
    }

    @Test
    fun `관리자 전용 사용자 목록 조회`() {
        repeat(5) {
            userRepository.save(
                UserEntity.create(
                    email = "user$it@test.com",
                    password = "password",
                    name = "user$it",
                    role = RoleType.ROLE_MEMBER
                )
            )
        }

        val result = userUseCase.pageUsers(mockAdmin(), Pageable.unpaged())
        assertEquals(5, result.totalElements)
    }

    private fun mockCurrentUser(userId: UUID) = CurrentUser(
        userId = userId,
        roleType = RoleType.ROLE_MEMBER
    )

    private fun mockAdmin() = CurrentUser(
        userId = UUID.randomUUID(),
        roleType = RoleType.ROLE_ADMIN
    )
}
