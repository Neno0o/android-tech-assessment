package com.pelagohealth.codingchallenge.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelagohealth.codingchallenge.domain.model.Fact
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.pelagohealth.codingchallenge.domain.usecase.FetchNewFactUseCase
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fetchNewFactUseCase: FetchNewFactUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainScreenState())
    val uiState: StateFlow<MainScreenState> = _uiState.asStateFlow()

    init {
        loadFact()
    }

    fun loadFact() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

            val currentFact = _uiState.value.current

            fetchNewFactUseCase(currentFact)
                .onSuccess { newFact ->
                    _uiState.update { it.copy(current = newFact, loading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(loading = false) }
                }
        }
    }

    data class MainScreenState(
        val current: Fact? = null,
        val loading: Boolean = false,
    )
}
