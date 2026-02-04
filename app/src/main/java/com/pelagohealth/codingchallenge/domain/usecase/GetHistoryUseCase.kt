package com.pelagohealth.codingchallenge.domain.usecase

import com.pelagohealth.codingchallenge.domain.model.Fact
import com.pelagohealth.codingchallenge.domain.repository.FactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val repository: FactRepository
) {
    operator fun invoke(): Flow<Result<List<Fact>>> = repository.getHistoryStream()
        .map { list ->
            Result.success(list)
        }
        .catch { exception ->
            emit(Result.failure(exception))
        }
}
