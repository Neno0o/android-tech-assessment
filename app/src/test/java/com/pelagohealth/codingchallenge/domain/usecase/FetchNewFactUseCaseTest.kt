package com.pelagohealth.codingchallenge.domain.usecase

import com.pelagohealth.codingchallenge.domain.model.Fact
import com.pelagohealth.codingchallenge.domain.repository.FactRepository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FetchNewFactUseCaseTest {

    private val repository: FactRepository = mockk(relaxed = true)

    private lateinit var useCase: FetchNewFactUseCase

    @Before
    fun setup() {
        useCase = FetchNewFactUseCase(repository)
    }

    @Test
    fun `invoke saves previous fact to history when API call succeeds`() = runTest {
        // Given
        val previousFact = Fact("Old Fact", "url")
        val newFact = Fact("New Fact", "url")

        coEvery { repository.fetchFactFromApi() } returns Result.success(newFact)

        // When
        val result = useCase(previousFact)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(newFact, result.getOrNull())

        coVerify(exactly = 1) { repository.saveToHistory(previousFact) }
    }

    @Test
    fun `invoke does NOT save to history if previous fact is null (initial load)`() = runTest {
        // Given
        val newFact = Fact("New Fact", "url")
        coEvery { repository.fetchFactFromApi() } returns Result.success(newFact)

        // When
        val result = useCase(null)

        // Then
        assertTrue(result.isSuccess)

        // Verify
        coVerify(exactly = 0) { repository.saveToHistory(any()) }
    }

    @Test
    fun `invoke does NOT save to history when API call fails`() = runTest {
        // Given
        val previousFact = Fact("Old Fact", "url")
        val exception = Exception("Network Error")

        coEvery { repository.fetchFactFromApi() } returns Result.failure(exception)

        // When
        val result = useCase(previousFact)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 0) { repository.saveToHistory(any()) }
    }
}
