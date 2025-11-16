package com.example.resolvedoc.feature.pendencias.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.resolvedoc.feature.pendencias.domain.usecase.CreatePendenciaUseCase


@HiltViewModel
class NewPendenciaViewModel @Inject constructor(

    private val createPendenciaUseCase: CreatePendenciaUseCase
) : ViewModel() {

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    fun resetSavedState() {
        _isSaved.value = false
    }


    fun savePendencia(
        medico: String,
        tipo: String,
        descricao: String,
        unidade: String
    ) {
        viewModelScope.launch {
            _isSaved.value = false
            try {

                createPendenciaUseCase.invoke(medico, tipo, descricao, unidade)
                _isSaved.value = true

                Log.d("FIREBASE_TEST", "Iniciando salvamento...")
                createPendenciaUseCase.invoke(medico, tipo, descricao, unidade)
                _isSaved.value = true
                Log.d("FIREBASE_TEST", "Salvamento concluído com sucesso!")

            } catch (e: Exception) {

                Log.e("NewPendenciaVM", "Falha ao salvar pendência", e)

                _isSaved.value = false

                Log.e("FIREBASE_TEST", "Falha catastrófica ao salvar. Verifique REGRAS e DEPENDÊNCIAS.", e)
            }
        }
    }
}