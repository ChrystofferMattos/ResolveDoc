package com.example.resolvedoc.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resolvedoc.feature.auth.domain.usecase.ChangePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val name: String = "Profissional CEMAS",
    val email: String = "usuario@cemas.vv.es.gov.br",

    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    fun updateCurrentPassword(currentPassword: String) {
        _state.update { it.copy(currentPassword = currentPassword, error = null, isSuccess = false) }
    }

    fun updateNewPassword(newPassword: String) {
        _state.update { it.copy(newPassword = newPassword, error = null, isSuccess = false) }
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _state.update { it.copy(confirmPassword = confirmPassword, error = null, isSuccess = false) }
    }

    fun resetPasswordState() {
        _state.update { it.copy(isSuccess = false, error = null) }
    }

    fun clearMessages() {
        _state.update { it.copy(error = null, isSuccess = false) }
    }

    fun changePassword() {
        val current = _state.value

        _state.update { it.copy(isLoading = true, error = null, isSuccess = false) }

        if (current.newPassword.isBlank() || current.confirmPassword.isBlank()) {
            _state.update {
                it.copy(
                    error = "Preencha a nova senha.",
                    isLoading = false
                )
            }
            return
        }

        if (current.newPassword != current.confirmPassword) {
            _state.update {
                it.copy(
                    error = "As novas senhas não coincidem.",
                    isLoading = false
                )
            }
            return
        }

        viewModelScope.launch {
            try {
                // ✅ agora chamando o use case corretamente
                changePasswordUseCase(current.currentPassword, current.newPassword)

                _state.update {
                    it.copy(
                        currentPassword = "",
                        newPassword = "",
                        confirmPassword = "",
                        isSuccess = true,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Falha ao alterar senha: ${e.message}",
                        isLoading = false,
                        isSuccess = false
                    )
                }
            }
        }
    }
}
