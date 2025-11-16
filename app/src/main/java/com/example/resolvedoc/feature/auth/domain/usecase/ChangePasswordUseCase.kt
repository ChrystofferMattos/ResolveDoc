package com.example.resolvedoc.feature.auth.domain.usecase

import com.example.resolvedoc.feature.auth.domain.AuthRepository
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(currentPassword: String, newPassword: String) {
        authRepository.changePassword(currentPassword, newPassword)
    }
}
