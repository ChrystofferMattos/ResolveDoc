package com.example.resolvedoc.feature.auth.data

import com.example.resolvedoc.feature.auth.domain.AuthRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    // LOGIN
    override suspend fun login(email: String, password: String): Boolean =
        suspendCoroutine { cont ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    cont.resume(true)
                }
                .addOnFailureListener { e ->
                    cont.resumeWithException(e)
                }
        }

    override fun getCurrentUserId(): String? =
        auth.currentUser?.uid

    override suspend fun logout() {
        auth.signOut()
    }
    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ) {
        val user = auth.currentUser ?: throw Exception("Usuário não está logado.")

        val email = user.email ?: throw Exception("Usuário não possui email para reautenticação.")


        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        suspendCoroutine<Unit> { cont ->
            user.reauthenticate(credential)
                .addOnSuccessListener {

                    user.updatePassword(newPassword)
                        .addOnSuccessListener {
                            cont.resume(Unit)
                        }
                        .addOnFailureListener { e ->
                            cont.resumeWithException(e)
                        }
                }
                .addOnFailureListener { e ->
                    cont.resumeWithException(e)
                }
        }
    }
}
