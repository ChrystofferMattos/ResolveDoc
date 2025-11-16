package com.example.resolvedoc.di

import com.example.resolvedoc.feature.pendencias.data.repository.PendenciaRepositoryImpl
import com.example.resolvedoc.feature.pendencias.domain.repository.PendenciaRepository
import com.example.resolvedoc.feature.pendencias.domain.usecase.GetPendenciasUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dagger.Binds

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth


    @Provides
    fun provideGetPendenciasUseCase(repo: PendenciaRepository) = GetPendenciasUseCase(repo)

    @Module
    @InstallIn(SingletonComponent::class)
    @Suppress("UNUSED")
    abstract class RepositoryModule {

        @Binds
        @Singleton
        @Suppress("UNUSED")
        abstract fun bindPendenciaRepository(
            repositoryImpl: PendenciaRepositoryImpl
        ): PendenciaRepository
    }

}
