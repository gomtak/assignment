package com.protopie.api.auth.application.port.`in`

import com.protopie.api.auth.application.command.SignUpCommand
import java.util.UUID

interface SignUpUseCase {
    fun signUp(command: SignUpCommand): UUID
}
