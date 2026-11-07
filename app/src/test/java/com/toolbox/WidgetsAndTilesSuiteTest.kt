package com.toolbox

import com.toolbox.core.persistence.AppBackupData
import com.toolbox.core.persistence.BackupCounterItem
import com.toolbox.core.persistence.BackupNoteItem
import com.toolbox.core.shortcuts.FavoriteShortcutManager
import com.toolbox.dashboard.allTools
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WidgetsAndTilesSuiteTest {

    @Test
    fun testPredefinedShortcutsCountAndValidity() {
        val shortcuts = FavoriteShortcutManager.predefinedShortcuts
        assertTrue("Must contain at least 20 predefined shortcuts", shortcuts.size >= 20)

        // All shortcut tool IDs should resolve to valid registered tools
        val registeredToolIds = allTools.map { it.id }.toSet()
        for (shortcut in shortcuts) {
            assertTrue(
                "Shortcut toolId ${shortcut.toolId} should be in allTools",
                shortcut.toolId in registeredToolIds,
            )
            assertTrue("Shortcut title cannot be blank", shortcut.title.isNotBlank())
            assertTrue("Shortcut subtitle cannot be blank", shortcut.subtitle.isNotBlank())
        }
    }

    @Test
    fun testNewManagerToolsRegisteredInAllTools() {
        val toolIds = allTools.map { it.id }.toSet()
        assertTrue("widgets_manager should be registered", "widgets_manager" in toolIds)
        assertTrue("quick_tiles_manager should be registered", "quick_tiles_manager" in toolIds)
        assertTrue("shortcuts_manager should be registered", "shortcuts_manager" in toolIds)
    }

    @Test
    fun testBackupDataSerializationRoundTrip() {
        val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

        val backup = AppBackupData(
            version = 1,
            exportedAt = 123456789L,
            themeMode = "Dark",
            natureTheme = "Forest",
            themeMatchedIcons = true,
            categorySortOrder = "AlphabeticalAsc",
            toolSortOrder = "FavoritesFirst",
            favoriteToolIds = listOf("flashlight", "calculator", "level"),
            pinnedToolIds = listOf("flashlight"),
            counters = listOf(BackupCounterItem("Workout Reps", 42)),
            notes = listOf(BackupNoteItem("n1", "Meeting Notes\nDiscuss sprint goals", 123456L)),
            measurementLogCsv = "timestamp,tool,value\n12345,level,0.0",
        )

        val serialized = json.encodeToString(backup)
        assertNotNull(serialized)

        val deserialized = json.decodeFromString<AppBackupData>(serialized)
        assertEquals("Dark", deserialized.themeMode)
        assertEquals("Forest", deserialized.natureTheme)
        assertEquals(3, deserialized.favoriteToolIds.size)
        assertEquals(1, deserialized.counters.size)
        assertEquals(42, deserialized.counters[0].value)
        assertEquals("Workout Reps", deserialized.counters[0].name)
        assertEquals(1, deserialized.notes.size)
        assertEquals("Meeting Notes\nDiscuss sprint goals", deserialized.notes[0].body)
    }
}
