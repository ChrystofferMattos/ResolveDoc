package com.example.resolvedoc.feature.pendencias.domain.repository

import com.example.resolvedoc.feature.pendencias.domain.model.Pendencia
import kotlinx.coroutines.flow.Flow

interface PendenciaRepository {
    fun getPendenciasStream(): Flow<List<Pendencia>>
    suspend fun createPendencia(p: Pendencia)
}
