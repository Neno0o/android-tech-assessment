package com.pelagohealth.codingchallenge.data.repository

import com.pelagohealth.codingchallenge.data.datasource.rest.FactsRestApi
import com.pelagohealth.codingchallenge.data.mapper.toDomain
import com.pelagohealth.codingchallenge.di.IoDispatcher
import com.pelagohealth.codingchallenge.domain.model.Fact
import com.pelagohealth.codingchallenge.domain.repository.FactRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Repository providing random facts.
 */
class FactRepositoryImpl @Inject constructor(
    private val api: FactsRestApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : FactRepository {

    private val _history = MutableStateFlow<List<Fact>>(emptyList())

    override fun getHistoryStream(): Flow<List<Fact>> = _history.asStateFlow()

    override suspend fun fetchFactFromApi(): Result<Fact> = withContext(ioDispatcher) {
        try {
            val dto = api.getFact()
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveToHistory(fact: Fact) {
        _history.update { currentList ->
            (listOf(fact) + currentList).take(10)
        }
    }
}
