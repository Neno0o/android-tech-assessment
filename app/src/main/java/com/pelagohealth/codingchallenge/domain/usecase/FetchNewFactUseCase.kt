package com.pelagohealth.codingchallenge.domain.usecase

import com.pelagohealth.codingchallenge.domain.model.Fact
import com.pelagohealth.codingchallenge.domain.repository.FactRepository
import javax.inject.Inject

class FetchNewFactUseCase @Inject constructor(
    private val repository: FactRepository
) {

    suspend operator fun invoke(currentFact: Fact?): Result<Fact> {
        val result = repository.fetchFactFromApi()

        result.onSuccess {
            if (currentFact != null) {
                repository.saveToHistory(currentFact)
            }
        }

        return result
    }
}
