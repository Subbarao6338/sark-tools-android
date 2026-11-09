package com.toolbox.dashboard

import com.toolbox.apptools.AppToolsRegistry
import com.toolbox.core.persistence.CategorySortOrder
import com.toolbox.devtools.DevToolsRegistry
import com.toolbox.filestools.FilesToolsRegistry
import com.toolbox.healthfitness.HealthFitnessRegistry
import com.toolbox.imagetools.ImageToolsRegistry
import com.toolbox.networktools.NetworkToolsRegistry
import com.toolbox.programmingtools.ProgrammingToolsRegistry
import com.toolbox.securityprivacy.SecurityPrivacyRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ModularCategoriesSuiteTest {

    @Test
    fun testNewToolCategoriesExist() {
        val expectedLabels = mapOf(
            ToolCategory.AppTools to "App tools",
            ToolCategory.DevTools to "Dev tools",
            ToolCategory.ProgrammingTools to "Programming tools",
            ToolCategory.FilesTools to "Files tools",
            ToolCategory.NetworkTools to "Network tools",
            ToolCategory.HealthFitness to "Health & Fitness",
            ToolCategory.SecurityPrivacy to "Security & Privacy",
        )

        for ((category, label) in expectedLabels) {
            assertTrue("Category $category should be defined", ToolCategory.entries.contains(category))
            assertEquals(label, category.label)
        }
    }

    @Test
    fun testToolsAssignedToNewCategories() {
        // App tools
        val appTools = allTools.filter { it.category == ToolCategory.AppTools }.map { it.id }.toSet()
        assertTrue(appTools.contains("widgets_manager"))
        assertTrue(appTools.contains("quick_tiles_manager"))
        assertTrue(appTools.contains("shortcuts_manager"))

        // Dev tools
        val devTools = allTools.filter { it.category == ToolCategory.DevTools }.map { it.id }.toSet()
        assertTrue(devTools.contains("json_formatter"))
        assertTrue(devTools.contains("uuid_generator"))
        assertTrue(devTools.contains("regex_tester"))
        assertTrue(devTools.contains("html_entity_tool"))

        // Programming tools
        val progTools = allTools.filter { it.category == ToolCategory.ProgrammingTools }.map { it.id }.toSet()
        assertTrue(progTools.contains("hash_encode"))
        assertTrue(progTools.contains("number_base"))
        assertTrue(progTools.contains("binary_bitwise"))

        // Files tools
        val filesTools = allTools.filter { it.category == ToolCategory.FilesTools }.map { it.id }.toSet()
        assertTrue(filesTools.contains("storage_analyzer"))
        assertTrue(filesTools.contains("duplicate_photos"))

        // Network tools
        val networkTools = allTools.filter { it.category == ToolCategory.NetworkTools }.map { it.id }.toSet()
        assertTrue(networkTools.contains("wifi_signal"))
        assertTrue(networkTools.contains("network_info"))
        assertTrue(networkTools.contains("wifi_qr_share"))
        assertTrue(networkTools.contains("network_speed_test"))
        assertTrue(networkTools.contains("web_tools"))

        // Image tools (including moved media converter tools)
        val imageTools = allTools.filter { it.category == ToolCategory.ImageTools }.map { it.id }.toSet()
        assertTrue(imageTools.contains("image_resizer"))
        assertTrue(imageTools.contains("image_format_converter"))
        assertTrue(imageTools.contains("color_palette_gen"))

        // Health & Fitness tools
        val healthTools = allTools.filter { it.category == ToolCategory.HealthFitness }.map { it.id }.toSet()
        assertTrue(healthTools.contains("water_intake_calc"))
        assertTrue(healthTools.contains("body_fat_calc"))
        assertTrue(healthTools.contains("target_heart_rate"))
        assertTrue(healthTools.contains("macro_calc"))
        assertTrue(healthTools.contains("pace_calc"))

        // Security & Privacy tools
        val securityTools = allTools.filter { it.category == ToolCategory.SecurityPrivacy }.map { it.id }.toSet()
        assertTrue(securityTools.contains("password_audit"))
        assertTrue(securityTools.contains("aes_encryption"))
        assertTrue(securityTools.contains("checksum_verifier"))
        assertTrue(securityTools.contains("steganography"))
        assertTrue(securityTools.contains("uuid_validator"))
    }

    @Test
    fun testModularRegistriesIntegrity() {
        assertNotNull(AppToolsRegistry)
        assertNotNull(DevToolsRegistry)
        assertNotNull(ProgrammingToolsRegistry)
        assertNotNull(FilesToolsRegistry)
        assertNotNull(NetworkToolsRegistry)
        assertNotNull(ImageToolsRegistry)
        assertNotNull(HealthFitnessRegistry)
        assertNotNull(SecurityPrivacyRegistry)
    }

    @Test
    fun testCategoryOrderingFromBeginning() {
        val rawGrouped = allTools.groupBy { it.category }
        val sortedDefault = ToolCategory.entries.filter { it in rawGrouped }
        // Verify default category ordering starts with the first defined categories and not Sensors
        assertEquals(ToolCategory.UnitConverters, sortedDefault.first())
        assertTrue("Sensors should not be the first category", sortedDefault.first() != ToolCategory.Sensors)

        // Verify sorted tab strip categories
        val sortedAsc = ToolCategory.entries.sortedBy { it.label }
        val sortedDesc = ToolCategory.entries.sortedByDescending { it.label }
        assertEquals(sortedAsc.first().label, sortedDesc.last().label)
    }
}
