package com.example.resolvedoc.feature.auth.data

import com.example.resolvedoc.feature.auth.domain.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine



class AuthRepositoryImpl @Inject constructor(private val auth: FirebaseAuth) : AuthRepository {

    override suspend fun login(email: String, password: String): Boolean = suspendCoroutine { cont ->
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                cont.resume(true)
            }
            .addOnFailureListener {

                cont.resumeWith(Result.failure(it))
            }
    }

    override fun getCurrentUserId(): String? = auth.currentUser?.uid
    override suspend fun logout() { auth.signOut() }
    }

