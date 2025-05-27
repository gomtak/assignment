package com.protopie.api.auth.adapters.`in`

import com.protopie.api.auth.adapters.dto.SignInRequest
import com.protopie.api.auth.application.port.`in`.SignInUseCase
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/signin")
class SignInController(
    private val signInUseCase: SignInUseCase,
) {

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 JWT 토큰을 반환합니다.")
    @PostMapping
    fun signIn(@RequestBody request: SignInRequest) = ok(signInUseCase.signIn(request.email, request.password))
}
