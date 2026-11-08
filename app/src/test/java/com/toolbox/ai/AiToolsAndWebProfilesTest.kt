package com.toolbox.ai

import com.toolbox.dashboard.ToolCategory
import com.toolbox.dashboard.allTools
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AiToolsAndWebProfilesTest {

    @Test
    fun testAiToolsCategoryRegistration() {
        val aiTools = allTools.filter { it.category == ToolCategory.AiTools }
        assertTrue("AI Tools category should have at least 5 tools", aiTools.size >= 5)

        val aiToolIds = aiTools.map { it.id }.toSet()
        assertTrue(aiToolIds.contains("perchance_ai"))
        assertTrue(aiToolIds.contains("perchance_text_gen"))
        assertTrue(aiToolIds.contains("perchance_photo_gen"))
        assertTrue(aiToolIds.contains("perchance_chat"))
        assertTrue(aiToolIds.contains("huggingface_ai"))

        val category = ToolCategory.AiTools
        assertEquals("AI tools", category.label)
    }

    @Test
    fun testAiToolsDefinitions() {
        val hfTool = allTools.first { it.id == "huggingface_ai" }
        assertEquals("Hugging Face AI", hfTool.name)
        assertEquals(ToolCategory.AiTools, hfTool.category)
        assertTrue(hfTool.searchKeywords.contains("huggingface"))
        assertTrue(hfTool.searchKeywords.contains("models"))

        val textTool = allTools.first { it.id == "perchance_text_gen" }
        assertEquals("Perchance Text", textTool.name)
        assertEquals(ToolCategory.AiTools, textTool.category)

        val photoTool = allTools.first { it.id == "perchance_photo_gen" }
        assertEquals("Perchance Photo", photoTool.name)
        assertEquals(ToolCategory.AiTools, photoTool.category)

        val chatTool = allTools.first { it.id == "perchance_chat" }
        assertEquals("Perchance Chat", chatTool.name)
        assertEquals(ToolCategory.AiTools, chatTool.category)
    }

    @Test
    fun testWebProfileManagerDefaults() {
        val profiles = WebProfileManager.predefinedProfiles
        assertEquals(4, profiles.size)

        val defaultProfile = profiles.first { it.id == "default" }
        assertTrue(defaultProfile.isDefault)
        assertFalse(defaultProfile.isIncognito)

        val anonymousProfile = profiles.first { it.id == "anonymous" }
        assertTrue(anonymousProfile.isIncognito)
        assertEquals("Incognito / Private", anonymousProfile.name)

        val workProfile = profiles.first { it.id == "work" }
        assertEquals("Work / Projects", workProfile.name)

        val personalProfile = profiles.first { it.id == "personal" }
        assertEquals("Personal", personalProfile.name)
    }

    @Test
    fun testCustomWebProfileCreation() {
        val customProfile = WebProfile(
            id = "custom_test_123",
            name = "Research Lab",
            description = "AI Research and benchmark profile",
            isIncognito = true,
            customUserAgent = "CustomAgent/1.0",
        )

        assertEquals("custom_test_123", customProfile.id)
        assertEquals("Research Lab", customProfile.name)
        assertTrue(customProfile.isIncognito)
        assertEquals("CustomAgent/1.0", customProfile.customUserAgent)
    }
}
