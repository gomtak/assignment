package com.protopie.api.auth.application.service

import com.protopie.api.auth.application.command.SignUpCommand
import com.protopie.api.auth.application.port.`in`.SignUpUseCase
import com.protopie.api.common.RoleType
import com.protopie.api.user.application.port.out.UserPort
import com.protopie.api.user.domain.model.User
import jakarta.annotation.PostConstruct
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SignUpService(
    private val userPort: UserPort,
    private val passwordEncoder: PasswordEncoder
) : SignUpUseCase {

    @PostConstruct
    fun init() {
        val admin = "admin@test.com"
        if (!userPort.existsByEmail(admin)) {
            userPort.save(
                User.create(
                    email = admin,
                    password = passwordEncoder.encode("123123"),
                    name = "admin",
                    role = RoleType.ROLE_ADMIN
                )
            )
        }
    }

    override fun signUp(command: SignUpCommand): UUID {
        if (userPort.existsByEmail(command.email)) {
            error("User email already exists")
        }

        val encodedPassword = passwordEncoder.encode(command.password)
        val user = User.create(
            email = command.email,
            password = encodedPassword,
            name = command.name,
            role = RoleType.ROLE_MEMBER
        )
        return userPort.save(user)
    }
}
