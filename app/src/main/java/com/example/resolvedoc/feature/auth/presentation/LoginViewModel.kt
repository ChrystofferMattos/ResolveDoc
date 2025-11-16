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
import com.example.resolvedoc.feature.auth.domain.usecase.LoginUseCase


data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(

    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onUsernameChange(newUsername: String) {
        _state.update { it.copy(username = newUsername, error = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _state.update { it.copy(password = newPassword, error = null) }
    }


    fun resetLoginState() {
        _state.update { it.copy(isLoginSuccess = false) }
    }

    fun login() {
        _state.update { it.copy(isLoading = true, error = null) }

        val username = _state.value.username
        val password = _state.value.password

        if (username.isBlank() || password.isBlank()) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = "Preencha o nome de usuário e a senha."
                )
            }
            return
        }

        viewModelScope.launch {
            try {

                val result = loginUseCase.execute(username, password)

                if (result) {
                    _state.update { it.copy(isLoginSuccess = true, isLoading = false) }
                } else {
                    _state.update { it.copy(error = "Credenciais inválidas.", isLoading = false) }
                }

            } catch (e: Exception) {
                _state.update { it.copy(error = "Erro de conexão: ${e.message}", isLoading = false) }
            }
        }
    }
}