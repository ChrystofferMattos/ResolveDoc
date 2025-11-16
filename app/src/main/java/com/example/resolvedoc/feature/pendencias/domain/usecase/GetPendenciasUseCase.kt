package com.example.resolvedoc.feature.pendencias.domain.usecase

import com.example.resolvedoc.feature.pendencias.domain.repository.PendenciaRepository

class GetPendenciasUseCase(private val repo: PendenciaRepository) {
    operator fun invoke() = repo.getPendenciasStream()
}
