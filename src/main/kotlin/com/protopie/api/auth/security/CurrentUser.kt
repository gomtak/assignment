package com.protopie.api.auth.security

import com.protopie.api.common.RoleType
import java.util.UUID

data class CurrentUser(
    val userId: UUID,
    val roleType: RoleType,
)
