package com.protopie.api.auth.adapters.`in`

import com.protopie.api.auth.adapters.dto.SignUpRequest
import com.protopie.api.auth.application.command.SignUpCommand
import com.protopie.api.auth.application.port.`in`.SignUpUseCase
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/signup")
class SignUpController(
    private val signUpUseCase: SignUpUseCase,
) {

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 이름을 받아 회원가입을 수행합니다.")
    @PostMapping
    fun signUp(@RequestBody request: SignUpRequest) = ok(
        signUpUseCase.signUp(
            SignUpCommand(
                email = request.email,
                password = request.password,
                name = request.name
            )
        )
    )
}
