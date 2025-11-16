package com.example.resolvedoc.feature.pendencias.data.repository

import com.example.resolvedoc.feature.pendencias.domain.model.Pendencia
import com.example.resolvedoc.feature.pendencias.domain.repository.PendenciaRepository
import com.example.resolvedoc.feature.pendencias.data.model.PendenciaDto
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
        val dto = PendenciaDto(p.medico, p.tipo, p.descricao, p.unidade, p.status, p.enviadoEm, p.resolvidoEm)

        collection.add(dto).await()
    }
}
