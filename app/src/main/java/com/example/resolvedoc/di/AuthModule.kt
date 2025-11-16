package com.example.resolvedoc.di

import com.example.resolvedoc.feature.auth.data.AuthRepositoryImpl
import com.example.resolvedoc.feature.auth.domain.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

abstract class AuthModule {

    @Binds
    @Singleton

    abstract fun bindAuthRepository(
        repositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}