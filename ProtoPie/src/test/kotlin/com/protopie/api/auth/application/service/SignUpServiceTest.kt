package com.protopie.api.auth.application.service

import com.protopie.api.auth.application.command.SignUpCommand
import com.protopie.api.user.application.port.out.UserPort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.UUID
import kotlin.test.Test

class SignUpServiceTest {

    private val userPort: UserPort = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()
    private val signUpService = SignUpService(userPort, passwordEncoder)

    @Test
    fun `회원가입이 성공하면 userId를 리턴한다`() {
        // given
        val command = SignUpCommand(
            email = "test@example.com",
            password = "plainPassword",
            name = "테스터"
        )
        val userId = UUID.randomUUID()
        every { userPort.existsByEmail(command.email) } returns false
        every { passwordEncoder.encode(command.password) } returns "hashedPassword"
        every { userPort.save(any()) } returns userId

        // when
        val result = signUpService.signUp(command)

        // then
        assertEquals(userId, result)

        verify(exactly = 1) { userPort.existsByEmail(command.email) }
        verify(exactly = 1) { passwordEncoder.encode(command.password) }
        verify(exactly = 1) { userPort.save(any()) }
    }

    @Test
    fun `이미 존재하는 이메일로 회원가입을 시도하면 예외가 발생한다`() {
        // given
        val command = SignUpCommand("test@example.com", "password", "user")

        every { userPort.existsByEmail(command.email) } returns true

        // expect
        org.junit.jupiter.api.assertThrows<IllegalStateException> {
            signUpService.signUp(command)
        }

        verify(exactly = 1) { userPort.existsByEmail(command.email) }
        verify(exactly = 0) { userPort.save(any()) }
    }
}
