package com.example.resolvedoc.feature.pendencias.domain.usecase

import com.example.resolvedoc.feature.pendencias.domain.model.Pendencia
import com.example.resolvedoc.feature.pendencias.domain.repository.PendenciaRepository
import javax.inject.Inject

class CreatePendenciaUseCase @Inject constructor(
    private val repository: PendenciaRepository
) {
    suspend operator fun invoke(
        medico: String,
        tipo: String,
        descricao: String,
        unidade: String
    ) {

        val novaPendencia = Pendencia(
            medico = medico,
            tipo = tipo,
            descricao = descricao,
            unidade = unidade,
            status = "Aberta",
            enviadoEm = System.currentTimeMillis()
        )


        repository.createPendencia(novaPendencia)
    }
}