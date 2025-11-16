package com.example.resolvedoc.feature.pendencias.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resolvedoc.feature.pendencias.domain.model.Pendencia
import com.example.resolvedoc.feature.pendencias.domain.usecase.GetPendenciasUseCase
import com.example.resolvedoc.feature.pendencias.domain.usecase.MarkPendenciaAsResolvidaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PendenciaDetailUiState(
    val isLoading: Boolean = false,
    val pendencia: Pendencia? = null,
    val isUpdating: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null
)

@HiltViewModel
class PendenciaDetailViewModel @Inject constructor(
    private val getPendenciasUseCase: GetPendenciasUseCase,
    private val markPendenciaAsResolvidaUseCase: MarkPendenciaAsResolvidaUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(PendenciaDetailUiState())
    val state: StateFlow<PendenciaDetailUiState> = _state

    private val pendenciaId: String =
        savedStateHandle["pendenciaId"] ?: ""

    init {
        loadPendencia()
    }

    private fun loadPendencia() {
        if (pendenciaId.isBlank()) {
            _state.value = PendenciaDetailUiState(
                isLoading = false,
                error = "ID da pendência inválido."
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {

                getPendenciasUseCase()
                    .collect { lista ->
                        val encontrada = lista.find { it.id == pendenciaId }
                        _state.value = _state.value.copy(
                            isLoading = false,
                            pendencia = encontrada,
                            error = if (encontrada == null) "Pendência não encontrada." else null
                        )
                    }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao carregar pendência."
                )
            }
        }
    }

    fun marcarComoResolvida() {
        val atual = _state.value.pendencia ?: return

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isUpdating = true,
                error = null,
                successMessage = null
            )

            try {
                markPendenciaAsResolvidaUseCase(atual.id)
                _state.value = _state.value.copy(
                    isUpdating = false,
                    successMessage = "Pendência marcada como resolvida."
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isUpdating = false,
                    error = e.message ?: "Erro ao atualizar pendência."
                )
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(
            error = null,
            successMessage = null
        )
    }
}
