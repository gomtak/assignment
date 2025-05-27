package com.protopie.api.user.domain.service

import com.protopie.api.user.application.port.out.UserPort
import com.protopie.api.user.domain.model.User
import org.springframework.stereotype.Component

@Component
class UserDomainService(
    private val userPort: UserPort,
) {
    fun updateUserInfo(user: User, email: String?, name: String?) {
        if (email == null && name == null) {
            throw IllegalArgumentException("At least one of email or name must be provided")
        }
        email?.let {
            if (!userPort.existsByEmail(it)) {
                user.updateEmail(it)
            } else {
                throw IllegalArgumentException("User with email $it already exists")
            }
        }
        name?.let {
            user.updateName(it)
        }
    }
}
