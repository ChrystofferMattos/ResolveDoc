package com.example.resolvedoc.feature.pendencias.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resolvedoc.feature.pendencias.domain.model.Pendencia
import com.example.resolvedoc.feature.pendencias.domain.usecase.GetPendenciasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val abertas: Int = 0,
    val resolvidas: Int = 0,
    val tempoMedioResolucaoDias: Double? = null,
    val pendenciasRecentes: List<Pendencia> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getPendenciasUseCase: GetPendenciasUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state

    init {
        observarPendencias()
    }

    private fun observarPendencias() {
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
                        error = e.message ?: "Erro ao carregar dados da dashboard."
                    )
                }
                .collect { pendencias ->
                    _state.value = montarEstado(pendencias)
                }
        }
    }

    private fun montarEstado(pendencias: List<Pendencia>): DashboardUiState {
        if (pendencias.isEmpty()) {
            return DashboardUiState(
                isLoading = false,
                error = null
            )
        }

        val total = pendencias.size
        val resolvidas = pendencias.count { it.status == "Resolvida" }
        val abertas = total - resolvidas

        val tempoMedioResolucao = calcularTempoMedioResolucaoDias(pendencias)

        // Ordena por data de envio (mais recentes primeiro)
        val recentes = pendencias
            .sortedByDescending { it.enviadoEm }
            .take(4)

        return DashboardUiState(
            isLoading = false,
            error = null,
            abertas = abertas,
            resolvidas = resolvidas,
            tempoMedioResolucaoDias = tempoMedioResolucao,
            pendenciasRecentes = recentes
        )
    }

    private fun calcularTempoMedioResolucaoDias(pendencias: List<Pendencia>): Double? {
        val resolvidasComDatas = pendencias.filter {
            it.status == "Resolvida" &&
                    it.resolvidoEm != null &&
                    it.enviadoEm > 0L
        }

        if (resolvidasComDatas.isEmpty()) return null

        val dias = resolvidasComDatas.map { p ->
            val diffMillis = (p.resolvidoEm!! - p.enviadoEm).coerceAtLeast(0L)
            diffMillis / (1000.0 * 60 * 60 * 24) // ms -> dias
        }

        return dias.average()
    }
}
