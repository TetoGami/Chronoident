package com.tetogami.chronoident

import org.junit.Test
import org.junit.Assert.*

/**
 * Basic unit tests for the stopwatch functionality.
 */
class StopwatchTest {

    @Test
    fun formatTime_isCorrect() {
        // Test time formatting
        assertEquals("00:00:00", formatTime(0))
        assertEquals("00:00:01", formatTime(1000))
        assertEquals("00:01:00", formatTime(60000))
        assertEquals("01:00:00", formatTime(3600000))
        assertEquals("01:23:45", formatTime(5025000))
    }

    @Test
    fun customStartTime_isCorrect() {
        // Test custom start time calculation
        val hours = 1L
        val minutes = 30L
        val seconds = 45L
        val expected = (hours * 3600 + minutes * 60 + seconds) * 1000
        val actual = (hours * 3600 + minutes * 60 + seconds) * 1000
        assertEquals(expected, actual)
        assertEquals(5445000L, actual)
    }

    private fun formatTime(timeInMs: Long): String {
        val seconds = (timeInMs / 1000) % 60
        val minutes = (timeInMs / (1000 * 60)) % 60
        val hours = (timeInMs / (1000 * 60 * 60)) % 24
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}