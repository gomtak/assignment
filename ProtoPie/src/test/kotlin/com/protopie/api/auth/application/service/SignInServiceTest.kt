package com.protopie.api.auth.application.service

import com.protopie.api.auth.adapters.dto.TokenResponse
import com.protopie.api.auth.jwt.JwtTokenProvider
import com.protopie.api.auth.security.CustomUserDetails
import com.protopie.api.common.RoleType
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.Authentication
import java.util.UUID
import kotlin.test.assertEquals

class SignInServiceTest {

    private val authenticationManagerBuilder: AuthenticationManagerBuilder = mockk()
    private val authenticationManager: AuthenticationManager = mockk()
    private val jwtTokenProvider: JwtTokenProvider = mockk()

    private lateinit var signInService: SignInService

    @BeforeEach
    fun setup() {
        every { authenticationManagerBuilder.getObject() } returns authenticationManager
        signInService = SignInService(jwtTokenProvider, authenticationManagerBuilder)
    }

    @Test
    fun `인증에 성공하면 TokenResponse를 반환해야 한다`() {
        // given
        val email = "test@example.com"
        val password = "secure123"
        val userId = UUID.randomUUID()
        val userDetails = CustomUserDetails(
            id = userId,
            email = email,
            password = password,
            roleType = RoleType.ROLE_MEMBER
        )

        val authentication: Authentication = mockk()
        every { authentication.principal } returns userDetails
        every { authenticationManager.authenticate(any()) } returns authentication

        val expectedToken = TokenResponse("access-token", "refresh-token")
        every { jwtTokenProvider.createToken(userDetails) } returns expectedToken

        // when
        val result = signInService.signIn(email, password)

        // then
        assertEquals(expectedToken, result)

        verify { authenticationManagerBuilder.getObject() }
        verify { authenticationManager.authenticate(match {
            it is UsernamePasswordAuthenticationToken &&
                    it.principal == email &&
                    it.credentials == password
        }) }
        verify { jwtTokenProvider.createToken(userDetails) }
    }
}
