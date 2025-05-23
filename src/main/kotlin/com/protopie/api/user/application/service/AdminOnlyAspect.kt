package com.protopie.api.user.application.service

import com.protopie.api.auth.security.CurrentUser
import com.protopie.api.common.RoleType
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import org.springframework.security.access.AccessDeniedException

@Aspect
@Component
class AdminOnlyAspect {

    @Before("@annotation(adminOnly)")
    fun checkAdminAccess(joinPoint: JoinPoint, adminOnly: AdminOnly) {
        val signature = joinPoint.signature as MethodSignature
        val paramNames = signature.parameterNames
        val args = joinPoint.args

        val userIndex = paramNames.indexOf(adminOnly.currentUser)
        if (userIndex == -1) {
            throw IllegalArgumentException("AdminOnly: 지정한 파라미터 '${adminOnly.currentUser}'를 찾을 수 없습니다.")
        }

        val currentUser = args[userIndex] as? CurrentUser
            ?: throw AccessDeniedException("AdminOnly: CurrentUser 파라미터가 유효하지 않습니다.")

        if (currentUser.roleType != RoleType.ROLE_ADMIN) {
            throw AccessDeniedException("관리자만 접근할 수 있습니다.")
        }
    }
}

