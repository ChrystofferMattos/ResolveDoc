package com.example.resolvedoc.feature.pendencias.data.repository

import com.example.resolvedoc.feature.pendencias.data.model.PendenciaDto
import com.example.resolvedoc.feature.pendencias.domain.model.Pendencia
import com.example.resolvedoc.feature.pendencias.domain.repository.PendenciaRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PendenciaRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PendenciaRepository {

    private val collection = firestore.collection("pendencias")


    override fun getPendenciasStream(): Flow<List<Pendencia>> = callbackFlow {
        val subscription = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val list = snapshot?.documents?.mapNotNull { doc ->
                val dto = doc.toObject(PendenciaDto::class.java)
                dto?.toDomain(doc.id)
            } ?: emptyList()

            trySend(list)
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun createPendencia(p: Pendencia) {
        val dto = PendenciaDto(
            medico = p.medico,
            tipo = p.tipo,
            descricao = p.descricao,
            unidade = p.unidade,
            status = p.status,
            enviadoEm = p.enviadoEm,
            resolvidoEm = p.resolvidoEm
        )

        collection.add(dto).await()
    }

    override suspend fun getPendenciaById(id: String): Pendencia? {
        return try {
            val doc = collection.document(id).get().await()
            if (doc.exists()) {
                val dto = doc.toObject(PendenciaDto::class.java)
                dto?.toDomain(doc.id)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun markAsResolved(id: String) {
        val updates = mapOf(
            "status" to "Resolvida",
            "resolvidoEm" to System.currentTimeMillis()
        )
        collection.document(id).update(updates).await()
    }
}
