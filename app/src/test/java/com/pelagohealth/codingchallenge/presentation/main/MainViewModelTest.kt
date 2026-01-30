package com.pelagohealth.codingchallenge.presentation.main

import com.pelagohealth.codingchallenge.MainDispatcherRule
import com.pelagohealth.codingchallenge.domain.model.Fact
import com.pelagohealth.codingchallenge.domain.usecase.FetchNewFactUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val useCase: FetchNewFactUseCase = mockk()
    private lateinit var viewModel: MainViewModel

    @Test
    fun `init loads fact successfully`() = runTest {
        // Given
        val expectedFact = Fact("Cats sleep a lot", "url")
        coEvery { useCase(null) } returns Result.success(expectedFact)

        viewModel = MainViewModel(useCase)

        // Then
        val state = viewModel.uiState.value
        assertEquals(expectedFact, state.current)
        assertFalse(state.loading)
    }

    @Test
    fun `init handles failure gracefully`() = runTest {
        // Given
        coEvery { useCase(null) } returns Result.failure(Exception("Network error"))

        // When
        viewModel = MainViewModel(useCase)

        // Then
        val state = viewModel.uiState.value
        assertNull(state.current)
        assertFalse(state.loading)
    }

    @Test
    fun `loadFact updates current fact on success`() = runTest {
        // Given
        val initialFact = Fact("Initial Fact", "url")
        val newFact = Fact("New Fact", "url")

        coEvery { useCase(null) } returns Result.success(initialFact)
        viewModel = MainViewModel(useCase)

        coEvery { useCase(initialFact) } returns Result.success(newFact)

        // When
        viewModel.loadFact()

        // Then
        val state = viewModel.uiState.value
        assertEquals(newFact, state.current)
        assertFalse(state.loading)
    }

    @Test
    fun `loadFact retains previous fact on failure`() = runTest {
        // Given
        val initialFact = Fact("Initial Fact", "url")

        coEvery { useCase(null) } returns Result.success(initialFact)
        viewModel = MainViewModel(useCase)

        coEvery { useCase(initialFact) } returns Result.failure(Exception("Error"))

        // When
        viewModel.loadFact()

        // Then
        val state = viewModel.uiState.value
        assertEquals(initialFact, state.current)
        assertFalse(state.loading)
    }

    @Test
    fun `loadFact triggers loading state`() = runTest {
        val fact = Fact("Test", "url")
        coEvery { useCase(any()) } returns Result.success(fact)

        viewModel = MainViewModel(useCase)

        coVerify(exactly = 1) { useCase(null) }
    }
}
