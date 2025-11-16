package com.example.resolvedoc.feature.pendencias.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resolvedoc.feature.pendencias.domain.model.Pendencia
import com.example.resolvedoc.feature.pendencias.domain.usecase.GetPendenciasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PendenciasViewModel @Inject constructor(
    private val getPendenciasUseCase: GetPendenciasUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PendenciasUiState())
    val state: StateFlow<PendenciasUiState> = _state

    init {
        observePendencias()
    }

    private fun observePendencias() {
        viewModelScope.launch {
            getPendenciasUseCase()
                .onStart {
                    _state.value = _state.value.copy(
                        isLoading = true,
                        error = null
                    )
                }
                .catch { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Erro ao carregar pendências."
                    )
                }
                .collect { pendencias ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        pendencias = pendencias,
                        error = null
                    )
                }
        }
    }
}

data class PendenciasUiState(
    val isLoading: Boolean = false,
    val pendencias: List<Pendencia> = emptyList(),
    val error: String? = null
)
