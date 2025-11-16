package com.example.resolvedoc.feature.auth.domain.usecase

import com.example.resolvedoc.feature.auth.domain.AuthRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend fun execute(email: String, password: String): Boolean {
        delay(1000)
        return email == "coordenadora@cemas.com" && password == "123456"
    }
}