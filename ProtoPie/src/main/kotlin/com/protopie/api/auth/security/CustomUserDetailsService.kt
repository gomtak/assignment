package com.protopie.api.auth.security

import com.protopie.api.user.application.port.out.UserPort
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userPort: UserPort
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails? {
        val user = userPort.findByEmail(username) ?: throw UsernameNotFoundException(username)

        return CustomUserDetails(
            id = user.id,
            email = user.email,
            password = user.password,
            roleType = user.roleType
        )
    }
}
