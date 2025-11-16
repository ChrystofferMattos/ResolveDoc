package com.example.resolvedoc.feature.pendencias.domain.usecase

import com.example.resolvedoc.feature.pendencias.domain.repository.PendenciaRepository
import javax.inject.Inject

class MarkPendenciaAsResolvidaUseCase @Inject constructor(
    private val repository: PendenciaRepository
) {
    suspend operator fun invoke(id: String) {
        repository.markAsResolved(id)
    }
}
