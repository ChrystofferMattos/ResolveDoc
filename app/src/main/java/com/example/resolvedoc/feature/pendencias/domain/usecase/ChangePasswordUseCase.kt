package com.example.resolvedoc.feature.pendencias.domain.usecase
// ChangePasswordUseCase.kt

package com.example.resolvedoc.feature.auth.domain.usecase

import javax.inject.Inject
import com.example.resolvedoc.feature.auth.domain.AuthRepository // Assumindo que o repositório tem a lógica

class ChangePasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository // Injete o Repositório de Autenticação
) {
    /**
     * Re-autentica e altera a senha do usuário.
     */
    suspend fun execute(currentPassword: String, newPassword: String) {
        // A lógica de re-autenticação e alteração da senha deve estar no repositório.
        authRepository.changePassword(currentPassword, newPassword)
    }
}