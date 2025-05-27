package com.protopie.api.user.application.service

import com.protopie.api.common.RoleType
import com.protopie.api.user.adapters.dto.UpdateUserRequest
import com.protopie.api.user.adapters.dto.UserDeletedEvent
import com.protopie.api.user.domain.model.User
import com.protopie.api.user.application.port.out.UserPort
import com.protopie.api.user.domain.service.UserDomainService
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.util.*
import jakarta.persistence.EntityNotFoundException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UserServiceTest {

    private lateinit var userService: UserService
    private val userPort: UserPort = mockk()
    private val userDomainService: UserDomainService = mockk()
    private val eventPublisher: ApplicationEventPublisher = mockk(relaxed = true) // deletedAt이 매번 달라짐

    private val userId = UUID.randomUUID()
    private val user = User.of(
        id = userId,
        email = "test@test.com",
        name = "Test",
        password = "password",
        role = RoleType.ROLE_MEMBER
    )

    @BeforeEach
    fun setup() {
        userService = UserService(userPort, userDomainService, eventPublisher)
    }

    @Test
    fun `fetchUser - success`() {
        every { userPort.getUserById(userId) } returns user

        val result = userService.fetchUser(userId, currentUser = mockk())

        assertEquals(userId, result.id)
    }

    @Test
    fun `fetchUser - user not found`() {
        every { userPort.getUserById(userId) } returns null

        assertFailsWith<EntityNotFoundException> {
            userService.fetchUser(userId, currentUser = mockk())
        }
    }

    @Test
    fun `updateUser - success`() {
        every { userPort.getUserById(userId) } returns user
        every { userDomainService.updateUserInfo(user, any(), any()) } just Runs
        every { userPort.updateUser(user) } just Runs

        userService.updateUser(userId, UpdateUserRequest("new@test.com", "New Name"), mockk())

        verify { userDomainService.updateUserInfo(user, "new@test.com", "New Name") }
        verify { userPort.updateUser(user) }
    }

    @Test
    fun `deleteUser - success`() {
        every { userPort.getUserById(userId) } returns user
        every { userPort.deleteUser(userId) } just Runs

        userService.deleteUser(userId, mockk())

        verify { userPort.deleteUser(userId) }
        verify { eventPublisher.publishEvent(ofType(UserDeletedEvent::class)) }
    }
}
