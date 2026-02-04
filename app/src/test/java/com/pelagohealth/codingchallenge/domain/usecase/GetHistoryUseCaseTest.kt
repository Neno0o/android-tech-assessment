package com.pelagohealth.codingchallenge.domain.usecase

import com.pelagohealth.codingchallenge.domain.model.Fact
import com.pelagohealth.codingchallenge.domain.repository.FactRepository

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetHistoryUseCaseTest {

    private val repository: FactRepository = mockk()

    private val useCase = GetHistoryUseCase(repository)

    @Test
    fun `invoke wraps repository stream in Result success`() = runTest {
        // Given
        val expectedHistory = listOf(
            Fact("History Fact 1", "url"),
            Fact("History Fact 2", "url")
        )

        every { repository.getHistoryStream() } returns flowOf(expectedHistory)

        // When
        val resultFlow = useCase()
        val actualResult = resultFlow.first()

        // Then
        verify(exactly = 1) { repository.getHistoryStream() }

        // Verify
        assertEquals(expectedHistory, actualResult.getOrNull())
    }

    @Test
    fun `invoke catches exceptions and emits Result failure`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        every { repository.getHistoryStream() } returns flow { throw exception }

        // When
        val resultFlow = useCase()
        val actualResult = resultFlow.first()

        // Verify
        assertEquals(exception, actualResult.exceptionOrNull())
    }
}
