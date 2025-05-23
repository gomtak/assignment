package com.protopie.api.user.adapters.`in`.web

import com.protopie.api.auth.security.CurrentUser
import com.protopie.api.user.adapters.dto.UpdateUserRequest
import com.protopie.api.user.application.port.`in`.UserUseCase
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/users")
class UserController(
    private val userUseCase: UserUseCase
) {

    @Operation(summary = "사용자 정보 조회", description = "지정한 사용자 ID로 사용자 정보를 조회합니다.")
    @GetMapping("/{userId}")
    fun fetchUser(
        @PathVariable userId: UUID,
        @AuthenticationPrincipal currentUser: CurrentUser
    ) = ResponseEntity.ok(userUseCase.fetchUser(userId, currentUser))

    @Operation(summary = "사용자 정보 수정", description = "지정한 사용자 ID의 이메일 또는 이름을 수정합니다.")
    @PutMapping("/{userId}")
    fun updateUser(
        @PathVariable userId: UUID,
        @AuthenticationPrincipal currentUser: CurrentUser,
        @RequestBody request: UpdateUserRequest
    ): ResponseEntity<Void> {
        userUseCase.updateUser(userId, request, currentUser)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "사용자 탈퇴", description = "지정한 사용자 ID를 탈퇴 처리합니다.")
    @DeleteMapping("/{userId}")
    fun deleteUser(
        @PathVariable userId: UUID,
        @AuthenticationPrincipal currentUser: CurrentUser
    ): ResponseEntity<Void> {
        userUseCase.deleteUser(userId, currentUser)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "모든 사용자 조회 (Admin 전용)", description = "관리자 권한으로 전체 사용자 목록을 페이징으로 조회합니다.")
    @GetMapping
    fun pageUsers(
        @AuthenticationPrincipal currentUser: CurrentUser,
        pageable: Pageable
    ) = ResponseEntity.ok(userUseCase.pageUsers(currentUser, pageable))

}
