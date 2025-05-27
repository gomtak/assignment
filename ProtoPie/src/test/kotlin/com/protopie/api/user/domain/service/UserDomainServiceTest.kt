package com.protopie.api.user.domain.service

import com.protopie.api.user.application.port.out.UserPort
import com.protopie.api.user.domain.model.User
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.assertEquals

class UserDomainServiceTest {

    private lateinit var userPort: UserPort
    private lateinit var userDomainService: UserDomainService
    private lateinit var user: User

    @BeforeEach
    fun setup() {
        userPort = mockk()
        userDomainService = UserDomainService(userPort)
        user = User.of(
            id = UUID.randomUUID(),
            email = "original@test.com",
            name = "Original Name",
            password = "password",
            role = mockk()
        )
    }

    @Test
    fun `이메일과 이름이 모두 null인 경우 예외 발생`() {
        val exception = assertThrows<IllegalArgumentException> {
            userDomainService.updateUserInfo(user, null, null)
        }
        assertEquals("At least one of email or name must be provided", exception.message)
    }

    @Test
    fun `중복되지 않은 이메일과 이름이 주어졌을 때 정상 업데이트`() {
        every { userPort.existsByEmail("new@test.com") } returns false

        // 실제 도메인 객체에서 메서드를 호출하므로 Spy 패턴 필요 없음
        userDomainService.updateUserInfo(user, "new@test.com", "New Name")

        assertEquals("new@test.com", user.email)
        assertEquals("New Name", user.name)
    }

    @Test
    fun `중복된 이메일이 주어졌을 때 예외 발생`() {
        every { userPort.existsByEmail("duplicate@test.com") } returns true

        val exception = assertThrows<IllegalArgumentException> {
            userDomainService.updateUserInfo(user, "duplicate@test.com", "Any Name")
        }

        assertEquals("User with email duplicate@test.com already exists", exception.message)
        assertEquals("original@test.com", user.email) // 변경되지 않아야 함
    }

    @Test
    fun `이메일 없이 이름만 업데이트`() {
        userDomainService.updateUserInfo(user, null, "Updated Name")
        assertEquals("Updated Name", user.name)
    }

    @Test
    fun `이름 없이 이메일만 업데이트`() {
        every { userPort.existsByEmail("email@test.com") } returns false
        userDomainService.updateUserInfo(user, "email@test.com", null)
        assertEquals("email@test.com", user.email)
    }
}
