package com.toolbox.dashboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UniqueToolIconsTest {

    @Test
    fun testAllToolsHaveUniqueIcons() {
        val totalTools = allTools.size
        val uniqueIcons = allTools.map { it.icon }.toSet()
        val duplicateIcons = allTools
            .groupBy { it.icon }
            .filter { it.value.size > 1 }

        assertTrue(
            "Found duplicate icons across tools: ${duplicateIcons.map { "${it.key}: ${it.value.map { t -> t.id }}" }}",
            duplicateIcons.isEmpty()
        )
        assertEquals("Every single tool must have a completely unique icon", totalTools, uniqueIcons.size)
    }
}
