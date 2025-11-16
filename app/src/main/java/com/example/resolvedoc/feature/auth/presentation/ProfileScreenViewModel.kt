package com.example.resolvedoc.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.resolvedoc.feature.auth.domain.usecase.ChangePasswordUseCase

data class PasswordUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase,
) : ViewModel() {

    private val _passwordState = MutableStateFlow(PasswordUiState())
    val passwordState: StateFlow<PasswordUiState> = _passwordState.asStateFlow()


    fun onCurrentPasswordChange(currentPassword: String) {
        _passwordState.update { it.copy(currentPassword = currentPassword, error = null) }
    }

    fun onNewPasswordChange(newPassword: String) {
        _passwordState.update { it.copy(newPassword = newPassword, error = null) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _passwordState.update { it.copy(confirmPassword = confirmPassword, error = null) }
    }

    fun resetPasswordState() {
        _passwordState.update { it.copy(isSuccess = false, error = null) }
    }

    fun changePassword() {
        val state = _passwordState.value
        _passwordState.update { it.copy(isLoading = true, error = null) }

        if (state.newPassword.isBlank() || state.confirmPassword.isBlank()) {
            _passwordState.update { it.copy(error = "Preencha a nova senha.", isLoading = false) }
            return
        }


        if (state.newPassword != state.confirmPassword) {
            _passwordState.update { it.copy(error = "As novas senhas não coincidem.", isLoading = false) }
            return
        }

        viewModelScope.launch {
            try {

                changePasswordUseCase.execute(state.currentPassword, state.newPassword)


                _passwordState.update {
                    it.copy(
                        currentPassword = "",
                        newPassword = "",
                        confirmPassword = "",
                        isSuccess = true,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _passwordState.update {
                    it.copy(
                        error = "Falha ao alterar senha: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
}
