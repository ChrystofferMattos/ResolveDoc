package com.example.resolvedoc.feature.pendencias.domain.usecase

import com.example.resolvedoc.feature.pendencias.domain.model.Pendencia
import com.example.resolvedoc.feature.pendencias.domain.repository.PendenciaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPendenciasUseCase @Inject constructor(
    private val repository: PendenciaRepository
) {
    operator fun invoke(): Flow<List<Pendencia>> {
        return repository.getPendenciasStream()
    }
}
