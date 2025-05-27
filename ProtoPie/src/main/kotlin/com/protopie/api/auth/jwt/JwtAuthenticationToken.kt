package com.protopie.api.auth.jwt

import com.protopie.api.auth.security.CurrentUser
import com.protopie.api.common.RoleType
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.UUID

class JwtAuthenticationToken(
    val userId: UUID,
    val roleType: RoleType
) : AbstractAuthenticationToken(listOf(SimpleGrantedAuthority(roleType.toString()))) {

    init {
        isAuthenticated = true
    }

    override fun getCredentials(): Any? = null

    override fun getPrincipal(): Any = CurrentUser(userId, roleType)
}

