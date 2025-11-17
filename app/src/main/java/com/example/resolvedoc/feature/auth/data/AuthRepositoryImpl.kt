package com.example.resolvedoc.feature.auth.data

import com.example.resolvedoc.feature.auth.domain.AuthRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override suspend fun login(email: String, password: String): Boolean {
        auth.signInWithEmailAndPassword(email, password).await()
        return true
    }

    override fun getCurrentUserId(): String? = auth.currentUser?.uid

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ) {
        val user = auth.currentUser
            ?: throw Exception("Usuário não está logado.")

        val email = user.email
            ?: throw Exception("Usuário não possui e-mail cadastrado.")


        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        try {
            user.reauthenticate(credential).await()
        } catch (e: Exception) {

            throw Exception("Senha atual incorreta.")
        }


        try {
            user.updatePassword(newPassword).await()
        } catch (e: Exception) {
            throw Exception("Erro ao atualizar senha: ${e.message}")
        }
    }
}
