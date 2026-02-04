package com.pelagohealth.codingchallenge.domain.usecase

import android.util.Log
import com.pelagohealth.codingchallenge.domain.model.Fact
import com.pelagohealth.codingchallenge.domain.repository.FactRepository
import javax.inject.Inject

class FetchNewFactUseCase
@Inject constructor(
    private val repository: FactRepository
) {
    suspend operator fun invoke(currentFact: Fact?): Result<Fact> {
        return repository.fetchFactFromApi().map { newFact ->
            if (currentFact != null) {
                try {
                    repository.saveToHistory(currentFact)
                } catch (e: Exception) {
                    Log.e("FetchNewFactUseCase", "Failed to save history: $e")
                }
            }

            newFact
        }
    }
}
