package com.protopie.api.auth.application.service

import com.protopie.api.auth.adapters.dto.TokenResponse
import com.protopie.api.auth.application.command.SignUpCommand
import com.protopie.api.auth.application.port.`in`.SignInUseCase
import com.protopie.api.auth.application.port.`in`.SignUpUseCase
import com.protopie.api.config.TestContainerConfig
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test

@SpringBootTest
@Transactional
class SignInServiceIntegrationTest: TestContainerConfig() {
    @Autowired
    private lateinit var signInUseCase: SignInUseCase

    @Autowired
    private lateinit var signUpUseCase: SignUpUseCase

    @Test
    fun `정상 로그인 시 access, refresh 토큰이 발급된다`() {
        // given
        val email = "integration@test.com"
        val password = "secure1234"
        val name = "테스트"

        signUpUseCase.signUp(
            SignUpCommand(
                email = email,
                password = password,
                name = name
            )
        )

        // when
        val tokenResponse: TokenResponse = signInUseCase.signIn(email, password)

        // then
        assertTrue(tokenResponse.accessToken.isNotBlank())
        assertTrue(tokenResponse.refreshToken.isNotBlank())
    }

    @Test
    fun `비밀번호가 틀리면 예외가 발생한다`() {
        // given
        val email = "wrongpass@test.com"
        val password = "correct1234"
        signUpUseCase.signUp(SignUpCommand(email, password, "유저"))

        // when + then
        assertThrows(BadCredentialsException::class.java) {
            signInUseCase.signIn(email, "wrongPassword")
        }
    }
}
