package com.protopie.api.user.application.service

import com.protopie.api.auth.security.CurrentUser
import com.protopie.api.common.RoleType
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import java.util.UUID

@Aspect
@Component
class UserAccessCheckAspect {

    @Before("@annotation(checkUserAccess)")
    fun checkPermission(joinPoint: JoinPoint, checkUserAccess: CheckUserAccess) {
        val methodSignature = joinPoint.signature as MethodSignature
        val paramNames = methodSignature.parameterNames
        val args = joinPoint.args

        val userIdIndex = paramNames.indexOf(checkUserAccess.targetUserId)
        val currentUserIndex = paramNames.indexOf(checkUserAccess.currentUser)

        if (userIdIndex == -1 || currentUserIndex == -1) {
            throw IllegalArgumentException("지정한 파라미터 이름을 찾을 수 없습니다.")
        }

        val targetUserId = args[userIdIndex] as UUID
        val currentUser = args[currentUserIndex] as CurrentUser

        if (currentUser.roleType != RoleType.ROLE_ADMIN && currentUser.userId != targetUserId) {
            throw AccessDeniedException("해당 유저 정보를 조회할 권한이 없습니다.")
        }
    }
}

