package com.protopie.api.auth.security

import com.protopie.api.common.RoleType
import org.springframework.security.core.CredentialsContainer
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

data class CustomUserDetails(
    val id: UUID,
    val email: String,
    private var password: String?,
    val roleType: RoleType,
) : UserDetails, CredentialsContainer {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority(roleType.name))
    }

    override fun getPassword(): String? {
        return password
    }

    override fun getUsername(): String {
        return email
    }

    override fun eraseCredentials() {
        password = ""
    }
}
