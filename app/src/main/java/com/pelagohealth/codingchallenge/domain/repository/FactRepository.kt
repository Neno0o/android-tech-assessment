package com.pelagohealth.codingchallenge.domain.repository

import com.pelagohealth.codingchallenge.domain.model.Fact
import kotlinx.coroutines.flow.Flow

interface FactRepository {

    fun getHistoryStream(): Flow<List<Fact>>

    suspend fun fetchFactFromApi(): Result<Fact>

    suspend fun saveToHistory(fact: Fact)
}
