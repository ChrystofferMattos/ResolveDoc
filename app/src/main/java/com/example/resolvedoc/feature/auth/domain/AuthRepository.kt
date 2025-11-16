package com.example.resolvedoc.feature.auth.domain

interface AuthRepository {
    suspend fun login(email: String, password: String): Boolean
    fun getCurrentUserId(): String?
    suspend fun logout()
    suspend fun changePassword(currentPassword: String, newPassword: String)
}
