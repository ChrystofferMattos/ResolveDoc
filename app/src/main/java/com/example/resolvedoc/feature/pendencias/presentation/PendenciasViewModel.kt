package com.example.resolvedoc.feature.pendencias.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.resolvedoc.feature.pendencias.domain.model.Pendencia
import com.example.resolvedoc.feature.pendencias.domain.usecase.GetPendenciasUseCase


@HiltViewModel
class PendenciasViewModel @Inject constructor(
    private val getPendenciasUseCase: GetPendenciasUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PendenciasUiState())
    val state: StateFlow<PendenciasUiState> = _state.asStateFlow()

    init { observe() }

    private fun observe() {

        val pendenciasFlow: Flow<List<Pendencia>> = getPendenciasUseCase.invoke()

        viewModelScope.launch {

            pendenciasFlow.collect { list ->
                _state.update {
                    it.copy(pendencias = list, isLoading = false, error = null)
                }
            }
        }
    }
}


data class PendenciasUiState(
    val pendencias: List<Pendencia> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)