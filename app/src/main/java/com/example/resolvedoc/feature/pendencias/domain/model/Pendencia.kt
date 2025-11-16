package com.example.resolvedoc.feature.pendencias.domain.model

data class Pendencia(
    val id: String = "",
    val medico: String = "",
    val tipo: String = "",
    val descricao: String = "",
    val unidade: String = "",
    val status: String = "Aberto",
    val enviadoEm: Long,
    val resolvidoEm: Long? = null
)
