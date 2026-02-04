package com.pelagohealth.codingchallenge.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelagohealth.codingchallenge.domain.model.Fact
import com.pelagohealth.codingchallenge.domain.usecase.GetHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    getHistoryUseCase: GetHistoryUseCase
) : ViewModel() {

    val state: StateFlow<HistoryScreenState> = getHistoryUseCase()
        .map { result ->
            result.fold(
                onSuccess = { historyList ->
                    HistoryScreenState(facts = historyList, loading = false)
                },
                onFailure = {
                    HistoryScreenState(facts = emptyList(), loading = false)
                }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HistoryScreenState(loading = true)
        )

    data class HistoryScreenState(
        val facts: List<Fact>? = null,
        val loading: Boolean = false,
    )
}