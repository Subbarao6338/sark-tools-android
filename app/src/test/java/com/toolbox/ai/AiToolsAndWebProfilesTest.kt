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
        assertTrue("AI Tools category should have at least 15 tools", aiTools.size >= 15)

        val aiToolIds = aiTools.map { it.id }.toSet()
        assertTrue(aiToolIds.contains("perchance_ai"))
        assertTrue(aiToolIds.contains("perchance_text_gen"))
        assertTrue(aiToolIds.contains("perchance_photo_gen"))
        assertTrue(aiToolIds.contains("perchance_chat"))
        assertTrue(aiToolIds.contains("perchance_video_gen"))
        assertTrue(aiToolIds.contains("perchance_story_gen"))
        assertTrue(aiToolIds.contains("perchance_character_gen"))
        assertTrue(aiToolIds.contains("perchance_anime_gen"))
        assertTrue(aiToolIds.contains("perchance_rpg_gen"))
        assertTrue(aiToolIds.contains("perchance_writing_prompt"))
        assertTrue(aiToolIds.contains("perchance_lore_gen"))
        assertTrue(aiToolIds.contains("perchance_dialogue_gen"))
        assertTrue(aiToolIds.contains("perchance_voice_actor"))
        assertTrue(aiToolIds.contains("perchance_art_plugin"))
        assertTrue(aiToolIds.contains("perchance_name_gen"))
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

        val videoTool = allTools.first { it.id == "perchance_video_gen" }
        assertEquals("Perchance Video", videoTool.name)
        assertEquals(ToolCategory.AiTools, videoTool.category)
        assertTrue(videoTool.searchKeywords.contains("video"))
        assertTrue(videoTool.searchKeywords.contains("generator"))

        val storyTool = allTools.first { it.id == "perchance_story_gen" }
        assertEquals("Perchance Story", storyTool.name)
        assertEquals(ToolCategory.AiTools, storyTool.category)

        val charTool = allTools.first { it.id == "perchance_character_gen" }
        assertEquals("Perchance Character", charTool.name)
        assertEquals(ToolCategory.AiTools, charTool.category)

        val animeTool = allTools.first { it.id == "perchance_anime_gen" }
        assertEquals("Perchance Anime", animeTool.name)
        assertEquals(ToolCategory.AiTools, animeTool.category)

        val rpgTool = allTools.first { it.id == "perchance_rpg_gen" }
        assertEquals("Perchance RPG", rpgTool.name)
        assertEquals(ToolCategory.AiTools, rpgTool.category)
    }

    @Test
    fun testPerchanceVideoGenerationPrompt() {
        val prompt = PerchanceGeneratorEngine.generateAiVideoPrompt()
        assertTrue(prompt.isNotBlank())
        assertTrue(prompt.contains("Scene:"))
        assertTrue(prompt.contains("Camera Dynamics:"))
        assertTrue(prompt.contains("Lighting & Render:"))
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

    @Test
    fun testPwaWebToolsCapabilities() {
        // Validate PWA Web tools keys
        val supportedWebTools = listOf(
            "perchance_text_gen",
            "perchance_photo_gen",
            "perchance_chat",
            "perchance_video_gen",
            "perchance_story_gen",
            "perchance_character_gen",
            "perchance_anime_gen",
            "perchance_rpg_gen",
            "perchance_writing_prompt",
            "perchance_lore_gen",
            "perchance_dialogue_gen",
            "perchance_voice_actor",
            "perchance_art_plugin",
            "perchance_name_gen",
            "huggingface_ai",
        )

        for (toolId in supportedWebTools) {
            val tool = allTools.firstOrNull { it.id == toolId }
            assertNotNull("Web tool $toolId should exist in allTools", tool)
            assertTrue("Web tool description should not be blank", tool!!.description.isNotBlank())
        }
    }
}
