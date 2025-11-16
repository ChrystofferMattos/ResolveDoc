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
import kotlin.math.roundToInt
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.resolvedoc.feature.pendencias.report.ReportPdfGenerator
import kotlinx.coroutines.launch

data class MedicoResumo(
    val medico: String,
    val total: Int,
    val abertas: Int,
    val resolvidas: Int
)

data class ReportsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val total: Int = 0,
    val abertas: Int = 0,
    val resolvidas: Int = 0,
    val taxaResolucao: Int = 0, // em %

    val tempoMedioResolucaoDias: Double? = null, // null = não calculado

    val porStatus: Map<String, Int> = emptyMap(),
    val porTipo: Map<String, Int> = emptyMap(),

    val resumoPorMedico: List<MedicoResumo> = emptyList()
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getPendenciasUseCase: GetPendenciasUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ReportsUiState())
    val state: StateFlow<ReportsUiState> = _state

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
                        error = e.message ?: "Erro ao carregar relatórios."
                    )
                }
                .collect { pendencias ->
                    _state.value = montarEstado(pendencias)
                }
        }
    }

    private fun montarEstado(pendencias: List<Pendencia>): ReportsUiState {
        if (pendencias.isEmpty()) {
            return ReportsUiState(
                isLoading = false,
                error = null
            )
        }

        val total = pendencias.size

        val porStatus = pendencias.groupingBy { it.status.ifBlank { "Sem status" } }.eachCount()
        val resolvidas = porStatus["Resolvida"] ?: 0
        val abertas = total - resolvidas

        val taxaResolucao = if (total > 0) {
            ((resolvidas.toDouble() / total.toDouble()) * 100).roundToInt()
        } else 0

        val tempoMedioResolucaoDias = calcularTempoMedioResolucao(pendencias)

        val porTipo = pendencias.groupingBy { it.tipo.ifBlank { "Sem tipo" } }.eachCount()

        val resumoPorMedico = montarResumoPorMedico(pendencias)

        return ReportsUiState(
            isLoading = false,
            error = null,
            total = total,
            abertas = abertas,
            resolvidas = resolvidas,
            taxaResolucao = taxaResolucao,
            tempoMedioResolucaoDias = tempoMedioResolucaoDias,
            porStatus = porStatus,
            porTipo = porTipo,
            resumoPorMedico = resumoPorMedico
        )
    }

    private fun calcularTempoMedioResolucao(pendencias: List<Pendencia>): Double? {

        val resolvidasComDatas = pendencias.filter {
            it.resolvidoEm != null && it.enviadoEm > 0L
        }

        if (resolvidasComDatas.isEmpty()) return null

        val mediasEmDias = resolvidasComDatas.map { p ->
            val diffMillis = (p.resolvidoEm!! - p.enviadoEm).coerceAtLeast(0L)
            diffMillis / (1000.0 * 60 * 60 * 24) // ms para dias
        }

        return mediasEmDias.average()
    }
    fun exportToPdf(context: Context) {
        val currentState = state.value

        viewModelScope.launch {
            ReportPdfGenerator.generate(context, currentState)
        }
    }
    private fun montarResumoPorMedico(pendencias: List<Pendencia>): List<MedicoResumo> {
        if (pendencias.isEmpty()) return emptyList()

        val porMedico = pendencias.groupBy { it.medico.ifBlank { "Sem médico" } }

        return porMedico.map { (medico, lista) ->
            val total = lista.size
            val resolvidas = lista.count { it.status == "Resolvida" }
            val abertas = total - resolvidas

            MedicoResumo(
                medico = medico,
                total = total,
                abertas = abertas,
                resolvidas = resolvidas
            )
        }.sortedByDescending { it.total }
    }
}
