package com.example.resolvedoc.feature.pendencias.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resolvedoc.feature.pendencias.domain.usecase.CreatePendenciaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class NewPendenciaViewModel @Inject constructor(
    private val createPendenciaUseCase: CreatePendenciaUseCase
) : ViewModel() {

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun savePendencia(
        medico: String,
        tipo: String,
        descricao: String,
        unidade: String
    ) {
        viewModelScope.launch {
            val medicoTrimmed = medico.trim()
            val tipoTrimmed = tipo.trim()
            val descricaoTrimmed = descricao.trim()
            val unidadeTrimmed = unidade.trim()

            // Validação simples
            if (
                medicoTrimmed.isBlank() ||
                tipoTrimmed.isBlank() ||
                descricaoTrimmed.isBlank() ||
                unidadeTrimmed.isBlank()
            ) {
                _error.value = "Preencha todos os campos."
                return@launch
            }

            _isSaved.value = false
            _isSaving.value = true
            _error.value = null

            try {
                Log.d("FIREBASE_TEST", "Iniciando salvamento...")
                createPendenciaUseCase(
                    medicoTrimmed,
                    tipoTrimmed,
                    descricaoTrimmed,
                    unidadeTrimmed
                )
                _isSaved.value = true
                Log.d("FIREBASE_TEST", "Salvamento concluído com sucesso!")
            } catch (e: Exception) {
                Log.e("FIREBASE_TEST", "Erro ao salvar pendência", e)
                _error.value = e.message ?: "Erro ao salvar pendência."
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun resetSavedState() {
        _isSaved.value = false
    }
}