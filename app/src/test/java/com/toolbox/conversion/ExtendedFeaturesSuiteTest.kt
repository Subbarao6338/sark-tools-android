package com.toolbox.conversion

import com.toolbox.core.persistence.CategorySortOrder
import com.toolbox.core.persistence.ToolSortOrder
import com.toolbox.dashboard.ToolCategory
import com.toolbox.dashboard.allTools
import com.toolbox.datetime.moonphase.calculateMoonPhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ExtendedFeaturesSuiteTest {

    @Test
    fun testMoonPhaseCalculation() {
        val newMoonDate = LocalDate.of(2024, 1, 11)
        val phaseAtNew = calculateMoonPhase(newMoonDate)
        assertEquals("New Moon", phaseAtNew.phaseName)
        assertEquals(0, phaseAtNew.illuminationPercent)

        // Halfway through synodic cycle should be full moon
        val fullMoonDate = LocalDate.of(2024, 1, 25)
        val phaseAtFull = calculateMoonPhase(fullMoonDate)
        assertTrue(phaseAtFull.phaseName.contains("Gibbous") || phaseAtFull.phaseName.contains("Full"))
        assertTrue(phaseAtFull.illuminationPercent > 90)
    }

    @Test
    fun testDateTimeAndSocialCategoriesExist() {
        val dateTimeTools = allTools.filter { it.category == ToolCategory.DateTime }
        assertTrue("DateTime category should contain tools", dateTimeTools.isNotEmpty())
        assertTrue(dateTimeTools.any { it.id == "time_diff" })
        assertTrue(dateTimeTools.any { it.id == "moon_phase" })
        assertTrue(dateTimeTools.any { it.id == "date_calculator" })
        assertTrue(dateTimeTools.any { it.id == "stopwatch_timer" })
        assertTrue(dateTimeTools.any { it.id == "world_clock" })

        val socialTools = allTools.filter { it.category == ToolCategory.Social }
        assertTrue("Social category should contain tools", socialTools.isNotEmpty())
        assertTrue(socialTools.any { it.id == "hashtag_generator" })
        assertTrue(socialTools.any { it.id == "social_bio" })
        assertTrue(socialTools.any { it.id == "social_preview" })
        assertTrue(socialTools.any { it.id == "quote_maker" })
        assertTrue(socialTools.any { it.id == "status_saver" })
    }

    @Test
    fun testCategoryAndToolSortOrders() {
        // Test category alphabetical sorting
        val sortedAsc = ToolCategory.entries.sortedBy { it.label }
        val sortedDesc = ToolCategory.entries.sortedByDescending { it.label }
        assertEquals(sortedAsc.first().label, sortedDesc.last().label)

        // Test tool sort
        val sortedTools = allTools.sortedBy { it.name.lowercase() }
        for (i in 0 until sortedTools.size - 1) {
            assertTrue(sortedTools[i].name.lowercase() <= sortedTools[i + 1].name.lowercase())
        }
    }
}
