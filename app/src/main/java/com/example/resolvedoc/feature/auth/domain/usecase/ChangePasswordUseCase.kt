package com.example.resolvedoc.feature.auth.domain.usecase

import javax.inject.Inject
import com.example.resolvedoc.feature.auth.domain.AuthRepository

class ChangePasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun execute(currentPassword: String, newPassword: String) {

        authRepository.changePassword(currentPassword, newPassword)
    }
}