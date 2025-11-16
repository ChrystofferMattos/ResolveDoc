package com.example.resolvedoc.feature.pendencias.data.model

import com.example.resolvedoc.feature.pendencias.domain.model.Pendencia

data class PendenciaDto(
    val medico: String = "",
    val tipo: String = "",
    val descricao: String = "",
    val unidade: String = "",
    val status: String = "Aberto",
    val enviadoEm: Long = System.currentTimeMillis(),
    val resolvidoEm: Long? = null
) {
    fun toDomain(id: String) = Pendencia(
        id = id,
        medico = medico,
        tipo = tipo,
        descricao = descricao,
        unidade = unidade,
        status = status,
        enviadoEm = enviadoEm,
        resolvidoEm = resolvidoEm
    )
}