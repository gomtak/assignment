package com.protopie.api.auth.application.service

import com.protopie.api.auth.application.command.SignUpCommand
import com.protopie.api.auth.application.port.`in`.SignUpUseCase
import com.protopie.api.config.TestContainerConfig
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test

@SpringBootTest
@Transactional
class SignUpServiceIntegrationTest: TestContainerConfig() {

    @Autowired
    lateinit var signUpUseCase: SignUpUseCase

    @Test
    fun `회원가입 통합 테스트 - userId 리턴 확인`() {
        // given
        val command = SignUpCommand(
            email = "integration@test.com",
            password = "12345678",
            name = "Integration User"
        )

        // when
        val userId = signUpUseCase.signUp(command)

        // then
        assertTrue(userId.toString().isNotBlank())
    }
}
