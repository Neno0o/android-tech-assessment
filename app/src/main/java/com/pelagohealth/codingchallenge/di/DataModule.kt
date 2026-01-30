package com.pelagohealth.codingchallenge.di

import com.pelagohealth.codingchallenge.data.repository.FactRepositoryImpl
import com.pelagohealth.codingchallenge.domain.repository.FactRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindFactRepository(
        factRepositoryImpl: FactRepositoryImpl,
    ): FactRepository
}
