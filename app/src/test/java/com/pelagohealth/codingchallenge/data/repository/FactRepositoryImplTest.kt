package com.pelagohealth.codingchallenge.data.repository

import com.pelagohealth.codingchallenge.data.datasource.rest.FactDto
import com.pelagohealth.codingchallenge.data.datasource.rest.FactsRestApi
import com.pelagohealth.codingchallenge.domain.model.Fact
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class FactRepositoryImplTest {

    private val api: FactsRestApi = mockk()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: FactRepositoryImpl

    @Before
    fun setup() {
        repository = FactRepositoryImpl(api, testDispatcher)
    }

    @Test
    fun `fetchFactFromApi returns success when API call succeeds`() = runTest {
        // Given
        val expectedText = "Cats are liquid"
        val mockDto =
            FactDto(id = "1", text = expectedText, sourceUrl = "sourceUrl")
        coEvery { api.getFact() } returns mockDto

        // When
        val result = repository.fetchFactFromApi()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedText, result.getOrNull()?.text)
    }

    @Test
    fun `fetchFactFromApi returns failure when API throws exception`() = runTest {
        // Given
        coEvery { api.getFact() } throws IOException("Network error")

        // When
        val result = repository.fetchFactFromApi()

        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `saveToHistory adds new fact to the top of the list`() = runTest {
        // Given
        val fact1 = Fact("Fact 1", "url")
        val fact2 = Fact("Fact 2", "url")

        // When
        repository.saveToHistory(fact1)
        repository.saveToHistory(fact2)

        // Then
        val history = repository.getHistoryStream().first()
        assertEquals(2, history.size)
        assertEquals("Fact 2", history[0].text)
        assertEquals("Fact 1", history[1].text)
    }

    @Test
    fun `saveToHistory limits history to 10 items`() = runTest {
        repeat(15) { index ->
            repository.saveToHistory(Fact("Fact #$index", "url"))
        }

        // When
        val history = repository.getHistoryStream().first()

        // Then
        assertEquals(10, history.size)
        assertEquals("Fact #14", history[0].text)
        assertEquals("Fact #5", history[9].text)
    }

    @Test
    fun `getHistoryStream starts empty`() = runTest {
        // When
        val history = repository.getHistoryStream().first()

        // Then
        assertTrue(history.isEmpty())
    }
}
