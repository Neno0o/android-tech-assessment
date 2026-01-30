package com.pelagohealth.codingchallenge.presentation.history

import com.pelagohealth.codingchallenge.MainDispatcherRule
import com.pelagohealth.codingchallenge.domain.model.Fact
import com.pelagohealth.codingchallenge.domain.usecase.GetHistoryUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getHistoryUseCase: GetHistoryUseCase = mockk()
    private lateinit var viewModel: HistoryViewModel

    @Test
    fun `state starts with loading true and null facts`() = runTest {
        // Given
        every { getHistoryUseCase() } returns MutableStateFlow(emptyList())

        // When
        viewModel = HistoryViewModel(getHistoryUseCase)

        // Then
        val state = viewModel.state.value
        assertTrue("Loading should be true initially", state.loading)
        assertNull("Facts should be null initially", state.facts)
    }

    @Test
    fun `state updates with facts when use case emits data`() = runTest {
        // Given
        val expectedFacts = listOf(Fact("Fact 1", "url"), Fact("Fact 2", "url"))
        every { getHistoryUseCase() } returns flowOf(expectedFacts)

        viewModel = HistoryViewModel(getHistoryUseCase)

        // When
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect()
        }

        // Then
        val state = viewModel.state.value
        assertEquals(expectedFacts, state.facts)
        assertFalse("Loading should be false after data loads", state.loading)
    }

    @Test
    fun `state updates correctly when history is empty`() = runTest {
        // Given
        every { getHistoryUseCase() } returns flowOf(emptyList())

        viewModel = HistoryViewModel(getHistoryUseCase)

        // When
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect()
        }

        // Then
        val state = viewModel.state.value
        assertTrue("Facts list should be empty", state.facts!!.isEmpty())
        assertFalse(state.loading)
    }
}
